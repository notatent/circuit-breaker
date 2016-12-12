package com.tent.dubbo.circuit.breaker;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.tent.dubbo.circuit.breaker.common.Constants;
import com.tent.dubbo.circuit.breaker.common.utils.CircuitBreakerUtils;

/**
 * DefaultCommand
 *
 * @author tent.zhang
 * @param <Result>
 */
public class DefaultCommand extends HystrixCommand<Result> {

	private Invoker<?> invoker;
	private Invocation invocation;

	/**
	 * dubbo 熔断器的 Command.
	 *
	 * @param invoker
	 * @param invocation
	 */
	public DefaultCommand(Invoker<?> invoker, Invocation invocation) {
		this(configSetter(invoker, invocation));
		this.invoker = invoker;
		this.invocation = invocation;
	}

	/**
	 * Creates a new instance of DefaultCommand.
	 *
	 * @param setter
	 */
	public DefaultCommand(com.netflix.hystrix.HystrixCommand.Setter setter) {
		super(setter);
	}

	/**
	 * 设置 dubbo Command 的 Setter. <br/>
	 * 
	 * @param invoker
	 * @param invocation
	 * @return
	 */
	protected static Setter configSetter(Invoker<?> invoker, Invocation invocation) {

		// 设置 command 的 commandKey 和 groupKey
		Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
				.andCommandKey(HystrixCommandKey.Factory.asKey(invocation.getMethodName()));

		// dubbo 服务的隔离策略
		if (CircuitBreakerUtils.getValue(Constants.DUBBO_ISOLATION_STRATEGY_THREAD,
				Constants.DUBBO_ISOLATION_STRATEGY_THREAD_DEFAULT)) {
			// 线程池隔离服务资源
			setter.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
					.withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD)
					// 设置熔断器触发的条件
					.withCircuitBreakerRequestVolumeThreshold(
							CircuitBreakerUtils.getValue(Constants.DUBBO_CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD,
									Constants.DUBBO_CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD_DEFAULT))
					.withCircuitBreakerSleepWindowInMilliseconds(
							CircuitBreakerUtils.getValue(Constants.DUBBO_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS,
									Constants.DUBBO_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS_DEFAULT))
					.withCircuitBreakerErrorThresholdPercentage(
							CircuitBreakerUtils.getValue(Constants.DUBBO_CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE,
									Constants.DUBBO_CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE_DEFAULT)));
			// 线程池大小
			setter.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
					.withCoreSize(CircuitBreakerUtils.getValue(Constants.DUBBO_CIRCUIT_BREAKER_THREAD_POOL_CORE_SIZE,
							Constants.DUBBO_CIRCUIT_BREAKER_THREAD_POOL_CORE_SIZE_DEFAULT)));
		} else {
			// 信号量隔离服务资源
			setter.andCommandPropertiesDefaults(
					HystrixCommandProperties.Setter()
							.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
							// 信号量最大并发请求量
							.withExecutionIsolationSemaphoreMaxConcurrentRequests(CircuitBreakerUtils.getValue(
									Constants.DUBBO_CIRCUIT_BREAKER_MAX_CONCURRENT_REQUESTS,
									Constants.DUBBO_CIRCUIT_BREAKER_MAX_CONCURRENT_REQUESTS_DEFAULT))
							// 设置熔断器触发的条件
							.withCircuitBreakerRequestVolumeThreshold(CircuitBreakerUtils.getValue(
									Constants.DUBBO_CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD,
									Constants.DUBBO_CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD_DEFAULT))
							.withCircuitBreakerSleepWindowInMilliseconds(CircuitBreakerUtils.getValue(
									Constants.DUBBO_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS,
									Constants.DUBBO_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS_DEFAULT))
							.withCircuitBreakerErrorThresholdPercentage(CircuitBreakerUtils.getValue(
									Constants.DUBBO_CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE,
									Constants.DUBBO_CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE_DEFAULT)));
		}
		return setter;
	}

	/**
	 * dubbo 调用链的逻辑.
	 * @see com.netflix.hystrix.HystrixCommand#run()
	 */
	@Override
	protected Result run() {
		Result result = invoker.invoke(invocation);
		Throwable exception = result.getException();
		if (exception != null) {
			if (result.getException().getMessage()
					.startsWith("com.netflix.hystrix.exception.HystrixBadRequestException:")) {
				throw new HystrixBadRequestException(exception.getMessage(), exception);
			} else {
				throw new RuntimeException("dubbo provider execution error", exception);
			}
		}
		return result;
	}

	/**
	 * 优雅降级(dubbo 不提供此方法，因为invoker返回值不同。如果 dubbo要优雅降级，在 dubbo consumer处 catch
	 * RpcException 或 Exception).
	 * 
	 * @see com.netflix.hystrix.HystrixCommand#getFallback()
	 */
	/*
	 * @Override protected Result getFallback() { return super.getFallback(); }
	 */
}

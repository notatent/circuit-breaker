package com.tent.dubbo.circuit.breaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.netflix.hystrix.HystrixCommand;
import com.tent.dubbo.circuit.breaker.common.utils.CircuitBreakerUtils;

/**
 * 
 * CircuitBreakerFilter
 *
 * @author tent.zhang
 */
@Activate(group = Constants.CONSUMER)
public class CircuitBreakerFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(CircuitBreakerFilter.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		try {
			if (CircuitBreakerUtils.circuitBreakerEnabled()) {
				HystrixCommand<?> command = new DefaultCommand(invoker, invocation);
				if (CircuitBreakerUtils.asynExecution()) {
					return (Result) command.execute();
				} else {
					return (Result) command.queue().get();
				}
			} else {
				return invoker.invoke(invocation);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new RpcException(e.getMessage());
		}
	}
}
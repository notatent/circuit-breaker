package com.tent.dubbo.circuit.breaker.common.utils;

import com.netflix.config.DynamicPropertyFactory;
import com.tent.dubbo.circuit.breaker.common.Constants;

/**
 * 
 * CircuitBreakerUtils
 *
 * @author tent.zhang
 */
public class CircuitBreakerUtils {

	/**
	 * dubbo服务是否开启熔断功能. <br/>
	 * 
	 * @return
	 */
	public static boolean circuitBreakerEnabled() {
		return getValue(Constants.DUBBO_CIRCUIT_BREAKER_ENABLED, Constants.DUBBO_CIRCUIT_BREAKER_ENABLED_DEFAULT);
	}

	/**
	 * command是否同步执行. <br/>
	 * 
	 * @return
	 */
	public static boolean asynExecution() {
		return getValue(Constants.DUBBO_CIRCUIT_BREAKER_SYNCHRONOUS_EXECUTION, Constants.DUBBO_CIRCUIT_BREAKER_SYNCHRONOUS_EXECUTION_DEFAULT);
	}

	/**
	 * getValue. <br/>
	 * 
	 * @param fieldName
	 * @param defaultValue
	 * @return
	 */
	public static int getValue(String fieldName, int defaultValue) {
		return DynamicPropertyFactory.getInstance().getIntProperty(fieldName, defaultValue).get();
	}

	/**
	 * getValue. <br/>
	 * 
	 * @param fieldName
	 * @param defaultValue
	 * @return
	 */
	public static boolean getValue(String fieldName, boolean defaultValue) {
		return DynamicPropertyFactory.getInstance().getBooleanProperty(fieldName, defaultValue).get();
	}
}

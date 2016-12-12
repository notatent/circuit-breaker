package com.tent.dubbo.circuit.breaker.common;
/**
 * Constants
 *
 * @author tent.zhang
 */
public class Constants {

	// dubbo服务是否开启熔断功能
	public static final String  DUBBO_CIRCUIT_BREAKER_ENABLED           					= "dubbo.circuit.breaker.open";	
	public static final boolean DUBBO_CIRCUIT_BREAKER_ENABLED_DEFAULT						= true;
	public static final String  DUBBO_ISOLATION_STRATEGY_THREAD  							= "dubbo.isolation.strategy.thread";
	public static final boolean DUBBO_ISOLATION_STRATEGY_THREAD_DEFAULT						= true;
    public static final String  DUBBO_CIRCUIT_BREAKER_SYNCHRONOUS_EXECUTION           		= "dubbo.circuit.breaker.synchronous.execution";	
    public static final boolean DUBBO_CIRCUIT_BREAKER_SYNCHRONOUS_EXECUTION_DEFAULT      	= true;
    
    // metrics相关
    public static final String  DUBBO_CIRCUIT_BREAKER_METRICS_STREAM_PORT           		= "dubbo.circuit.breaker.metrics.stream.port";
    public static final int  	DUBBO_CIRCUIT_BREAKER_METRICS_STREAM_PORT_DEFAULT           = 7777;
    
    // 资源隔离相关字段
    public static final String	DUBBO_CIRCUIT_BREAKER_THREAD_POOL_CORE_SIZE 				= "dubbo.thread.pool.core.size";	
    public static final int  	DUBBO_CIRCUIT_BREAKER_THREAD_POOL_CORE_SIZE_DEFAULT 		= 10;	
    public static final String	DUBBO_CIRCUIT_BREAKER_MAX_CONCURRENT_REQUESTS           	= "dubbo.max.concurrent.requests";
    public static final int  	DUBBO_CIRCUIT_BREAKER_MAX_CONCURRENT_REQUESTS_DEFAULT       = 10;
    
    // 熔断器相关字段
    public static final String 	DUBBO_CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD				="dubbo.request.volume.threshold";
    public static final int  	DUBBO_CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD_DEFAULT  	= 20;	
    public static final String 	DUBBO_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS			="dubbo.sleep.window.in.milliseconds";
    public static final int  	DUBBO_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS_DEFAULT	= 5000;	
    public static final String  DUBBO_CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE 			="dubbo.error.threshold.percentage";
    public static final int  	DUBBO_CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE_DEFAULT    = 50;	
    
}

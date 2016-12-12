package com.tent.dubbo.circuit.breaker.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.tent.dubbo.circuit.breaker.common.Constants;

/**
 * MetricsServer
 *
 * @author tent.zhang
 */
public class MetricsServer {
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsServer.class);
	private static final /*volatile*/ ExecutorService executorService = Executors.newFixedThreadPool(1);
	private static volatile Server server = null;
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (server != null) {
					try {
						server.stop();
						server.destroy();
						logger.warn("metricsServer stop and destroy!");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}));
	}
	
	private MetricsServer(){
		
		try {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						server = new Server(CircuitBreakerUtils.getValue(Constants.DUBBO_CIRCUIT_BREAKER_METRICS_STREAM_PORT, Constants.DUBBO_CIRCUIT_BREAKER_METRICS_STREAM_PORT_DEFAULT));
						WebAppContext context = new WebAppContext();
						context.setContextPath("/");
						context.addServlet(HystrixMetricsStreamServlet.class, "/hystrix.stream");
						context.setResourceBase(".");
						server.setHandler(context);
						server.start();
//						server.join();
						logger.info("metrics server started!");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private static MetricsServer metricsServer;
	
	public static MetricsServer enable(){
		if (metricsServer == null) {
			synchronized (MetricsServer.class) {
				if (metricsServer == null) {
					metricsServer = new MetricsServer();
				}
			}
		}
		return metricsServer;
	}
	
	public static void destory() {
		if (server != null) {
			try {
				server.stop();
				server.destroy();
				logger.warn("metricsServer stop and destroy!");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}

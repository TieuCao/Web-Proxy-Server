package com.thaitien.proxy.core.intercept.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thaitien.proxy.core.intercept.RequestInterceptor;

/**
 * The class is implemented to provide basic request interceptor. Only support
 * requests that have GET, POST, CONNECT method
 * 
 * @author tien
 *
 */
public class DefaultRequestInterceptor implements RequestInterceptor {
	private static Logger requestLogger = LogManager.getLogger("Request");

	@Override
	public boolean isHandleRequest(String method, String url, String host, int port, String uri) {
		if ("GET".equals(method) || "POST".equals(method) || "CONNECT".equals(method))
			return true;
		return false;
	}

	@Override
	public void handleWhenBlocked(String method, String url, String host, int port, String uri) {
		requestLogger.info("REFUSE: " + url);
	}

	@Override
	public void handleWhenAccepted(String method, String url, String host, int port, String uri) {
		requestLogger.info("CONNECT: " + url);
	}

}

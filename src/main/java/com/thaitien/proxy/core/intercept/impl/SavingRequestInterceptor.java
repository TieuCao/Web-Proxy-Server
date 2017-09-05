package com.thaitien.proxy.core.intercept.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thaitien.proxy.core.intercept.RequestInterceptor;
import com.thaitien.proxy.core.model.HttpMethod;
import com.thaitien.proxy.core.model.RequestInterceptorModel;
import com.thaitien.proxy.db.entity.Request;
import com.thaitien.proxy.db.entity.RequestBlocking;
import com.thaitien.proxy.db.service.RequestBlockingService;
import com.thaitien.proxy.db.service.RequestService;

public class SavingRequestInterceptor implements RequestInterceptor {
	private static Logger requestLogger = LogManager.getRootLogger();
	private static Logger requestBlockLogger = LogManager.getRootLogger();

	private List<RequestInterceptorModel> lstRequests;
	private List<Integer> lstPorts;
	private boolean isWriteLog = true;

	private RequestService requestService = RequestService.getInstance();
	private RequestBlockingService requestBlockingService = RequestBlockingService.getInstance();

	public SavingRequestInterceptor() {
	}

	public SavingRequestInterceptor(List<RequestInterceptorModel> lstRequests, List<Integer> lstPorts) {
		this.lstRequests = lstRequests;
		this.lstPorts = lstPorts;
	}

	public List<RequestInterceptorModel> getLstRequests() {
		return lstRequests;
	}

	public void setLstRequests(List<RequestInterceptorModel> lstRequests) {
		this.lstRequests = lstRequests;
	}

	public List<Integer> getLstPorts() {
		return lstPorts;
	}

	public void setLstPorts(List<Integer> lstPorts) {
		this.lstPorts = lstPorts;
	}

	public boolean isWriteLog() {
		return isWriteLog;
	}

	public void setWriteLog(boolean isWriteLog) {
		this.isWriteLog = isWriteLog;
	}

	@Override
	public boolean isHandleRequest(String method, String url, String host, int port, String uri) {
		if (lstRequests == null && lstPorts == null)
			return true;

		if (lstPorts != null) {
			for (int p : lstPorts) {
				if (p == port)
					return false;
			}
		}

		if (lstRequests != null) {
			for (RequestInterceptorModel request : lstRequests) {
				if (url.matches(request.getAntPath().convertToRegexPattern())) {
					List<HttpMethod> lstMethods = request.getLstMethods();

					if (lstMethods == null || lstMethods.size() == 0)
						return false;

					for (HttpMethod httpMethod : lstMethods) {
						if (method.equals(httpMethod.getMethod()))
							return false;
					}
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public void handleWhenAccepted(String method, String url, String host, int port, String uri) {
		requestLogger.info(url);
		if (isWriteLog)
			requestService.insert(new Request(host, uri, port));

	}

	@Override
	public void handleWhenBlocked(String method, String url, String host, int port, String uri) {
		requestBlockLogger.info(url);
		if (isWriteLog)
			requestBlockingService.insert(new RequestBlocking(host, uri, port));
	}
}

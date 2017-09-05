package com.thaitien.proxy.core.intercept.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thaitien.proxy.core.intercept.CookiesInterceptor;
import com.thaitien.proxy.db.entity.CookieBlocking;
import com.thaitien.proxy.db.service.CookieBlockingService;

public class SavingCookiesInterceptor implements CookiesInterceptor {
	private static Logger cookieLogger = LogManager.getRootLogger();
	private CookieBlockingService cookieService = CookieBlockingService.getInstance();

	private boolean isRemoveInRequest, isRemoveInResponse;

	public void setRemoveInRequest(boolean isRemoveInRequest) {
		this.isRemoveInRequest = isRemoveInRequest;
	}

	public void setRemoveInResponse(boolean isRemoveInResponse) {
		this.isRemoveInResponse = isRemoveInResponse;
	}

	@Override
	public boolean isRemoveCookiesInRequest() {
		return isRemoveInRequest;
	}

	@Override
	public boolean isRemoveCookiesInResponse() {
		return isRemoveInResponse;
	}

	@Override
	public void handleCookieRemoveInRequest(String cookie) {
		cookieLogger.info("REQUEST COOKIE:" + cookie);
		cookieService.insert(new CookieBlocking(false, cookie));
	}

	@Override
	public void handleCookieRemoveInResponse(String cookie) {
		cookieLogger.info("RESPONSE COOKIE:" + cookie);
		cookieService.insert(new CookieBlocking(true, cookie));

	}

}

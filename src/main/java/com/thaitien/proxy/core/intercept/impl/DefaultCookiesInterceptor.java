package com.thaitien.proxy.core.intercept.impl;

import com.thaitien.proxy.core.intercept.CookiesInterceptor;

/**
 * The class is implemented to provide basic cookie interceptor. No cookie is
 * removed in transportation progress
 * 
 * @author tien
 *
 */
public class DefaultCookiesInterceptor implements CookiesInterceptor {

	@Override
	public boolean isRemoveCookiesInRequest() {
		return false;
	}

	@Override
	public boolean isRemoveCookiesInResponse() {
		return false;
	}

	@Override
	public void handleCookieRemoveInRequest(String cookie) {

	}

	@Override
	public void handleCookieRemoveInResponse(String cookie) {

	}

}

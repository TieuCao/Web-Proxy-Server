package com.thaitien.proxy.core.intercept;

public interface CookiesInterceptor {
	/**
	 * determined whether cookie in request is removed
	 * 
	 * @return true if accept, otherwise
	 */
	public boolean isRemoveCookiesInRequest();

	/**
	 * determine whether cookie in response is removed
	 * 
	 * @return true if accept, otherwise
	 */
	public boolean isRemoveCookiesInResponse();

	/**
	 * handle something with cookie that is removed
	 * 
	 * @param cookie
	 *            current cookie in request
	 */
	public void handleCookieRemoveInRequest(String cookie);

	/**
	 * handle something with cookie that is removed
	 * 
	 * @param cookie
	 *            current cookie in response
	 */
	public void handleCookieRemoveInResponse(String cookie);
}

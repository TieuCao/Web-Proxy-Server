package com.thaitien.proxy.core.intercept;

public interface RequestInterceptor {

	/**
	 * determined whether request is blocked base on method, URL or port
	 * 
	 * @param method
	 *            methods of HTTP. Example: CONNECT, GET, POST ...
	 * @param url
	 *            full URL
	 * @param host
	 *            host name part in full URL
	 * @param port
	 *            port part in full URL
	 * @param uri
	 *            uri part in full URL
	 * @return true if request is blocked, otherwise
	 */
	public boolean isHandleRequest(String method, String url, String host, int port, String uri);

	/**
	 * handle something when request is blocked
	 */
	public void handleWhenBlocked(String method, String url, String host, int port, String uri);

	/**
	 * handle something when request passed filter
	 */
	public void handleWhenAccepted(String method, String url, String host, int port, String uri);

}

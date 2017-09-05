package com.thaitien.proxy.core.intercept;

public interface ConnectionInterceptor {

	public boolean isInterceptConnection();

	public void connectIn(String url, int countConnection);

	/**
	 * Handle something when a connection finished
	 * 
	 * @param url
	 *            full URL that connected
	 * @param countActive
	 *            currently connection count
	 */
	public void connectOut(String url, int countConnection);
}

package com.thaitien.proxy.core.intercept.impl;

import com.thaitien.proxy.core.intercept.ConnectionInterceptor;

public class DefaultConnectionInterceptor implements ConnectionInterceptor {

	@Override
	public boolean isInterceptConnection() {
		return false;
	}

	@Override
	public void connectIn(String url, int countConnection) {
		
	}

	@Override
	public void connectOut(String url, int countConnection) {
		
	}


}

package com.thaitien.proxy.core.model;

public enum HttpMethod {

	CONNECT("CONNECT"), GET("GET"), POST("POST"), PUT("PUT"), HEAD("HEAD"), DELETE("DELETE"), TRACE("TRACE"), OPTIONS(
			"OPTIONS");

	private String method;

	private HttpMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

}
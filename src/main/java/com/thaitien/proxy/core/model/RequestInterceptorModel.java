package com.thaitien.proxy.core.model;

import java.util.ArrayList;
import java.util.List;

public class RequestInterceptorModel {
	private AntPath antPath;
	private List<HttpMethod> lstMethods;

	public RequestInterceptorModel() {
		lstMethods = new ArrayList<>();
	}

	public RequestInterceptorModel(AntPath antPath, List<HttpMethod> lstMethods) {
		this.antPath = antPath;
		this.lstMethods = lstMethods;
	}

	public AntPath getAntPath() {
		return antPath;
	}

	public void setAntPath(AntPath antPath) {
		this.antPath = antPath;
	}

	public List<HttpMethod> getLstMethods() {
		return lstMethods;
	}

	public void setLstMethods(List<HttpMethod> lstMethods) {
		this.lstMethods = lstMethods;
	}

	@Override
	public String toString() {
		return "RequestInterceptorModel [antPath=" + antPath + ", lstMethods=" + lstMethods + "]";
	}
	
}

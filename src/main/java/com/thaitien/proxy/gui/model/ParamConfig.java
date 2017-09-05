package com.thaitien.proxy.gui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.thaitien.proxy.core.model.RequestInterceptorModel;

public class ParamConfig extends Observable {
	public static final String LOG_MODE = "LOG MODE";
	public static final String MONITOR_CURRENT_REQUEST = "MONITOR CURRENT REQUEST";
	public static final String REMOVE_COOKIE_IN_REQUEST = "REMOVE COOKIE IN REQUEST";
	public static final String REMOVE_COOKIE_IN_RESPONSE = "REMOVE COOKIE IN RESPONSE";
	public static final String FILTER_BY_URL = "FILTER BY URL";
	public static final String FILTER_BY_PORT = "FILTER BY PORT";
	public static final int PORT_DEFAULT = 45678;
	public static final int MAX_CONNECT_DEFAULT = 50;

	private static ParamConfig unique;
	private int port, maxConnect;
	private boolean isEnableLog, isMonitorRequest;
	private boolean isRmRequesCookie, isRmResponseCookie;
	private boolean isFilterByUrl, isFilterByPort;
	private List<RequestInterceptorModel> lstSkipRequests;
	private List<Integer> lstSkipPorts;

	private ParamConfig() {
		port = PORT_DEFAULT;
		maxConnect = MAX_CONNECT_DEFAULT;
		lstSkipRequests = new ArrayList<>();
		lstSkipPorts = new ArrayList<>();
	}

	public synchronized static ParamConfig getInstance() {
		if (unique == null)
			unique = new ParamConfig();
		return unique;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxConnect() {
		return maxConnect;
	}

	public void setMaxConnect(int maxConnect) {
		this.maxConnect = maxConnect;
	}

	public boolean isMonitorRequest() {
		return isMonitorRequest;
	}

	public void setMonitorRequest(boolean isMonitorRequest) {
		this.isMonitorRequest = isMonitorRequest;
		setChanged();
		notifyObservers(MONITOR_CURRENT_REQUEST);
	}

	public boolean isEnableLog() {
		return isEnableLog;
	}

	/**
	 * set enable log and notify to observer to do something. Code for set enable log to observer is constant
	 * "ENABLE_LOG"
	 * 
	 * @param isEnableLog
	 *            true if enable, otherwise
	 */
	public void setEnableLog(boolean isEnableLog) {
		this.isEnableLog = isEnableLog;
		setChanged();
		notifyObservers(LOG_MODE);
	}

	public boolean isRmRequestCookie() {
		return isRmRequesCookie;
	}

	/**
	 * set enable remove cookie in request and notify to observer to do something. Code for set enable log to observer
	 * is constant "REMOVE_COOKIE_IN_REQUEST"
	 * 
	 * @param isRmRequesCookie
	 */
	public void setRmRequesCookie(boolean isRmRequesCookie) {
		this.isRmRequesCookie = isRmRequesCookie;
		setChanged();
		notifyObservers(REMOVE_COOKIE_IN_REQUEST);
	}

	public boolean isRmResponseCookie() {
		return isRmResponseCookie;
	}

	/**
	 * set enable remove cookie in response and notify to observer to do something. Code for set enable log to observer
	 * is constant "REMOVE_COOKIE_IN_RESPONSE"
	 * 
	 * @param isRmResponseCookie
	 */
	public void setRmResponseCookie(boolean isRmResponseCookie) {
		this.isRmResponseCookie = isRmResponseCookie;
		setChanged();
		notifyObservers(REMOVE_COOKIE_IN_RESPONSE);
	}

	public boolean isFilterByUrl() {
		return isFilterByUrl;
	}

	/**
	 * set enable filter by URL and notify to observer to do something. Code for set enable log to observer is constant
	 * "FILTER_BY_URL"
	 * 
	 * @param isFilterByUrl
	 */
	public void setFilterByUrl(boolean isFilterByUrl) {
		this.isFilterByUrl = isFilterByUrl;
		setChanged();
		notifyObservers(FILTER_BY_URL);
	}

	public boolean isFilterByPort() {
		return isFilterByPort;
	}

	/**
	 * set enable filter by port and notify to observer to do something. Code for set enable log to observer is constant
	 * "FILTER_BY_PORT"
	 * 
	 * @param isFilterByPort
	 */
	public void setFilterByPort(boolean isFilterByPort) {
		this.isFilterByPort = isFilterByPort;
		setChanged();
		notifyObservers(FILTER_BY_PORT);
	}

	public List<RequestInterceptorModel> getLstSkipRequests() {
		return lstSkipRequests;
	}

	public void setLstSkipRequests(List<RequestInterceptorModel> lstSkipRequests) {
		this.lstSkipRequests = lstSkipRequests;
	}

	public List<Integer> getLstSkipPorts() {
		return lstSkipPorts;
	}

	public void setLstSkipPorts(List<Integer> lstSkipPorts) {
		this.lstSkipPorts = lstSkipPorts;
	}

	@Override
	public String toString() {
		return "ParamConfig [port=" + port + ", maxConnect=" + maxConnect + ", isEnableLog=" + isEnableLog
				+ ", isRmRequesCookie=" + isRmRequesCookie + ", isRmResponseCookie=" + isRmResponseCookie
				+ ", isFilterByUrl=" + isFilterByUrl + ", isFilterByPort=" + isFilterByPort + ", lstSkipRequests="
				+ lstSkipRequests + ", lstSkipPorts=" + lstSkipPorts + "]";
	}
}

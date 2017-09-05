package com.thaitien.proxy.db.entity;

import java.io.Serializable;
import java.util.Date;

public class CookieBlocking implements Serializable {
	private static final long serialVersionUID = -851753423619962671L;
	private Long id;
	private boolean typeBlocking; // true: remove in response, false: remove in request
	private String cookie;
	private Date dateCreated;

	public CookieBlocking() {
	}

	public CookieBlocking(boolean typeBlocking, String cookie) {
		super();
		this.typeBlocking = typeBlocking;
		this.cookie = cookie;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isTypeBlocking() {
		return typeBlocking;
	}

	public void setTypeBlocking(boolean typeBlocking) {
		this.typeBlocking = typeBlocking;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

}

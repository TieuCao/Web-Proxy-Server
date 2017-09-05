package com.thaitien.proxy.db.entity;

import java.util.Date;

public class RequestBlocking implements java.io.Serializable {
	private static final long serialVersionUID = 4305464039722985632L;
	private Long id;
	private String hostname;
	private String uri;
	private Integer port;
	private Date dateCreated;

	public RequestBlocking() {
	}

	public RequestBlocking(long id) {
		this.id = id;
	}

	public RequestBlocking(String hostname, String uri, Integer port) {
		this.hostname = hostname;
		this.uri = uri;
		this.port = port;
	}

	public RequestBlocking(long id, String hostname, String uri, Integer port, Date dateCreated) {
		this.id = id;
		this.hostname = hostname;
		this.uri = uri;
		this.port = port;
		this.dateCreated = dateCreated;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

}

package com.thaitien.proxy.gui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thaitien.proxy.core.model.HttpMethod;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class BlackUrlModel {
	private StringProperty url;
	private HttpMethod[] methods;
	private BooleanProperty[] votes;

	public BlackUrlModel(String url, boolean initValue) {
		this.url = new SimpleStringProperty(url);
		this.votes = new BooleanProperty[HttpMethod.values().length];
		this.methods = new HttpMethod[HttpMethod.values().length];

		for (int i = 0; i < HttpMethod.values().length; i++) {
			methods[i] = HttpMethod.values()[i];
			votes[i] = new SimpleBooleanProperty(initValue);
		}
	}

	public BlackUrlModel(String url) {
		this(url, true);
	}

	public HttpMethod[] getMethods() {
		return methods;
	}

	public void setMethods(HttpMethod[] methods) {
		this.methods = methods;
	}

	public String getUrl() {
		return url.getValue();
	}

	public StringProperty getUrlProperty() {
		return url;
	}

	public void setUrl(String url) {
		this.url.setValue(url);
	}

	public BooleanProperty[] getVotes() {
		return votes;
	}

	public void setVotes(BooleanProperty[] votes) {
		this.votes = votes;
	}

	public void setVote(int index, boolean value) {
		this.votes[index].set(value);
	}

	public void setVote(HttpMethod method, boolean value) {
		for (int i = 0; i < votes.length; i++) {
			if (methods[i] == method) {
				votes[i].set(value);
				return;
			}
		}
	}

	public List<HttpMethod> getBlackMethodList() {
		ArrayList<HttpMethod> lstBlackMethods = new ArrayList<>();

		for (int i = 0; i < votes.length; i++) {
			if (votes[i].getValue().booleanValue())
				lstBlackMethods.add(methods[i]);
		}
		return lstBlackMethods;
	}

	public void setChangeListener(ChangeListener<Boolean> listener) {
		for (int i = 0; i < HttpMethod.values().length; i++) {
			votes[i].addListener(listener);
		}
	}

	@Override
	public String toString() {
		return "BlackUrlModel [url=" + url + ", lstMethod=" + methods + ", votes=" + Arrays.toString(votes) + "]";
	}

}

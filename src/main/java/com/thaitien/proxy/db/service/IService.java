package com.thaitien.proxy.db.service;

import java.util.List;

public interface IService<T, Id> {
	public Id insert(T entity);

	public void update(T entity);

	public void delete(Id id);

	public List<T> getAll();

}

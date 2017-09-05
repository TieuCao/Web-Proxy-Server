package com.thaitien.proxy.db.connection;

import java.sql.Connection;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {
	public static final String DEFAULT_DATABASE_NAME = "log.db";
	private static DataSource dataSource = buildConnectionPool();

	private static DataSource buildConnectionPool() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:sqlite:" + DEFAULT_DATABASE_NAME);
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(20);
		return new HikariDataSource(config);
	}

	/**
	 * Get connection from connection pool
	 * 
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		return dataSource.getConnection();
	}

}

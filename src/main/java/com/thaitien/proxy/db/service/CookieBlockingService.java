package com.thaitien.proxy.db.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.thaitien.proxy.db.connection.DBConnection;
import com.thaitien.proxy.db.entity.CookieBlocking;

public class CookieBlockingService implements ICookieBlockingService {
	private static CookieBlockingService unique;

	private CookieBlockingService() {
	}

	public static synchronized CookieBlockingService getInstance() {
		if (unique == null)
			unique = new CookieBlockingService();
		return unique;
	}

	@Override
	public Long insert(CookieBlocking entity) {
		String sql = "insert into Cookie_Blocking values (null,?,?,null)";
		try (Connection con = DBConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
			pst.setBoolean(1, entity.isTypeBlocking());
			pst.setString(2, entity.getCookie());
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			if (rs.next())
				return rs.getLong(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}

	@Override
	public void update(CookieBlocking entity) {

	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public List<CookieBlocking> getAll() {
		return null;
	}

}

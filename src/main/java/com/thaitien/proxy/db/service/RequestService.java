package com.thaitien.proxy.db.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.thaitien.proxy.db.connection.DBConnection;
import com.thaitien.proxy.db.entity.Request;

public class RequestService implements IRequestService {
	private static RequestService unique;

	private RequestService() {
	}

	public static synchronized RequestService getInstance() {
		if (unique == null)
			unique = new RequestService();
		return unique;
	}

	@Override
	public Long insert(Request entity) {
		String sql = "insert into Request values (null,?,?,?,null)";
		try (Connection con = DBConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
			pst.setString(1, entity.getHostname());
			pst.setString(2, entity.getUri());
			pst.setInt(3, entity.getPort());

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
	public void update(Request entity) {

	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public List<Request> getAll() {
		return null;
	}
}

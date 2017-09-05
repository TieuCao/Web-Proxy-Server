package com.thaitien.proxy.db.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.thaitien.proxy.db.connection.DBConnection;
import com.thaitien.proxy.db.entity.RequestBlocking;

public class RequestBlockingService implements IRequestBlockingService {
	private static RequestBlockingService unique;

	private RequestBlockingService() {
	}

	public static synchronized RequestBlockingService getInstance() {
		if (unique == null)
			unique = new RequestBlockingService();
		return unique;
	}

	@Override
	public Long insert(RequestBlocking entity) {
		String sql = "insert into Request_Blocking values (null,?,?,?,null)";
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
	public void update(RequestBlocking entity) {

	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public List<RequestBlocking> getAll() {
		return null;
	}

}

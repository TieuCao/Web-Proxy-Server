package com.thaitien.proxy.db.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;

import com.thaitien.proxy.db.connection.DBConnection;

public class DBHelper {

	public static void createDatabase(InputStream sqlInput) throws Exception {
		Connection con = null;
		InputStreamReader reader = null;
		Statement pst = null;

		try {
			con = DBConnection.getConnection();
			reader = new InputStreamReader(sqlInput, Charset.forName("UTF8"));

			StringBuilder sqlBuilder = new StringBuilder(sqlInput.available());
			for (int c = -1; (c = reader.read()) != -1;) {
				sqlBuilder.append((char) c);
			}

			pst = con.createStatement();
			pst.executeUpdate(sqlBuilder.toString());
		} catch (Exception e) {
			throw e;
		} finally {
			if (con != null)
				con.close();
			if (reader != null)
				reader.close();
			if (sqlInput != null)
				sqlInput.close();
			if (pst != null)
				pst.close();
		}
	}
}
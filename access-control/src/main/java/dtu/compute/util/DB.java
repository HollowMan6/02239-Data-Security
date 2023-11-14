package dtu.compute.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
	public void addUser(String username, String passwordhash) {
		try {
			Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
			PreparedStatement preparedStatement = conn
					.prepareStatement("insert into users (username, password_hash) values (?, ?)");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, passwordhash);
			preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getUserPasswordHashByName(String username) {
		String result = null;
		try {
			Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
			PreparedStatement preparedStatement = conn
					.prepareStatement("select password_hash from users where username = ?");
			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				result = resultSet.getString("password_hash");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void clear() {
		try {
			Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
			Statement statement = conn.createStatement();
			String sql = "TRUNCATE TABLE users";
			statement.executeUpdate(sql);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

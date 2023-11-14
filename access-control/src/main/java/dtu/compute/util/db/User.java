package dtu.compute.util.db;

import dtu.compute.util.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class User {
    public int addUser(String username, String passwordhash, String role) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("insert into users (username, role, password_hash) values (?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, role);
            preparedStatement.setString(3, passwordhash);
            result = preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int deleteUserByName(String username) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("delete from users where username = ?");
            preparedStatement.setString(1, username);
            result = preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getUserPasswordHashByName(String username) {
        String result = null;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("select password_hash from users where username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getString("password_hash");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getUserRoleByName(String username) {
        String result = null;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("select role from users where username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getString("role");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int updateUserRoleByName(String role, String username) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("update users set role = ? where username = ?");
            preparedStatement.setString(1, role);
            preparedStatement.setString(2, username);
            result = preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int count() {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("select count(*) from users");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) result = resultSet.getInt(1);
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

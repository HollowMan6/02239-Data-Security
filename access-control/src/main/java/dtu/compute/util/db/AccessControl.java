package dtu.compute.util.db;

import dtu.compute.server.ac.Model;
import dtu.compute.util.Configuration;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AccessControl {

    public int updateAccessControlList(String username, int permission) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("update access_control_list set print = ?, queue = ?, top_queue = ?, start = ?, stop = ?, restart = ?, status = ?, read_config = ?, set_config = ? where username = ?");
            preparedStatement.setInt(1, (permission & 0b100000000) >>> 8);
            preparedStatement.setInt(2, (permission & 0b010000000) >>> 7);
            preparedStatement.setInt(3, (permission & 0b001000000) >>> 6);
            preparedStatement.setInt(4, (permission & 0b000100000) >>> 5);
            preparedStatement.setInt(5, (permission & 0b000010000) >>> 4);
            preparedStatement.setInt(6, (permission & 0b000001000) >>> 3);
            preparedStatement.setInt(7, (permission & 0b000000100) >>> 2);
            preparedStatement.setInt(8, (permission & 0b000000010) >>> 1);
            preparedStatement.setInt(9, permission & 0b000000001);
            preparedStatement.setString(10, username);
            result = preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addAccessControlList(String username, int permission) {
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("insert into access_control_list (username, print, queue, top_queue, start, stop, restart, status, read_config, set_config) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, (permission & 0b100000000) >>> 8);
            preparedStatement.setInt(3, (permission & 0b010000000) >>> 7);
            preparedStatement.setInt(4, (permission & 0b001000000) >>> 6);
            preparedStatement.setInt(5, (permission & 0b000100000) >>> 5);
            preparedStatement.setInt(6, (permission & 0b000010000) >>> 4);
            preparedStatement.setInt(7, (permission & 0b000001000) >>> 3);
            preparedStatement.setInt(8, (permission & 0b000000100) >>> 2);
            preparedStatement.setInt(9, (permission & 0b000000010) >>> 1);
            preparedStatement.setInt(10, permission & 0b000000001);
            preparedStatement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Boolean> getAccessControlListByName(String username) {
        Map<String, Boolean> operations = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("select print, queue, top_queue, start, stop, restart, status, read_config, set_config from access_control_list where username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                operations.put("print", resultSet.getInt("print") != 0);
                operations.put("queue", resultSet.getInt("queue") != 0);
                operations.put("topQueue", resultSet.getInt("top_queue") != 0);
                operations.put("start", resultSet.getInt("start") != 0);
                operations.put("stop", resultSet.getInt("stop") != 0);
                operations.put("restart", resultSet.getInt("restart") != 0);
                operations.put("status", resultSet.getInt("status") != 0);
                operations.put("readConfig", resultSet.getInt("read_config") != 0);
                operations.put("setConfig", resultSet.getInt("set_config") != 0);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return operations;
    }

    public Map<String, Boolean> getAccessControlListByRole(String role) {
        Map<String, Boolean> operations = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("select print, queue, top_queue, start, stop, restart, status, read_config, set_config from roles where role = ?");
            preparedStatement.setString(1, role);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                operations.put("print", resultSet.getInt("print") != 0);
                operations.put("queue", resultSet.getInt("queue") != 0);
                operations.put("topQueue", resultSet.getInt("top_queue") != 0);
                operations.put("start", resultSet.getInt("start") != 0);
                operations.put("stop", resultSet.getInt("stop") != 0);
                operations.put("restart", resultSet.getInt("restart") != 0);
                operations.put("status", resultSet.getInt("status") != 0);
                operations.put("readConfig", resultSet.getInt("read_config") != 0);
                operations.put("setConfig", resultSet.getInt("set_config") != 0);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return operations;
    }

    public int deleteAccessControlListByName(String username) {
        int result = 0;
        try {
            Connection conn = DriverManager.getConnection(Configuration.dbUrl, Configuration.dbUsername, Configuration.dbPassword);
            PreparedStatement preparedStatement = conn.prepareStatement("delete from access_control_list where username = ?");
            preparedStatement.setString(1, username);
            result = preparedStatement.executeUpdate();
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
            statement.executeUpdate("TRUNCATE TABLE access_control_list");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
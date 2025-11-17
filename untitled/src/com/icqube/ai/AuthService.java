package com.icqube.ai;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class AuthService {


    private static final String DB_URL = "jdbc:ucanaccess://ICQube.accdb";

    private String currentTwoFactorCode;
    private String currentTwoFactorUser;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }


    public boolean validateCredentials(String username, String password) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String startTwoFactor(String username) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        currentTwoFactorCode = String.valueOf(code);
        currentTwoFactorUser = username;
        return currentTwoFactorCode;
    }

    public boolean verifyTwoFactor(String username, String codeEntered) {
        if (currentTwoFactorUser == null || currentTwoFactorCode == null) return false;
        return currentTwoFactorUser.equals(username) && currentTwoFactorCode.equals(codeEntered);
    }


    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------- CREATE ACCOUNT --------
    public boolean createUser(String username, String password, String email, String phone) {
        // Basic checks
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        // Don�t allow duplicate usernames
        if (userExists(username)) {
            return false;
        }

        String sql = "INSERT INTO Users (username, password, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, phone);

            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

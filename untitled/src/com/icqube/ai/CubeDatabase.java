package com.icqube.ai;

import java.sql.*;

public class CubeDatabase {

    private static final String DB_URL = "jdbc:ucanaccess://ICQube.accdb";

    public static String findCubeByTags(String[] tags) {
        StringBuilder query = new StringBuilder(
                "SELECT CubeName, Price, Brand, URL FROM Cubes WHERE ");
        for (int i = 0; i < tags.length; i++) {
            query.append("Tags LIKE ?");
            if (i < tags.length - 1) query.append(" AND ");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < tags.length; i++) {
                stmt.setString(i + 1, "%" + tags[i].trim() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("CubeName") + " (" + rs.getString("Brand") + ") - $" +
                        rs.getDouble("Price") + "\n" + rs.getString("URL");
            } else {
                return "No matching cube found.";
            }

        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }
}

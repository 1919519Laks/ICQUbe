package com.icqube.ai;

import java.sql.*;
import java.util.*;

public class CubeDatabase {
    private static final String DB_URL = "jdbc:ucanaccess://ICQube.accdb";

    public static String findCubeByTags(String[] tags) {
        List<String> normalizedTags = new ArrayList<>(Arrays.asList(tags));

        if (normalizedTags.isEmpty())
            return "Please specify your cube preferences to get recommendations.";

        StringBuilder query = new StringBuilder("SELECT CubeName, Price, Brand, URL, Tags FROM Cubes WHERE ");
        for (int i = 0; i < normalizedTags.size(); i++) {
            query.append("LOWER(Tags) LIKE ?");
            if (i < normalizedTags.size() - 1) query.append(" AND ");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            for (String tag : normalizedTags) {
                stmt.setString(paramIndex++, "%" + tag.toLowerCase().trim() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            List<CubeMatch> matches = new ArrayList<>();

            while (rs.next()) {
                String dbTags = rs.getString("Tags").toLowerCase();
                int matchCount = 0;
                for (String tag : normalizedTags) {
                    if (dbTags.contains(tag)) matchCount++;
                }
                if (matchCount == normalizedTags.size()) {
                    matches.add(new CubeMatch(
                            rs.getString("CubeName"),
                            rs.getDouble("Price"),
                            rs.getString("Brand"),
                            rs.getString("URL"),
                            rs.getString("Tags"),
                            matchCount
                    ));
                }
            }

            if (matches.isEmpty()) {
                List<String> missingFeatures = new ArrayList<>();
                List<String> partialMatches = new ArrayList<>();
                List<String> exampleCubes = new ArrayList<>();

                for (String tag : normalizedTags) {
                    String q = "SELECT CubeName, Price FROM Cubes WHERE LOWER(Tags) LIKE ?";
                    try (PreparedStatement s = conn.prepareStatement(q)) {
                        s.setString(1, "%" + tag + "%");
                        ResultSet r = s.executeQuery();
                        if (!r.next()) {
                            missingFeatures.add(tag);
                        } else {
                            partialMatches.add(tag);
                            exampleCubes.add(r.getString("CubeName") + " ($" + String.format("%.2f", r.getDouble("Price")) + ")");
                        }
                    }
                }
                return AIExplainHandler.generateFriendlyExplanation(missingFeatures, partialMatches, exampleCubes);
            }

            matches.sort((a, b) -> Integer.compare(b.matchCount, a.matchCount));

            StringBuilder results = new StringBuilder();
            for (CubeMatch cube : matches) {
                results.append(String.format(
                        "%s (%s) - $%.2f\n%s\nMatched tags: %d\n\n",
                        cube.cubeName, cube.brand, cube.price, cube.url, cube.matchCount
                ));
            }
            return results.toString().trim();

        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        }
    }

    private static class CubeMatch {
        String cubeName, brand, url, tags;
        double price;
        int matchCount;

        CubeMatch(String name, double price, String brand, String url, String tags, int matches) {
            this.cubeName = name;
            this.price = price;
            this.brand = brand;
            this.url = url;
            this.tags = tags;
            this.matchCount = matches;
        }
    }
}

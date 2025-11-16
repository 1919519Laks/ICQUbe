package com.icqube.ai;

public class CubeRecommender {
    public static String getRecommendedCube(String tagString) {
        String[] tags = tagString == null ? new String[0] : tagString.split("[,;\\s]+");
        return CubeDatabase.findCubeByFilters(tags, null, null, null);
    }

    public static String getRecommendedCube(String tagString, String brand, Double minPrice, Double maxPrice) {
        String[] tags = tagString == null ? new String[0] : tagString.split("[,;\\s]+");
        return CubeDatabase.findCubeByFilters(tags, brand, minPrice, maxPrice);
    }
}

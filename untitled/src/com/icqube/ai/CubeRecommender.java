package com.icqube.ai;

public class CubeRecommender {
    public static String getRecommendedCube(String aiOutput) {
        String[] tags = aiOutput.split("[,;\\s]+");
        return CubeDatabase.findCubeByTags(tags);
    }
}

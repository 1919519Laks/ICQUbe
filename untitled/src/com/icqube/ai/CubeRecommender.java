package com.icqube.ai;

public class CubeRecommender {

    // Break AI output into tags and query database
    public static String getRecommendedCube(String aiOutput) {
        String[] tags = aiOutput.split("[,;\\s]+");
        return CubeDatabase.findCubeByTags(tags);
    }
}

package com.icqube.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.List;

public class AIExplainHandler {

    private static final String MODEL = "gemini-2.5-flash";

    public static String generateFriendlyExplanation(List<String> missingFeatures, List<String> partialMatches, List<String> possibleCubes) {
        try {
            Client client = new Client();

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("You are a friendly chatbot helping users find Rubik's cubes. ");
            promptBuilder.append("The user requested cubes with these features: ");
            promptBuilder.append(String.join(", ", missingFeatures));
            promptBuilder.append(". However, some features are missing or partially matched: ");
            if (partialMatches.isEmpty()) {
                promptBuilder.append("None of those features are available in the database. ");
            } else {
                promptBuilder.append("Some features partially match cubes: ");
                promptBuilder.append(String.join(", ", partialMatches));
                promptBuilder.append(". ");
                promptBuilder.append("Example cubes with these features include: ");
                promptBuilder.append(String.join("; ", possibleCubes));
                promptBuilder.append(". ");
            }
            promptBuilder.append("Please write a polite, natural language explanation to the user, suggesting loosening criteria or alternatives if needed.");

            GenerateContentResponse response = client.models.generateContent(MODEL, promptBuilder.toString(), null);
            return response.text().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, we couldn't find cubes matching all your requested features. Please try adjusting your criteria.";
        }
    }
}

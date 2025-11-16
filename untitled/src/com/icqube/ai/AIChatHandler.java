package com.icqube.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIChatHandler {

    private static final String MODEL = "gemini-2.5-flash";

    public static String mapUserTagsToCanonical(String userInput) {
        try {
            Client client = new Client();

            String prompt = "You are a tag normalizer for a Rubik's cube database. " +
                    "Convert this user input tags or words: \"" + userInput + "\" to canonical tags: " +
                    "cheap;budget;fast;premium;magnetic;customizable;coremagnet;stickerless;bluetooth;uvcoated. " +
                    "Output only the final tags separated by semicolons. Read the userinput carefully and generate proper tags. Only generate tags from the user input, do not hallucinate tags. Do not output any cube names or recommendations.";

            GenerateContentResponse response = client.models.generateContent(MODEL, prompt, null);
            return response.text().toLowerCase().replaceAll("\\s+", "");
        } catch (Exception e) {
            e.printStackTrace();
            return userInput;
        }
    }

    public static String[] extractFilterDetails(String userInput) {
        String foundBrand = null;
        Double minPrice = null, maxPrice = null;
        Matcher brandMatcher = Pattern.compile("(brand|by)\\s+(\\w+)").matcher(userInput.toLowerCase());
        if (brandMatcher.find()) foundBrand = brandMatcher.group(2);

        Matcher priceMatcher = Pattern.compile("(under|below)\\s*\\$?(\\d+\\.?\\d*)").matcher(userInput.toLowerCase());
        if (priceMatcher.find()) maxPrice = Double.parseDouble(priceMatcher.group(2));

        Matcher minPriceMatcher = Pattern.compile("(over|above)\\s*\\$?(\\d+\\.?\\d*)").matcher(userInput.toLowerCase());
        if (minPriceMatcher.find()) minPrice = Double.parseDouble(minPriceMatcher.group(2));

        return new String[] { foundBrand, (minPrice == null ? "" : minPrice.toString()), (maxPrice == null ? "" : maxPrice.toString()) };
    }

    public static String getResponseWithContext(String fullConversation) {
        try {
            Client client = new Client();

            String classificationPrompt =
                    "You are an AI chatbot that answers either with cube recommendation tags or normal conversation. " +
                            "Given the conversation so far:\n" + fullConversation + "\n" +
                            "Classify the user's last input as 'request' or 'chat'. Only respond with 'request' or 'chat'.";

            GenerateContentResponse classification = client.models.generateContent(MODEL, classificationPrompt, null);
            String label = classification.text().trim().toLowerCase();

            if ("chat".equals(label)) {
                GenerateContentResponse chatResponse = client.models.generateContent(MODEL, fullConversation, null);
                return "[chat]" + chatResponse.text();
            } else {
                String tagPrompt = "Based on the conversation:\n" + fullConversation +
                        "\nExtract 1-6 tags for cube recommendation using: cheap;budget;fast;premium;magnetic;customizable;coremagnet;stickerless;bluetooth;uvcoated. " +
                        "Output only the tags separated by semicolons. Do not output any cube names or recommendations. Only use tags from the user input.";
                GenerateContentResponse tagResponse = client.models.generateContent(MODEL, tagPrompt, null);
                return tagResponse.text().toLowerCase().replaceAll("\\s+", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fast;controllable;3x3;cheap";
        }
    }
}

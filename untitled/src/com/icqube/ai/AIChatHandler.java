package com.icqube.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.List;

public class AIChatHandler {

    private static final String MODEL = "gemini-2.5-flash";

    /**
     * Maps raw user natural language input to canonical database tags using Gemini.
     * @param userInput The raw user input text.
     * @return semicolon separated list of normalized tags.
     */
    public static String mapUserTagsToCanonical(String userInput) {
        try {
            Client client = new Client();
            String prompt = "You are a tag normalizer for a Rubik's cube database. " +
                    "Convert this user input tags or words: \"" + userInput + "\" " +
                    "into canonical database tags chosen from: cheap;budget;fast;premium;magnetic;customizable;coremagnet;stickerless;bluetooth;uvcoated. " +
                    "Output only the final tags separated by semicolons.";
            GenerateContentResponse response = client.models.generateContent(MODEL, prompt, null);
            return response.text().toLowerCase().replaceAll("\\s+", "");
        } catch (Exception e) {
            e.printStackTrace();
            return userInput;
        }
    }

    /**
     * Returns AI response given the full conversation context.
     * Classifies last input as request or chat.
     * @param fullConversation Entire conversation so far.
     * @return Either tags separated by semicolon for search or text prefixed with [chat].
     */
    public static String getResponseWithContext(String fullConversation) {
        try {
            Client client = new Client();

            String classificationPrompt =
                    "You are an AI chatbot that answers either with cube recommendation tags or normal conversation. " +
                            "Given the conversation so far:\n" + fullConversation + "\n" +
                            "Classify the user's last input as 'request' for product recommendation or 'chat' for conversation only. " +
                            "Respond ONLY with 'request' or 'chat'.";

            GenerateContentResponse classification = client.models.generateContent(MODEL, classificationPrompt, null);
            String label = classification.text().trim().toLowerCase();

            if ("chat".equals(label)) {
                GenerateContentResponse chatResponse = client.models.generateContent(MODEL, fullConversation, null);
                return "[chat]" + chatResponse.text();
            } else {
                String tagPrompt = "Based on the conversation:\n" + fullConversation +
                        "\nExtract 3 to 6 appropriate tags for cube recommendation from: cheap;budget;fast;premium;magnetic;customizable;coremagnet;stickerless;bluetooth;uvcoated. " +
                        "Output only tags separated by semicolons.";
                GenerateContentResponse tagResponse = client.models.generateContent(MODEL, tagPrompt, null);
                return tagResponse.text().toLowerCase().replaceAll("\\s+", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fast;controllable;3x3;cheap";
        }
    }
}

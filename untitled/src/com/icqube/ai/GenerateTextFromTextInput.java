package com.icqube.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GenerateTextFromTextInput {
    public static void main(String[] args) {
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Does horchata have eggs in it?",
                        null);

        System.out.println(response.text());
        //System.getenv("GOOGLE_API_KEY");
    }
}
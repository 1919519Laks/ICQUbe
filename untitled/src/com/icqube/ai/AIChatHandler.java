package com.icqube.ai;

import java.net.*;
import java.io.*;

public class AIChatHandler {

    private static final String API_URL = "https://api.llama.ai/v1/chat";

    // Sends user prompt to AI, retrieves tags (placeholder)
    public static String getCubeTags(String userPrompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"prompt\": \"" + userPrompt + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("utf-8"));
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) response.append(line.trim());
            }

            return response.toString();
        } catch (Exception e) {
            return "fast;controllable;3x3;under20";
        }
    }
}

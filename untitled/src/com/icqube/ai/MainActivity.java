package com.icqube.ai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity {

    private static final java.util.List<String> conversationHistory = new java.util.ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ICQube Chatbot");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 600);

            JTextPane chatPane = new JTextPane();
            chatPane.setEditable(false);
            chatPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            chatPane.setBorder(new EmptyBorder(8, 8, 8, 8));
            chatPane.setBackground(Color.WHITE);

            JScrollPane chatScroll = new JScrollPane(chatPane,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            chatScroll.setBorder(null);

            JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
            inputPanel.setBackground(new Color(245, 248, 250));
            inputPanel.setBorder(new EmptyBorder(14, 10, 14, 10));

            JTextField inputField = new JTextField();
            inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 220, 240)),
                    new EmptyBorder(7, 7, 7, 7)
            ));
            inputField.setToolTipText("Type your cube request or chat...");

            JButton sendBtn = new JButton("Send");
            sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            sendBtn.setBackground(new Color(51, 153, 255));
            sendBtn.setForeground(Color.WHITE);
            sendBtn.setFocusPainted(false);

            inputPanel.add(inputField, BorderLayout.CENTER);
            inputPanel.add(sendBtn, BorderLayout.EAST);

            JLabel spinnerLabel = new JLabel();
            spinnerLabel.setVisible(false);
            try {
                URL spinnerUrl = new URL("https://i.imgur.com/6RMhx.gif"); // Spinner gif url
                spinnerLabel.setIcon(new ImageIcon(spinnerUrl));
            } catch (Exception e) {
                System.out.println("Failed to load spinner icon.");
            }

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(inputPanel, BorderLayout.CENTER);
            bottomPanel.add(spinnerLabel, BorderLayout.EAST);

            frame.setLayout(new BorderLayout());
            frame.add(chatScroll, BorderLayout.CENTER);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            StyledDocument doc = chatPane.getStyledDocument();

            Style userStyle = doc.addStyle("User", null);
            StyleConstants.setForeground(userStyle, new Color(41, 128, 185));
            StyleConstants.setBold(userStyle, true);

            Style botStyle = doc.addStyle("Bot", null);
            StyleConstants.setForeground(botStyle, new Color(44, 62, 80));
            StyleConstants.setBold(botStyle, false);

            Runnable clearInputError = () -> inputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 220, 240)),
                    new EmptyBorder(7, 7, 7, 7)
            ));

            sendBtn.addActionListener(e -> {
                String userPrompt = inputField.getText().trim();
                if (userPrompt.isEmpty()) {
                    inputField.setBorder(BorderFactory.createLineBorder(Color.RED));
                    return;
                }
                clearInputError.run();
                inputField.setText("");
                appendMessage(doc, "You: " + userPrompt + "\n", userStyle);

                conversationHistory.add("User: " + userPrompt);

                StringBuilder conversationBuilder = new StringBuilder();
                for (String line : conversationHistory) {
                    conversationBuilder.append(line).append("\n");
                }
                conversationBuilder.append("AI:");

                final String promptForAI = conversationBuilder.toString();

                new SwingWorker<Void, Void>() {
                    String aiOutput, response;

                    @Override
                    protected Void doInBackground() {
                        SwingUtilities.invokeLater(() -> spinnerLabel.setVisible(true));
                        // Get AI response: tags or chat reply
                        aiOutput = AIChatHandler.getResponseWithContext(promptForAI);
                        if (aiOutput.startsWith("[chat]")) {
                            response = aiOutput.substring(6).trim();
                        } else {
                            // Normalize tags before searching
                            String normalizedTags = AIChatHandler.mapUserTagsToCanonical(aiOutput);
                            response = CubeRecommender.getRecommendedCube(normalizedTags);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        spinnerLabel.setVisible(false);
                        appendMessage(doc, "Bot: " + response + "\n\n", botStyle);
                        conversationHistory.add("AI: " + response);
                        chatPane.setCaretPosition(chatPane.getDocument().getLength());
                    }
                }.execute();
            });

            frame.setVisible(true);
        });
    }

    private static void appendMessage(StyledDocument doc, String msg, Style style) {
        try {
            doc.insertString(doc.getLength(), msg, style);
        } catch (Exception ignored) {
        }
    }
}

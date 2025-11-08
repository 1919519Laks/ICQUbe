package com.icqube.ai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainActivity {

    /*
     * Pseudocode:
     * 1. Create Swing window
     * 2. Add input field, button, and text area
     * 3. On button click, call AI handler
     * 4. Pass AI output to CubeRecommender
     * 5. Display result in text area
     */

    public static void main(String[] args) {
        JFrame frame = new JFrame("ICQube AI Cube Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("🧊 ICQube: AI Cube Finder", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField inputField = new JTextField();
        inputField.setToolTipText("Describe your ideal cube...");

        JButton findButton = new JButton("Find Cube");

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        frame.add(title, BorderLayout.NORTH);
        frame.add(inputField, BorderLayout.CENTER);
        frame.add(findButton, BorderLayout.EAST);
        frame.add(scrollPane, BorderLayout.SOUTH);

        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userPrompt = inputField.getText().trim();
                if (userPrompt.isEmpty()) {
                    outputArea.setText("Please enter your cube preferences!");
                    return;
                }

                outputArea.setText("Analyzing your request...\n");

                // Step 1: Get AI tags
                String aiTags = AIChatHandler.getCubeTags(userPrompt);
                outputArea.append("AI extracted tags: " + aiTags + "\n\n");

                // Step 2: Recommend cube
                String recommendation = CubeRecommender.getRecommendedCube(aiTags);
                outputArea.append("Best match:\n" + recommendation);
            }
        });

        frame.setVisible(true);
    }
}

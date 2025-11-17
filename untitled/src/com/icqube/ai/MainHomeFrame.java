package com.icqube.ai;

import javax.swing.*;
import java.awt.*;

public class MainHomeFrame extends JFrame {

    private final String username;

    private final JTextField searchField;
    private final JTextArea resultArea;
    private final JButton searchButton;
    private final JButton saveButton;
    private final JButton aiChatButton;
    private final JButton filtersButton;
    private final JButton savedButton;
    private final JButton logoutButton;

    public MainHomeFrame(String username) {
        super("ICQUbe - Home");
        this.username = username;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 800);      // phone-ish dimensions
        setLocationRelativeTo(null);

        // Root layout
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: greeting + search bar
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        JLabel greeting = new JLabel("Welcome, " + username + "!");
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(greeting, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchField.setToolTipText("Search for a cube (e.g. 'fast budget 3x3')");

        searchButton = new JButton("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Center: result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Below result: action buttons (save + AI chat)
        JPanel midButtons = new JPanel(new GridLayout(1, 2, 5, 5));
        saveButton = new JButton("Save Current Result");
        aiChatButton = new JButton("Open AI Chatbot");
        midButtons.add(saveButton);
        midButtons.add(aiChatButton);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(midButtons, BorderLayout.SOUTH);

        // Bottom: navigation / extra tools
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        filtersButton = new JButton("Filters");
        savedButton = new JButton("Saved");
        logoutButton = new JButton("Logout");
        bottomPanel.add(filtersButton);
        bottomPanel.add(savedButton);
        bottomPanel.add(logoutButton);

        // Assemble
        root.add(topPanel, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);

        hookEvents();
    }

    private void hookEvents() {
        // Main search using cube recommender
        searchButton.addActionListener(e -> performSearch());

        // Save last result into favorites
        saveButton.addActionListener(e -> {
            SavedFavoritesManager.saveLastResult();
            JOptionPane.showMessageDialog(this,
                    SavedFavoritesManager.hasFavorites()
                            ? "Current result saved to favorites."
                            : "Nothing to save yet.",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Open full AI chat window (Lucky's UI)
        aiChatButton.addActionListener(e -> {
            chatUI ui = new chatUI();
            ui.setVisible(true);
        });

        // Open Filters window (your FP)
        filtersButton.addActionListener(e -> {
            try {
                new FiltersFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Filters page is not available. Make sure FiltersFrame exists.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Open Saved favorites list
        savedButton.addActionListener(e -> new SavedFavoritesFrame().setVisible(true));

        // Logout back to login
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Log out and return to login screen?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter something to search for.");
            return;
        }

        try {
            // Use Lucky's CubeRecommender to get a recommendation.
            // This assumes a method: getRecommendedCube(String tags, String brand, Double minPrice, Double maxPrice)
            String result = CubeRecommender.getRecommendedCube(query, null, null, null);

            if (result == null || result.isBlank()) {
                resultArea.setText("No cubes matched that search. Try different keywords.");
            } else {
                resultArea.setText(result);
            }

            // Store last result so it can be saved
            SavedFavoritesManager.setLastResult(result);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred while searching.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

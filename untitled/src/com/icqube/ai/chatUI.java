package com.icqube.ai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

public class chatUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(chatUI.class.getName());
    private java.util.List<String> conversationHistory = new ArrayList<>();
    private JTextPane chatPane;
    private Style userStyle, botStyle;

    private void appendMessage(String msg, Style style) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            doc.insertString(doc.getLength(), msg, style);
        } catch (Exception ignored) {}
    }

    private void showSpinnerInChat() {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            doc.insertString(doc.getLength(), "\n[Loading...]\n", botStyle);
            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        } catch (Exception ignored) {}
    }

    private void removeSpinnerFromChat() {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            String text = doc.getText(0, doc.getLength());
            int spinnerIndex = text.lastIndexOf("[Loading...]");
            if (spinnerIndex != -1) {
                doc.remove(spinnerIndex, "[Loading...]".length() + 2);
            }
        } catch (Exception ignored) {}
    }

    private void appendMessageWithFeedback(String msg, Style style, String aiResponse) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            doc.insertString(doc.getLength(), msg, style);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JButton thumbsUp = new JButton("👍");
            JButton thumbsDown = new JButton("👎");
            buttonPanel.add(thumbsUp);
            buttonPanel.add(thumbsDown);

            thumbsUp.addActionListener(e -> System.out.println("Thumbs up for: " + aiResponse));
            thumbsDown.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(chatUI.this, "Why didn't you like this response?");
                if (reason != null && !reason.trim().isEmpty()) {
                    System.out.println("Feedback: " + aiResponse + " - " + reason);
                    appendMessage("Thanks for your feedback!\n", botStyle);
                }
            });

            doc.insertString(doc.getLength(), "\n[Feedback: 👍 👎]\n", botStyle);
        } catch (Exception ignored) {}
    }

    public chatUI() {
        initComponents();
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chatPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        chatPane.setBackground(Color.WHITE);
        jScrollPane1.setViewportView(chatPane);

        StyledDocument doc = chatPane.getStyledDocument();
        userStyle = doc.addStyle("User", null);
        StyleConstants.setForeground(userStyle, new Color(41, 128, 185));
        StyleConstants.setBold(userStyle, true);

        botStyle = doc.addStyle("Bot", null);
        StyleConstants.setForeground(botStyle, new Color(44, 62, 80));
        StyleConstants.setBold(botStyle, false);
    }

    private void initComponents() {
        jScrollBar1 = new javax.swing.JScrollBar();
        scrollPane1 = new java.awt.ScrollPane();
        textField5 = new java.awt.TextField();
        textField4 = new java.awt.TextField();
        textField3 = new java.awt.TextField();
        textField2 = new java.awt.TextField();
        textField1 = new java.awt.TextField();
        panel1 = new java.awt.Panel();
        jTextField1 = new javax.swing.JTextField();
        button1 = new javax.swing.JButton();
        button2 = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        textField6 = new java.awt.TextField();
        button5 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();

        textField5.setText("textField5");
        scrollPane1.add(textField5);

        textField4.setText("textField4");
        scrollPane1.add(textField4);

        textField3.setText("textField3");
        scrollPane1.add(textField3);

        textField2.setText("textField2");
        scrollPane1.add(textField2);

        textField1.setText("textField1");
        scrollPane1.add(textField1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel1.setBackground(new java.awt.Color(89, 88, 88));

        jTextField1.setText("Search for your cube. DO NOT USE, NOT FUNCTIONAL");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jTextField1)
                                .addGap(28, 28, 28))
        );

        button1.setText("Home");
        button2.setText("Account");
        button3.setText("Cart");
        button4.setText("Saved");

        textField6.setText("Type ");
        textField6.addActionListener(this::textField6ActionPerformed);

        button5.setBackground(new java.awt.Color(174, 213, 255));
        button5.setText("Send");
        button5.addActionListener(e -> {
            String userPrompt = textField6.getText().trim();
            if (userPrompt.isEmpty()) {
                textField6.setBackground(Color.PINK);
                return;
            }
            textField6.setBackground(Color.WHITE);
            textField6.setText("");
            appendMessage("You: " + userPrompt + "\n", userStyle);
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
                    SwingUtilities.invokeLater(() -> showSpinnerInChat());
                    aiOutput = AIChatHandler.getResponseWithContext(promptForAI);
                    if (aiOutput.startsWith("[chat]")) {
                        response = aiOutput.substring(6).trim();
                    } else {
                        String normalizedTags = AIChatHandler.mapUserTagsToCanonical(aiOutput);
                        String[] filterDetails = AIChatHandler.extractFilterDetails(userPrompt);
                        String brand = filterDetails[0];
                        Double minPrice = filterDetails[1].isEmpty() ? null : Double.valueOf(filterDetails[1]);
                        Double maxPrice = filterDetails[2].isEmpty() ? null : Double.valueOf(filterDetails[2]);

                        boolean useFilters = (brand != null && !brand.isEmpty()) || minPrice != null || maxPrice != null;
                        if (useFilters) {
                            response = CubeRecommender.getRecommendedCube(normalizedTags, brand, minPrice, maxPrice);
                        } else {
                            response = CubeRecommender.getRecommendedCube(normalizedTags);
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    removeSpinnerFromChat();
                    if (aiOutput.startsWith("[chat]")) {
                        appendMessage("Bot: " + response + "\n\n", botStyle);
                    } else {
                        appendMessageWithFeedback("Bot: " + response + "\n\n", botStyle, response);
                    }
                    conversationHistory.add("AI: " + response);
                    chatPane.setCaretPosition(chatPane.getDocument().getLength());
                }
            }.execute();
        });
        textField6.addActionListener(e -> button5.doClick());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textField6, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button5, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jSeparator1)
                                .addContainerGap())
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(textField6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(button5, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24))
        );

        pack();
    }

    private void textField6ActionPerformed(java.awt.event.ActionEvent evt) {
        button5.doClick();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify
    private javax.swing.JScrollBar jScrollBar1;
    private java.awt.ScrollPane scrollPane1;
    private java.awt.TextField textField5;
    private java.awt.TextField textField4;
    private java.awt.TextField textField3;
    private java.awt.TextField textField2;
    private java.awt.TextField textField1;
    private java.awt.Panel panel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton button1;
    private javax.swing.JButton button2;
    private javax.swing.JButton button3;
    private javax.swing.JButton button4;
    private java.awt.TextField textField6;
    private javax.swing.JButton button5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JScrollPane jScrollPane1;
}

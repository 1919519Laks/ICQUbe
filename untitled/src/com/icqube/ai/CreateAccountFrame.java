package com.icqube.ai;

import javax.swing.*;
import java.awt.*;

public class CreateAccountFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JButton createButton;

    private final AuthService authService;

    public CreateAccountFrame() {
        super("Create ICQUbe Account");

        authService = new AuthService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmField = new JPasswordField(15);
        emailField = new JTextField(15);
        phoneField = new JTextField(15);
        createButton = new JButton("Create Account");

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Email (optional):"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Phone (optional):"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(createButton, gbc);

        setContentPane(panel);

        hookEvents();
    }

    private void hookEvents() {
        createButton.addActionListener(e -> handleCreateAccount());
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String pw = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.");
            return;
        }

        if (!pw.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        boolean success = authService.createUser(username, pw, email, phone);
        if (!success) {
            JOptionPane.showMessageDialog(this,
                    "Could not create account. Username may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Account created! You can now log in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose(); // close the sign-up window
        }
    }
}

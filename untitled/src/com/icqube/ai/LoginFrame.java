package com.icqube.ai;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton forgotButton;
    private final JButton createAccountButton;


    private final AuthService authService;

    public LoginFrame() {
        super("ICQUbe Login");
        authService = new AuthService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 520);
        setSize(350, 500);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ICQUbe Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        forgotButton = new JButton("Forgot Password");
        createAccountButton = new JButton("Create Account");

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        main.add(title, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = y;
        main.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        main.add(usernameField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        main.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        main.add(passwordField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        main.add(loginButton, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        main.add(forgotButton, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        main.add(createAccountButton, gbc);

        setContentPane(main);

        hookEvents();
    }

    private void hookEvents() {
        loginButton.addActionListener(e -> doLogin());
        forgotButton.addActionListener(e -> doForgot());
        createAccountButton.addActionListener(e -> openCreateAccount());
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        if (!authService.validateCredentials(username, password)) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            return;
        }

        String code = authService.startTwoFactor(username);
        JOptionPane.showMessageDialog(this,
                "2FA code (demo): " + code,
                "Two-Factor Authentication",
                JOptionPane.INFORMATION_MESSAGE);

        String entered = JOptionPane.showInputDialog(this, "Enter the 6-digit code:");
        if (entered == null) {
            JOptionPane.showMessageDialog(this, "2FA cancelled.");
            return;
        }

        if (authService.verifyTwoFactor(username, entered.trim())) {
            JOptionPane.showMessageDialog(this, "Login successful!");

            MainHomeFrame home = new MainHomeFrame(username);
            home.setVisible(true);
            this.dispose();


        } else {
            JOptionPane.showMessageDialog(this, "Incorrect 2FA code.");
        }
    }

    private void doForgot() {
        String username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username == null || username.trim().isEmpty()) return;

        if (!authService.userExists(username.trim())) {
            JOptionPane.showMessageDialog(this, "No account found with that username.");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Password reset process initiated (demo).\n" +
                        "Explain the full flow in the video/report.");
    }

    private void openCreateAccount() {
        new CreateAccountFrame().setVisible(true);
    }
}

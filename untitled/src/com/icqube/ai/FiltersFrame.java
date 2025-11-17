package com.icqube.ai;

import javax.swing.*;
import java.awt.*;

public class FiltersFrame extends JFrame {

    private final JTextField tagsField;
    private final JTextField brandField;
    private final JTextField minPriceField;
    private final JTextField maxPriceField;
    private final JButton searchButton;
    private final JTextArea resultArea;

    public FiltersFrame() {
        super("Cube Filters");
        setSize(400, 500);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tagsField = new JTextField(20);
        brandField = new JTextField(20);
        minPriceField = new JTextField(8);
        maxPriceField = new JTextField(8);
        searchButton = new JButton("Search");
        resultArea = new JTextArea(8, 30);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Tags (fast, budget, etc.):"), gbc);
        gbc.gridx = 1;
        form.add(tagsField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1;
        form.add(brandField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Min Price:"), gbc);
        gbc.gridx = 1;
        form.add(minPriceField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Max Price:"), gbc);
        gbc.gridx = 1;
        form.add(maxPriceField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        form.add(searchButton, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        form.add(new JScrollPane(resultArea), gbc);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);

        searchButton.addActionListener(e -> doSearch());
    }

    private void doSearch() {
        String tags = tagsField.getText().trim();
        String brand = brandField.getText().trim();
        String minText = minPriceField.getText().trim();
        String maxText = maxPriceField.getText().trim();

        Double min = minText.isEmpty() ? null : Double.valueOf(minText);
        Double max = maxText.isEmpty() ? null : Double.valueOf(maxText);
        if (min != null && max != null && max < min) {
            JOptionPane.showMessageDialog(this, "Max price must be >= min price.");
            return;
        }

        String result = CubeRecommender.getRecommendedCube(
                tags,
                brand.isEmpty() ? null : brand,
                min,
                max
        );

        resultArea.setText(result);
    }
}

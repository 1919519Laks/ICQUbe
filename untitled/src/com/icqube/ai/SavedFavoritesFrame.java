package com.icqube.ai;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SavedFavoritesFrame extends JFrame {

    public SavedFavoritesFrame() {
        super("Saved Cubes");
        setSize(350, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<String> favorites = SavedFavoritesManager.getFavorites();

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        if (favorites.isEmpty()) {
            area.setText("You have no saved cubes yet.\nRun a search on the home page and click 'Save Current Result'.");
        } else {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (String fav : favorites) {
                sb.append("⭐ Favorite ").append(i++).append(":\n");
                sb.append(fav).append("\n\n");
            }
            area.setText(sb.toString());
        }

        JScrollPane scrollPane = new JScrollPane(area);

        add(scrollPane, BorderLayout.CENTER);
    }
}

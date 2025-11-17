package com.icqube.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SavedFavoritesManager {

    private static final List<String> favorites = new ArrayList<>();
    private static String lastResult;

    public static void setLastResult(String result) {
        lastResult = result;
    }

    public static void saveLastResult() {
        if (lastResult != null && !lastResult.isBlank()) {
            favorites.add(lastResult);
        }
    }

    public static List<String> getFavorites() {
        return Collections.unmodifiableList(favorites);
    }

    public static boolean hasFavorites() {
        return !favorites.isEmpty();
    }
}

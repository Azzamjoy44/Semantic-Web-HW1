package com.recipeapp.util;

import java.util.List;

public final class AppConstants {

    private AppConstants() {}

    public static final List<String> DIFFICULTY_LEVELS = List.of("Beginner", "Intermediate", "Advanced");

    public static final List<String> CUISINE_TYPES = List.of(
        "Italian", "French", "Indian", "Chinese", "Japanese",
        "Thai", "Spanish", "Greek", "Mediterranean", "Mexican",
        "American", "British", "European", "African", "Asian"
    );
}

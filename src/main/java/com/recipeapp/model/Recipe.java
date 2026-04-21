package com.recipeapp.model;

import java.util.List;

public class Recipe {
    private String id;
    private String title;
    private List<String> cuisines;        // exactly 2
    private String primaryDifficulty;
    private List<String> supportedLevels; // exactly 3
    private String description;
    private int prepTime;
    private int cookTime;

    public Recipe() {}

    public Recipe(String id, String title, List<String> cuisines, String primaryDifficulty,
                  List<String> supportedLevels, String description, int prepTime, int cookTime) {
        this.id = id;
        this.title = title;
        this.cuisines = cuisines;
        this.primaryDifficulty = primaryDifficulty;
        this.supportedLevels = supportedLevels;
        this.description = description;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getCuisines() { return cuisines; }
    public void setCuisines(List<String> cuisines) { this.cuisines = cuisines; }

    public String getPrimaryDifficulty() { return primaryDifficulty; }
    public void setPrimaryDifficulty(String primaryDifficulty) { this.primaryDifficulty = primaryDifficulty; }

    public List<String> getSupportedLevels() { return supportedLevels; }
    public void setSupportedLevels(List<String> supportedLevels) { this.supportedLevels = supportedLevels; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }

    public int getCookTime() { return cookTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }

    public int getTotalTime() { return prepTime + cookTime; }

    public String getCuisinesDisplay() {
        return cuisines != null ? String.join(", ", cuisines) : "";
    }

    public String getSupportedLevelsDisplay() {
        return supportedLevels != null ? String.join(", ", supportedLevels) : "";
    }
}

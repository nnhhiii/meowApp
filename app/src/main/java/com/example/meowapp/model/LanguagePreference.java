package com.example.meowapp.model;

public class LanguagePreference {

    private String language_id, user_id, level_id;
    private int language_score;

    public LanguagePreference() {
    }

    public LanguagePreference(String language_id, String user_id, String level_id, int language_score) {
        this.language_id = language_id;
        this.user_id = user_id;
        this.level_id = level_id;
        this.language_score = language_score;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return level_id;
    }

    public void setId(String id) {
        this.level_id = level_id;
    }

    public int getLanguage_score() {
        return language_score;
    }

    public void setLanguage_score(int language_score) {
        this.language_score = language_score;
    }
}

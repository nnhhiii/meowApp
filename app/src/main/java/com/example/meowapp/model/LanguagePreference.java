package com.example.meowapp.model;

import android.text.TextUtils;

public class LanguagePreference {

    private String language_id, user_id, level_id, language_name;
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

    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
    }

    public int getLanguage_score() {
        return language_score;
    }

    public void setLanguage_score(int language_score) {
        this.language_score = language_score;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

}

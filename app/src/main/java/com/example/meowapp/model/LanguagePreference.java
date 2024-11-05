package com.example.meowapp.model;

public class LanguagePreference {
    String language_id, user,id;
    int language_score;

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLanguage_score() {
        return language_score;
    }

    public void setLanguage_score(int language_score) {
        this.language_score = language_score;
    }
}

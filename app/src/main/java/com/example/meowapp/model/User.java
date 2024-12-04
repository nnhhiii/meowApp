package com.example.meowapp.model;

import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.gson.annotations.SerializedName;

public class User {
    private String username, email, password, role, avatar, language_id, created_at, updated_at;
    private int score, streaks, diamonds, hearts, lessons, eightyLessons, perfectLessons ;

    public int getLessons() {
        return lessons;
    }

    public void setLessons(int lessons) {
        this.lessons = lessons;
    }

    public int getEightyLessons() {
        return eightyLessons;
    }

    public void setEightyLessons(int eightyLessons) {
        this.eightyLessons = eightyLessons;
    }

    public int getPerfectLessons() {
        return perfectLessons;
    }

    public void setPerfectLessons(int perfectLessons) {
        this.perfectLessons = perfectLessons;
    }

    public int getStreaks() {
        return streaks;
    }

    public void setStreaks(int streaks) {
        this.streaks = streaks;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getLanguage_id() {
        return language_id;
    }
    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
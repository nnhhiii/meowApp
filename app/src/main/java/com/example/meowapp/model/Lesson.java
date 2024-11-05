package com.example.meowapp.model;
import android.graphics.Point;

public class Lesson {
    private String lesson_name;
    private int lesson_score;
    private String language_id;
    private String level_id;
    private String created_at;
    private String updated_at;

    public String getLesson_name() {
        return lesson_name;
    }

    public void setLesson_name(String lesson_name) {
        this.lesson_name = lesson_name;
    }

    public int getLesson_score() {
        return lesson_score;
    }

    public void setLesson_score(int lesson_score) {
        this.lesson_score = lesson_score;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
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
}

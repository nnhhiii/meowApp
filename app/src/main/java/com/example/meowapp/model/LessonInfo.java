package com.example.meowapp.model;

public class LessonInfo {
    private String lessonId;
    private Lesson lesson;
    private String languageName;
    private String levelName;
    private int score;

    public LessonInfo(String lessonId, Lesson lesson, String languageName, String levelName, int score) {
        this.lessonId = lessonId;
        this.lesson = lesson;
        this.languageName = languageName;
        this.levelName = levelName;
        this.score = score;
    }

    // Getter methods for the properties
    public String getLessonId() {
        return lessonId;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getScore() {
        return score;
    }
}


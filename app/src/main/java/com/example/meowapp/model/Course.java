package com.example.meowapp.model;

public class Course {
    private String id; // Thêm thuộc tính id
    private String name;
    private String language;

    // Constructor
    public Course(String id, String name, String language) {
        this.id = id;
        this.name = name;
        this.language = language;
    }

    // Getter và Setter cho id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho language
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

package com.example.meowapp.model;

public class Course {
    private String name;
    private String description;

    // Constructor, Getter và Setter
    public Course(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

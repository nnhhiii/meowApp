package com.example.meowapp.model;

import java.sql.Timestamp;
import java.util.List;

public class Question {
    private String question_text, lesson_id, correct_answer,
            option_a, option_b,option_c,option_d, question_type,
            image_option_a, image_option_b, image_option_c, image_option_d;
    private List<String> order_word;
    private Timestamp created_at, updated_at;
    public Question() {
    }
    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public List<String> getOrder_word() {
        return order_word;
    }

    public void setOrder_word(List<String> order_word) {
        this.order_word = order_word;
    }

    public String getImage_option_a() {
        return image_option_a;
    }

    public void setImage_option_a(String image_option_a) {
        this.image_option_a = image_option_a;
    }

    public String getImage_option_b() {
        return image_option_b;
    }

    public void setImage_option_b(String image_option_b) {
        this.image_option_b = image_option_b;
    }

    public String getImage_option_c() {
        return image_option_c;
    }

    public void setImage_option_c(String image_option_c) {
        this.image_option_c = image_option_c;
    }

    public String getImage_option_d() {
        return image_option_d;
    }

    public void setImage_option_d(String image_option_d) {
        this.image_option_d = image_option_d;
    }

    public static class User {
        private String name;
        private String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}


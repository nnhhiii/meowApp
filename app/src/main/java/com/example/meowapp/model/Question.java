package com.example.meowapp.model;

public class Question {
    private String question_text, lesson_id, correct_answer, order_words,
            option_a, option_b, option_c, option_d, question_type,
            image_option_a, image_option_b, image_option_c, image_option_d,
            created_at, updated_at, level_id, language_id; // Thêm level_id và language_id

    public Question() {
    }

    // Getter và Setter cho level_id
    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
    }

    // Getter và Setter cho language_id
    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getOrder_words() {
        return order_words;
    }

    public void setOrder_words(String order_words) {
        this.order_words = order_words;
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
}

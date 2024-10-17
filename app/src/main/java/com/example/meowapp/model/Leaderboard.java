package com.example.meowapp.model;

import com.google.gson.annotations.SerializedName;

public class Leaderboard {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String userName;

    private int rank;

    @SerializedName("total_score")
    private int totalScore;

    @SerializedName("user_image_url")
    private String userImageUrl;


    public Leaderboard(String userId, int rank, int totalScore, String userImageUrl) {
        this.userId = userId;
        this.rank = rank;
        this.totalScore = totalScore;
        this.userImageUrl = userImageUrl;
    }

    // Getters và setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

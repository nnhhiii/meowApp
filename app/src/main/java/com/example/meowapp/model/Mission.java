package com.example.meowapp.model;

public class Mission {
    private String missionName;
    private String rewardType;
    private String created_at;
    private String updated_at;
    private int rewardAmount, requiredScore, requiredPerfectLessons, requiredEightyLessons, requiredLessons, requiredStreaks;

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(int requiredScore) {
        this.requiredScore = requiredScore;
    }

    public int getRequiredPerfectLessons() {
        return requiredPerfectLessons;
    }

    public void setRequiredPerfectLessons(int requiredPerfectLessons) {
        this.requiredPerfectLessons = requiredPerfectLessons;
    }

    public int getRequiredEightyLessons() {
        return requiredEightyLessons;
    }

    public void setRequiredEightyLessons(int requiredEightyLessons) {
        this.requiredEightyLessons = requiredEightyLessons;
    }

    public int getRequiredLessons() {
        return requiredLessons;
    }

    public void setRequiredLessons(int requiredLessons) {
        this.requiredLessons = requiredLessons;
    }

    public int getRequiredStreaks() {
        return requiredStreaks;
    }

    public void setRequiredStreaks(int requiredStreaks) {
        this.requiredStreaks = requiredStreaks;
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

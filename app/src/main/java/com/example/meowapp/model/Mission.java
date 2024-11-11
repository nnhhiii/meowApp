package com.example.meowapp.model;

public class Mission {
    private String missionName;
    private String rewardType;
    private int rewardAmount, requiredScore, required100PercentLessons, required80PercentLessons, requiredLessons, requiredStreaks;

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

    public int getRequired100PercentLessons() {
        return required100PercentLessons;
    }

    public void setRequired100PercentLessons(int required100PercentLessons) {
        this.required100PercentLessons = required100PercentLessons;
    }

    public int getRequired80PercentLessons() {
        return required80PercentLessons;
    }

    public void setRequired80PercentLessons(int required80PercentLessons) {
        this.required80PercentLessons = required80PercentLessons;
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
}

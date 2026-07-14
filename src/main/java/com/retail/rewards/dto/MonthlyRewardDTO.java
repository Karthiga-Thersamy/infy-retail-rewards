package com.retail.rewards.dto;

public class MonthlyRewardDTO {
    private String month;
    private double rewardPoints;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(double rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}

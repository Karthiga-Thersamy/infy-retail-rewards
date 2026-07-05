package com.retail.rewards.util;

public class RewardsCalculator {
    public static double calculatePoints(double amount) {
        double points = 0.0;
        if (amount > 100) {
            points += (amount - 100) * 2; // 2 points per $1 over 100
            points += 50; // guaranteed 50 points for $50-$100 range
        } else if (amount > 50) {
            points += (amount - 50); // 1 point per $1 between 50 and 100
        }
        return points;
    }
}


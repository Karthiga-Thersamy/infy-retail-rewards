package com.retail.rewards.util;

public class RewardsCalculator {
    private static final double FIRST_THRESHOLD = 50.0;
    private static final double SECOND_THRESHOLD = 100.0;
    private static final double SECOND_THRESHOLD_BONUS = SECOND_THRESHOLD - FIRST_THRESHOLD;

    public static double calculatePoints(double amount) {
        if (amount > SECOND_THRESHOLD) {
            return (amount - SECOND_THRESHOLD) * 2 + SECOND_THRESHOLD_BONUS;
        }

        if (amount > FIRST_THRESHOLD) {
            return amount - FIRST_THRESHOLD;
        }

        return 0.0;
    }
}


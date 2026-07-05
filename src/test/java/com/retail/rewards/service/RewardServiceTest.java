package com.retail.rewards.service;

import com.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RewardServiceTest {

    @Test
    void testAmountBelow50() {
        // No points should be awarded
        assertEquals(0.0, RewardsCalculator.calculatePoints(49.99), 0.01);
    }

    @Test
    void testAmountExactly50() {
        // Boundary case: still no points
        assertEquals(0.0, RewardsCalculator.calculatePoints(50.0), 0.01);
    }

    @Test
    void testAmountBetween50And100() {
        // $75.50 → 25.5 points
        assertEquals(25.5, RewardsCalculator.calculatePoints(75.50), 0.01);
    }

    @Test
    void testAmountExactly100() {
        // $100 → 50 points
        assertEquals(50.0, RewardsCalculator.calculatePoints(100.0), 0.01);
    }

    @Test
    void testAmountJustAbove100() {
        // $100.25 → 50 + (0.25 * 2) = 50.5 points
        assertEquals(50.5, RewardsCalculator.calculatePoints(100.25), 0.01);
    }

    @Test
    void testHighValueAmount() {
        // $200.25 → 50 + (100.25 * 2) = 250.5 points
        assertEquals(250.5, RewardsCalculator.calculatePoints(200.25), 0.01);
    }

    @Test
    void testNegativeAmount() {
        // Defensive case: negative purchases should yield 0 points
        assertEquals(0.0, RewardsCalculator.calculatePoints(-20.0), 0.01);
    }

    @Test
    void testZeroAmount() {
        // $0 → 0 points
        assertEquals(0.0, RewardsCalculator.calculatePoints(0.0), 0.01);
    }
}

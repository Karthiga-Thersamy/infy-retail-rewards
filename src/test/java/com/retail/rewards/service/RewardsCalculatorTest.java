package com.retail.rewards.service;

import com.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RewardsCalculatorTest {

    @Test
    void belowFirstThreshold() {
        assertEquals(0.0, RewardsCalculator.calculatePoints(49.99), 0.01);
    }

    @Test
    void atFirstThreshold() {
        assertEquals(0.0, RewardsCalculator.calculatePoints(50.0), 0.01);
    }

    @Test
    void betweenThresholds() {
        assertEquals(25.5, RewardsCalculator.calculatePoints(75.50), 0.01);
    }

    @Test
    void atSecondThreshold() {
        assertEquals(50.0, RewardsCalculator.calculatePoints(100.0), 0.01);
    }

    @Test
    void justAboveSecondThreshold() {
        assertEquals(50.5, RewardsCalculator.calculatePoints(100.25), 0.01);
    }

    @Test
    void wellAboveSecondThreshold() {
        assertEquals(250.5, RewardsCalculator.calculatePoints(200.25), 0.01);
    }

    @Test
    void negativeAmount() {
        assertEquals(0.0, RewardsCalculator.calculatePoints(-20.0), 0.01);
    }

    @Test
    void zeroAmount() {
        assertEquals(0.0, RewardsCalculator.calculatePoints(0.0), 0.01);
    }
}

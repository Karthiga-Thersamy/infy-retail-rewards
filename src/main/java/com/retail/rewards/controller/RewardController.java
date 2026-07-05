package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardResponseDTO;
import com.retail.rewards.service.RewardService;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<RewardResponseDTO> getRewards(
            @PathVariable Long customerId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth ym = YearMonth.of(year, month);
        return ResponseEntity.ok(rewardService.getRewards(customerId, ym));
    }
}

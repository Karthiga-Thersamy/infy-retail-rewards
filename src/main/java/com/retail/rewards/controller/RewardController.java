package com.retail.rewards.controller;

import com.retail.rewards.dto.RewardResponseDTO;
import com.retail.rewards.dto.RewardSummaryResponseDTO;
import com.retail.rewards.service.RewardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.time.LocalDate;


@RestController
@Validated
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;
    private static final int DEFAULT_MONTHS = 3;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<RewardResponseDTO> getRewards(
            @PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) @Min(1) Integer numberOfMonths) {
        LocalDate[] range = resolveDateRange(startDate, endDate, numberOfMonths);
        if (range == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(rewardService.getRewards(customerId, range[0], range[1]));
    }

    @GetMapping("/summary")
    public ResponseEntity<RewardSummaryResponseDTO> getRewardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) @Min(1) Integer numberOfMonths) {
        LocalDate[] range = resolveDateRange(startDate, endDate, numberOfMonths);
        if (range == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(rewardService.getRewardSummary(range[0], range[1]));
    }

    private LocalDate[] resolveDateRange(LocalDate startDate, LocalDate endDate, Integer numberOfMonths) {
        int months = numberOfMonths == null ? DEFAULT_MONTHS : numberOfMonths;

        if (startDate != null && endDate != null) {
            return new LocalDate[]{startDate, endDate};
        }

        if (startDate != null) {
            return new LocalDate[]{startDate, startDate.plusMonths(months).minusDays(1)};
        }

        if (endDate != null) {
            return new LocalDate[]{endDate.minusMonths(months).plusDays(1), endDate};
        }

        LocalDate today = LocalDate.now();
        return new LocalDate[]{today.minusMonths(months).plusDays(1), today};
    }
}

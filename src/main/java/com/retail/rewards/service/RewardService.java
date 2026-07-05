package com.retail.rewards.service;

import com.retail.rewards.dto.RewardResponseDTO;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class RewardService {

    @Autowired
    private TransactionRepository transactionRepository;

    public RewardResponseDTO getRewards(Long customerId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(customerId, start, end);

        if (transactions.isEmpty()) {
            throw new CustomerNotFoundException(customerId);
        }

        double monthlyPoints = transactions.stream()
                .mapToDouble(t -> RewardsCalculator.calculatePoints(t.getAmount()))
                .sum();

        RewardResponseDTO dto = new RewardResponseDTO();
        dto.setCustomerId(customerId);
        dto.setMonth(month.toString());
        dto.setPoints(monthlyPoints);
        dto.setTransactions(transactions);
        return dto;
    }
}

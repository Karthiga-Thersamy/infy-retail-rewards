package com.retail.rewards.service;

import com.retail.rewards.dto.CustomerRewardsSummaryDTO;
import com.retail.rewards.dto.MonthlyRewardDTO;
import com.retail.rewards.dto.RewardResponseDTO;
import com.retail.rewards.dto.RewardSummaryResponseDTO;
import com.retail.rewards.dto.TransactionResponseDTO;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public RewardService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public RewardResponseDTO getRewards(Long customerId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return getRewards(customerId, start, end);
    }

    public RewardResponseDTO getRewards(Long customerId, LocalDate start, LocalDate end) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(customerId, start, end);

        double totalRewardPoints = transactions.stream()
                .mapToDouble(t -> RewardsCalculator.calculatePoints(t.getAmount()))
                .sum();

        List<TransactionResponseDTO> transactionDTOs = transactions.stream()
                .map(t -> {
                    TransactionResponseDTO dto = new TransactionResponseDTO();
                    dto.setId(t.getId());
                    dto.setCustomerId(t.getCustomerId());
                    dto.setAmount(t.getAmount());
                    dto.setDate(t.getDate());
                    dto.setRewardPoints(RewardsCalculator.calculatePoints(t.getAmount()));
                    return dto;
                })
                .collect(Collectors.toList());

        RewardResponseDTO dto = new RewardResponseDTO();
        dto.setCustomerId(customerId);
        dto.setCustomerName(customer.getName());
        dto.setEmailAddress(customer.getEmailAddress());
        dto.setStartDate(start.toString());
        dto.setEndDate(end.toString());
        dto.setRewardPoints(totalRewardPoints);
        dto.setTransactions(transactionDTOs);
        return dto;
    }

    public RewardSummaryResponseDTO getRewardSummary(LocalDate start, LocalDate end) {
        List<Transaction> transactions = transactionRepository.findAll();
        Map<Long, List<Transaction>> transactionsByCustomer = transactions.stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        List<CustomerRewardsSummaryDTO> customerSummaries = transactionsByCustomer.entrySet().stream()
                .map(entry -> {
                    Long customerId = entry.getKey();
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new CustomerNotFoundException(customerId));

                    List<Transaction> customerTransactions = entry.getValue();
                    double totalRewardPoints = customerTransactions.stream()
                            .mapToDouble(tx -> RewardsCalculator.calculatePoints(tx.getAmount()))
                            .sum();

                    List<MonthlyRewardDTO> monthlyRewards = customerTransactions.stream()
                            .collect(Collectors.groupingBy(tx -> YearMonth.from(tx.getDate())))
                            .entrySet().stream()
                            .map(monthEntry -> {
                                MonthlyRewardDTO monthDto = new MonthlyRewardDTO();
                                monthDto.setMonth(monthEntry.getKey().toString());
                                monthDto.setRewardPoints(monthEntry.getValue().stream()
                                        .mapToDouble(tx -> RewardsCalculator.calculatePoints(tx.getAmount()))
                                        .sum());
                                return monthDto;
                            })
                            .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                            .collect(Collectors.toList());

                    List<TransactionResponseDTO> transactionDTOs = customerTransactions.stream()
                            .map(tx -> {
                                TransactionResponseDTO txDto = new TransactionResponseDTO();
                                txDto.setId(tx.getId());
                                txDto.setCustomerId(tx.getCustomerId());
                                txDto.setAmount(tx.getAmount());
                                txDto.setDate(tx.getDate());
                                txDto.setRewardPoints(RewardsCalculator.calculatePoints(tx.getAmount()));
                                return txDto;
                            })
                            .collect(Collectors.toList());

                    CustomerRewardsSummaryDTO summary = new CustomerRewardsSummaryDTO();
                    summary.setCustomerId(customerId);
                    summary.setCustomerName(customer.getName());
                    summary.setEmailAddress(customer.getEmailAddress());
                    summary.setTotalRewardPoints(totalRewardPoints);
                    summary.setMonthlyRewards(monthlyRewards);
                    summary.setTransactions(transactionDTOs);
                    return summary;
                })
                .collect(Collectors.toList());

        double overallTotal = customerSummaries.stream()
                .mapToDouble(CustomerRewardsSummaryDTO::getTotalRewardPoints)
                .sum();

        RewardSummaryResponseDTO response = new RewardSummaryResponseDTO();
        response.setStartDate(start.toString());
        response.setEndDate(end.toString());
        response.setTotalRewardPoints(overallTotal);
        response.setCustomerList(customerSummaries);
        return response;
    }
}

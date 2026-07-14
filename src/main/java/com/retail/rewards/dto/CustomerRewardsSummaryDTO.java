package com.retail.rewards.dto;

import java.util.List;

public class CustomerRewardsSummaryDTO {
    private Long customerId;
    private String customerName;
    private String emailAddress;
    private double totalRewardPoints;
    private List<MonthlyRewardDTO> monthlyRewards;
    private List<TransactionResponseDTO> transactions;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public double getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(double totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    public List<MonthlyRewardDTO> getMonthlyRewards() {
        return monthlyRewards;
    }

    public void setMonthlyRewards(List<MonthlyRewardDTO> monthlyRewards) {
        this.monthlyRewards = monthlyRewards;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }
}

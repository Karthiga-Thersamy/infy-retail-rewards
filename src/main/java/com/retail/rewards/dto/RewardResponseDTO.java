package com.retail.rewards.dto;

import java.util.List;

public class RewardResponseDTO {
    private Long customerId;
    private String customerName;
    private String emailAddress;
    private String startDate;
    private String endDate;
    private double rewardPoints;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(double rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }

    // Backwards-compatible accessors
    public double getPoints() {
        return this.rewardPoints;
    }

    public String getMonth() {
        if (startDate == null || endDate == null) return null;
        if (startDate.length() >= 7 && startDate.substring(0,7).equals(endDate.substring(0,7))) {
            return startDate.substring(0,7);
        }
        return startDate + " - " + endDate;
    }
}

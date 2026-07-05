package com.retail.rewards.dto;

import com.retail.rewards.entity.Transaction;
import java.util.List;

public class RewardResponseDTO {
    private Long customerId;
    private String month;
    private double points;
    private List<Transaction> transactions;

    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }

    public double getPoints() {
        return points;
    }
    public void setPoints(double points) {
        this.points = points;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}

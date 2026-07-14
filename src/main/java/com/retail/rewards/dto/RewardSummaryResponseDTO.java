package com.retail.rewards.dto;

import java.util.List;

public class RewardSummaryResponseDTO {
    private String startDate;
    private String endDate;
    private double totalRewardPoints;
    private List<CustomerRewardsSummaryDTO> customerList;

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

    public double getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(double totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    public List<CustomerRewardsSummaryDTO> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<CustomerRewardsSummaryDTO> customerList) {
        this.customerList = customerList;
    }
}

package com.retail.rewards.controller;

import com.retail.rewards.dto.CustomerRewardsSummaryDTO;
import com.retail.rewards.dto.RewardResponseDTO;
import com.retail.rewards.dto.RewardSummaryResponseDTO;
import com.retail.rewards.dto.TransactionResponseDTO;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void testValidCustomerRewards() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Karthiga");
        responseDTO.setEmailAddress("karthiga@test.com");
        responseDTO.setStartDate("2026-04-01");
        responseDTO.setEndDate("2026-04-30");
        responseDTO.setRewardPoints(90.0);
        responseDTO.setTransactions(Collections.singletonList(new TransactionResponseDTO()));

        when(rewardService.getRewards(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("Karthiga"))
                .andExpect(jsonPath("$.rewardPoints").value(90.0))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    void testInvalidCustomerId() throws Exception {
        when(rewardService.getRewards(eq(999L), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new CustomerNotFoundException(999L));

        mockMvc.perform(get("/api/rewards/customers/999")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found with id: 999"));
    }

    @Test
    void testInvalidDateParameter() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-31")
                        .param("endDate", "2026-04-30"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidNumberOfMonthsOnCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .param("numberOfMonths", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("numberOfMonths")));
    }

    @Test
    void testInvalidDateParameterOnSummary() throws Exception {
        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-31")
                        .param("numberOfMonths", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidNumberOfMonthsOnSummary() throws Exception {
        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .param("numberOfMonths", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("numberOfMonths")));
    }

    @Test
    void testDefaultWindowWhenNoDatesProvided() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Karthiga");
        responseDTO.setEmailAddress("karthiga@test.com");
        responseDTO.setStartDate("2026-01-01");
        responseDTO.setEndDate("2026-03-31");
        responseDTO.setRewardPoints(90.0);
        responseDTO.setTransactions(Collections.singletonList(new TransactionResponseDTO()));

        when(rewardService.getRewards(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void testStartDateOnlyUsesThreeMonthWindow() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Karthiga");
        responseDTO.setEmailAddress("karthiga@test.com");
        responseDTO.setStartDate("2026-04-01");
        responseDTO.setEndDate("2026-06-30");
        responseDTO.setRewardPoints(90.0);
        responseDTO.setTransactions(Collections.singletonList(new TransactionResponseDTO()));

        when(rewardService.getRewards(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void testEndDateOnlyUsesThreeMonthWindow() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Karthiga");
        responseDTO.setEmailAddress("karthiga@test.com");
        responseDTO.setStartDate("2026-04-01");
        responseDTO.setEndDate("2026-06-30");
        responseDTO.setRewardPoints(90.0);
        responseDTO.setTransactions(Collections.singletonList(new TransactionResponseDTO()));

        when(rewardService.getRewards(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("endDate", "2026-06-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void testCustomerWithNumberOfMonths() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Karthiga");
        responseDTO.setEmailAddress("karthiga@test.com");
        responseDTO.setStartDate("2026-04-01");
        responseDTO.setEndDate("2026-05-31");
        responseDTO.setRewardPoints(90.0);
        responseDTO.setTransactions(Collections.singletonList(new TransactionResponseDTO()));

        when(rewardService.getRewards(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .param("numberOfMonths", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.startDate").value("2026-04-01"))
                .andExpect(jsonPath("$.endDate").value("2026-05-31"));
    }

    @Test
    void testZeroTransactionMonth() throws Exception {
        when(rewardService.getRewards(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new CustomerNotFoundException(1L));

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSummaryEndpoint() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();
        summaryDTO.setStartDate("2026-04-01");
        summaryDTO.setEndDate("2026-04-30");
        summaryDTO.setTotalRewardPoints(180.0);

        CustomerRewardsSummaryDTO customerSummary = new CustomerRewardsSummaryDTO();
        customerSummary.setCustomerId(1L);
        customerSummary.setCustomerName("Karthiga");
        customerSummary.setTotalRewardPoints(90.0);

        summaryDTO.setCustomerList(Collections.singletonList(customerSummary));

        when(rewardService.getRewardSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-04-01"))
                .andExpect(jsonPath("$.endDate").value("2026-04-30"))
                .andExpect(jsonPath("$.totalRewardPoints").value(180.0))
                .andExpect(jsonPath("$.customerList[0].customerId").value(1))
                .andExpect(jsonPath("$.customerList[0].customerName").value("Karthiga"))
                .andExpect(jsonPath("$.customerList[0].totalRewardPoints").value(90.0));
    }

    @Test
    void testSummaryStartDateOnlyUsesDefaultWindow() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();
        summaryDTO.setStartDate("2026-04-01");
        summaryDTO.setEndDate("2026-06-30");
        summaryDTO.setTotalRewardPoints(180.0);
        summaryDTO.setCustomerList(Collections.singletonList(new CustomerRewardsSummaryDTO()));

        when(rewardService.getRewardSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-04-01"))
                .andExpect(jsonPath("$.endDate").value("2026-06-30"));
    }

    @Test
    void testSummaryWithNumberOfMonths() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();
        summaryDTO.setStartDate("2026-04-01");
        summaryDTO.setEndDate("2026-05-31");
        summaryDTO.setTotalRewardPoints(180.0);
        summaryDTO.setCustomerList(Collections.singletonList(new CustomerRewardsSummaryDTO()));

        when(rewardService.getRewardSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .param("numberOfMonths", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-04-01"))
                .andExpect(jsonPath("$.endDate").value("2026-05-31"));
    }

    @Test
    void testSummaryNoDatesUsesDefaultWindow() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();
        summaryDTO.setStartDate("2026-01-01");
        summaryDTO.setEndDate("2026-03-31");
        summaryDTO.setTotalRewardPoints(180.0);
        summaryDTO.setCustomerList(Collections.singletonList(new CustomerRewardsSummaryDTO()));

        when(rewardService.getRewardSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

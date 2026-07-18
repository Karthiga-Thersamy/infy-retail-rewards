package com.retail.rewards.controller;

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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardController.class)
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    // ==========================================
    // CUSTOMER REWARDS ENDPOINT TESTS
    // ==========================================

    @Test
    void testValidCustomerRewards() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setCustomerName("Karthiga");
        responseDTO.setRewardPoints(90.0);
        responseDTO.setTransactions(Collections.singletonList(new TransactionResponseDTO()));

        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-04-30");

        when(rewardService.getRewards(eq(1L), eq(expectedStart), eq(expectedEnd)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("Karthiga"));
    }

    @Test
    void testDefaultWindowWhenNoDatesProvided() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);

        LocalDate expectedStart = LocalDate.parse("2026-04-19");
        LocalDate expectedEnd = LocalDate.now();

        when(rewardService.getRewards(eq(1L), eq(expectedStart), eq(expectedEnd)))
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

        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-06-30");

        when(rewardService.getRewards(eq(1L), eq(expectedStart), eq(expectedEnd)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testEndDateOnlyUsesThreeMonthWindow() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);

        LocalDate expectedStart = LocalDate.parse("2026-03-31");
        LocalDate expectedEnd = LocalDate.parse("2026-06-30");

        when(rewardService.getRewards(eq(1L), eq(expectedStart), eq(expectedEnd)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("endDate", "2026-06-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCustomerWithNumberOfMonths() throws Exception {
        RewardResponseDTO responseDTO = new RewardResponseDTO();
        responseDTO.setCustomerId(1L);

        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-05-31");

        when(rewardService.getRewards(eq(1L), eq(expectedStart), eq(expectedEnd)))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-04-01")
                        .param("numberOfMonths", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==========================================
    // SUMMARY ENDPOINT TESTS
    // ==========================================

    @Test
    void testSummaryEndpoint() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();
        summaryDTO.setTotalRewardPoints(180.0);

        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-04-30");

        when(rewardService.getRewardSummary(eq(expectedStart), eq(expectedEnd)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testSummaryStartDateOnlyUsesDefaultWindow() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();

        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-06-30");

        when(rewardService.getRewardSummary(eq(expectedStart), eq(expectedEnd)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testSummaryWithNumberOfMonths() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();

        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-05-31");

        when(rewardService.getRewardSummary(eq(expectedStart), eq(expectedEnd)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .param("startDate", "2026-04-01")
                        .param("numberOfMonths", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testSummaryNoDatesUsesDefaultWindow() throws Exception {
        RewardSummaryResponseDTO summaryDTO = new RewardSummaryResponseDTO();

        LocalDate expectedStart = LocalDate.parse("2026-04-19");
        LocalDate expectedEnd = LocalDate.now();

        when(rewardService.getRewardSummary(eq(expectedStart), eq(expectedEnd)))
                .thenReturn(summaryDTO);

        mockMvc.perform(get("/api/rewards/summary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==========================================
    // EXCEPTION & ERROR HANDLING TESTS
    // ==========================================

    @Test
    void testInvalidCustomerId() throws Exception {
        LocalDate expectedStart = LocalDate.parse("2026-04-01");
        LocalDate expectedEnd = LocalDate.parse("2026-04-30");

        when(rewardService.getRewards(eq(999L), eq(expectedStart), eq(expectedEnd)))
                .thenThrow(new CustomerNotFoundException(999L));

        mockMvc.perform(get("/api/rewards/customers/999")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    void testZeroTransactionMonth() throws Exception {
        LocalDate expectedStart = LocalDate.parse("2026-07-01");
        LocalDate expectedEnd = LocalDate.parse("2026-07-31");

        when(rewardService.getRewards(eq(1L), eq(expectedStart), eq(expectedEnd)))
                .thenThrow(new CustomerNotFoundException(1L));

        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31"))
                .andExpect(status().isNotFound());
    }
}
package com.retail.rewards.controller;

import com.retail.rewards.RewardsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RewardsApplication.class)
@AutoConfigureMockMvc
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidCustomerRewards() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("year", "2026")
                        .param("month", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.points").isNumber())
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    void testInvalidCustomerId() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/999")
                        .param("year", "2026")
                        .param("month", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found with id: 999"));
    }

    @Test
    void testInvalidMonthParameter() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("year", "2026")
                        .param("month", "13")) // invalid month
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingParameters() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testZeroTransactionMonth() throws Exception {
        mockMvc.perform(get("/api/rewards/customers/1")
                        .param("year", "2026")
                        .param("month", "7")) // assume no transactions in July
                .andExpect(status().isNotFound());
    }
}

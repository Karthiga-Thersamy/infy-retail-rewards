package com.retail.rewards.service;

import com.retail.rewards.dto.RewardResponseDTO;
import com.retail.rewards.dto.TransactionResponseDTO;
import com.retail.rewards.dto.RewardSummaryResponseDTO;
import com.retail.rewards.dto.CustomerRewardsSummaryDTO;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.CustomerNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RewardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardService rewardService;

    @Test
    void testGetRewardsAggregatesMultipleTransactions() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test User");
        customer.setEmailAddress("test@example.com");

        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setCustomerId(customerId);
        t1.setAmount(120.0);
        t1.setDate(LocalDate.of(2026, 4, 10));

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setCustomerId(customerId);
        t2.setAmount(75.0);
        t2.setDate(LocalDate.of(2026, 4, 15));

        List<Transaction> transactions = Arrays.asList(t1, t2);

        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 30);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, start, end)).thenReturn(transactions);

        RewardResponseDTO dto = rewardService.getRewards(customerId, start, end);

        assertNotNull(dto);
        assertEquals(customerId, dto.getCustomerId());
        assertEquals("Test User", dto.getCustomerName());

        double expectedPointsT1 = RewardsCalculator.calculatePoints(120.0);
        double expectedPointsT2 = RewardsCalculator.calculatePoints(75.0);
        assertEquals(expectedPointsT1 + expectedPointsT2, dto.getRewardPoints(), 0.001);

        List<TransactionResponseDTO> txDtos = dto.getTransactions();
        assertEquals(2, txDtos.size());
        
        assertEquals(120.0, txDtos.get(0).getAmount());
        assertEquals(expectedPointsT1, txDtos.get(0).getRewardPoints(), 0.001);

        assertEquals(75.0, txDtos.get(1).getAmount());
        assertEquals(expectedPointsT2, txDtos.get(1).getRewardPoints(), 0.001);
    }

    @Test
    void testGetRewardsThrowsCustomerNotFoundWhenCustomerMissing() {
        Long customerId = 999L;
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 30);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> rewardService.getRewards(customerId, start, end));
    }

    @Test
    void testGetRewardsReturnsZeroRewardForCustomerWithNoTransactions() {
        Long customerId = 2L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("No Tx User");
        customer.setEmailAddress("notx@example.com");

        LocalDate start = LocalDate.of(2026, 7, 1);
        LocalDate end = LocalDate.of(2026, 7, 31);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, start, end)).thenReturn(Collections.emptyList());

        RewardResponseDTO dto = rewardService.getRewards(customerId, start, end);

        assertEquals(customerId, dto.getCustomerId());
        assertEquals("No Tx User", dto.getCustomerName());
        assertEquals(0.0, dto.getRewardPoints(), 0.001);
        assertEquals(0, dto.getTransactions().size());
    }

    @Test
    void testGetRewardSummaryAggregatesCustomers() {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("User One");
        customer1.setEmailAddress("one@example.com");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("User Two");
        customer2.setEmailAddress("two@example.com");

        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setCustomerId(1L);
        t1.setAmount(120.0);
        t1.setDate(LocalDate.of(2026, 4, 10));

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setCustomerId(2L);
        t2.setAmount(200.0);
        t2.setDate(LocalDate.of(2026, 4, 20));

        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 30);

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer2));

        RewardSummaryResponseDTO summary = rewardService.getRewardSummary(start, end);

        double points1 = RewardsCalculator.calculatePoints(120.0);
        double points2 = RewardsCalculator.calculatePoints(200.0);

        assertNotNull(summary);
        assertEquals(points1 + points2, summary.getTotalRewardPoints(), 0.001);

        List<CustomerRewardsSummaryDTO> customerList = summary.getCustomerList();
        assertEquals(2, customerList.size());

        CustomerRewardsSummaryDTO c1Summary = customerList.stream()
                .filter(c -> c.getCustomerId().equals(1L))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Customer 1 mapping missing in summary output"));
        assertEquals("User One", c1Summary.getCustomerName());
        assertEquals(points1, c1Summary.getTotalRewardPoints(), 0.001);

        CustomerRewardsSummaryDTO c2Summary = customerList.stream()
                .filter(c -> c.getCustomerId().equals(2L))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Customer 2 mapping missing in summary output"));
        assertEquals("User Two", c2Summary.getCustomerName());
        assertEquals(points2, c2Summary.getTotalRewardPoints(), 0.001);
    }
}
package id.ac.ui.cs.advprog.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.productservice.dto.TransactionDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionRequestDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionUpdateDTO;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;
    private TransactionDTO transactionDTO;
    private TransactionRequestDTO requestDTO;
    private TransactionUpdateDTO updateDTO;
    private List<TransactionDTO> transactionList;
    private Map<String, Object> transactionDetails;
    private String transactionId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .build();

        transactionId = "test-transaction-id";

        transactionDTO = new TransactionDTO();
        transactionDTO.setId(transactionId);
        transactionDTO.setCustomerId("customer-123");
        transactionDTO.setPaymentMethod("CASH");
        transactionDTO.setStatus(TransactionStatus.PENDING);
        transactionDTO.setCreatedAt(new Date());
        transactionDTO.setUpdatedAt(new Date());
        transactionDTO.setTotalAmount(300.0);

        List<TransactionDTO.TransactionItemDTO> items = new ArrayList<>();
        TransactionDTO.TransactionItemDTO item = new TransactionDTO.TransactionItemDTO();
        item.setProductId("product-123");
        item.setProductName("Test Product");
        item.setPrice(100.0);
        item.setQuantity(3);
        item.setSubtotal(300.0);
        items.add(item);
        transactionDTO.setItems(items);

        requestDTO = new TransactionRequestDTO();
        requestDTO.setCustomerId("customer-123");
        requestDTO.setPaymentMethod("CASH");
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-123", 3);
        requestDTO.setProductQuantities(productQuantities);

        updateDTO = new TransactionUpdateDTO();
        updateDTO.setCustomerId("updated-customer");
        updateDTO.setPaymentMethod("CARD");
        Map<String, Integer> updatedQuantities = new HashMap<>();
        updatedQuantities.put("product-123", 5);

        transactionList = Collections.singletonList(transactionDTO);

        transactionDetails = new HashMap<>();
        transactionDetails.put("transaction", transactionDTO);
        List<Map<String, Object>> stockStatus = new ArrayList<>();
        Map<String, Object> productStatus = new HashMap<>();
        productStatus.put("productId", "product-123");
        productStatus.put("productName", "Test Product");
        productStatus.put("quantityInTransaction", 3);
        productStatus.put("currentStock", 10);
        stockStatus.add(productStatus);
        transactionDetails.put("stockStatus", stockStatus);
    }

    @Test
    void createTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequestDTO.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.customerId").value("customer-123"))
                .andExpect(jsonPath("$.paymentMethod").value("CASH"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(transactionService).createTransaction(any(TransactionRequestDTO.class));
    }

    @Test
    void getTransactionById_Success() throws Exception {
        when(transactionService.getTransactionById(transactionId)).thenReturn(transactionDTO);

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.customerId").value("customer-123"));

        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void getAllTransactions_NoParams_Success() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getAllTransactions();
    }

    @Test
    void getAllTransactions_WithCustomerId_Success() throws Exception {
        when(transactionService.getTransactionsByCustomerId("customer-123")).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions")
                        .param("customerId", "customer-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getTransactionsByCustomerId("customer-123");
    }

    @Test
    void getAllTransactions_WithStatus_Success() throws Exception {
        when(transactionService.getTransactionsByStatus(TransactionStatus.PENDING)).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getTransactionsByStatus(TransactionStatus.PENDING);
    }

    @Test
    void getAllTransactions_WithPaymentMethod_Success() throws Exception {
        when(transactionService.getTransactionsByPaymentMethod("CASH")).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions")
                        .param("paymentMethod", "CASH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getTransactionsByPaymentMethod("CASH");
    }

    @Test
    void getAllTransactions_WithDateRange_Success() throws Exception {
        when(transactionService.getTransactionsByDateRange(any(Date.class), any(Date.class))).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getTransactionsByDateRange(any(Date.class), any(Date.class));
    }

    @Test
    void updateTransaction_Success() throws Exception {
        when(transactionService.updateTransaction(eq(transactionId), any(TransactionUpdateDTO.class))).thenReturn(transactionDTO);

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId));

        verify(transactionService).updateTransaction(eq(transactionId), any(TransactionUpdateDTO.class));
    }

    @Test
    void completeTransaction_Success() throws Exception {
        when(transactionService.completeTransaction(transactionId)).thenReturn(transactionDTO);

        mockMvc.perform(patch("/api/transactions/{id}/complete", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId));

        verify(transactionService).completeTransaction(transactionId);
    }

    @Test
    void cancelTransaction_Success() throws Exception {
        when(transactionService.cancelTransaction(transactionId)).thenReturn(transactionDTO);

        mockMvc.perform(patch("/api/transactions/{id}/cancel", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId));

        verify(transactionService).cancelTransaction(transactionId);
    }

    @Test
    void deleteTransaction_Success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    void searchTransactions_Success() throws Exception {
        when(transactionService.searchTransactions("test")).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions/search")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).searchTransactions("test");
    }

    @Test
    void getOngoingTransactions_Success() throws Exception {
        when(transactionService.getOngoingTransactions()).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions/ongoing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getOngoingTransactions();
    }

    @Test
    void getPendingTransactions_Success() throws Exception {
        when(transactionService.getTransactionsByStatus(TransactionStatus.PENDING)).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getTransactionsByStatus(TransactionStatus.PENDING);
    }

    @Test
    void getInProgressTransactions_Success() throws Exception {
        when(transactionService.getTransactionsByStatus(TransactionStatus.IN_PROGRESS)).thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions/in-progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).getTransactionsByStatus(TransactionStatus.IN_PROGRESS);
    }

    @Test
    void filterTransactions_Success() throws Exception {
        when(transactionService.filterTransactions(
                anyString(), anyList(), anyList(), any(Date.class), any(Date.class), anyString(), anyString()))
                .thenReturn(transactionList);

        mockMvc.perform(get("/api/transactions/filter")
                        .param("customerId", "customer-123")
                        .param("statuses", "PENDING", "IN_PROGRESS")
                        .param("paymentMethods", "CASH")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId));

        verify(transactionService).filterTransactions(
                anyString(), anyList(), anyList(), any(Date.class), any(Date.class), anyString(), anyString());
    }

    @Test
    void confirmTransaction_Success() throws Exception {
        when(transactionService.confirmTransaction(transactionId)).thenReturn(transactionDTO);

        mockMvc.perform(patch("/api/transactions/{id}/confirm", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId));

        verify(transactionService).confirmTransaction(transactionId);
    }

    @Test
    void getTransactionDetails_Success() throws Exception {
        when(transactionService.getTransactionDetails(transactionId))
                .thenReturn(CompletableFuture.completedFuture(transactionDetails));

        mockMvc.perform(asyncDispatch(mockMvc.perform(get("/api/transactions/{id}/details", transactionId))
                        .andReturn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.id").value(transactionId))
                .andExpect(jsonPath("$.stockStatus[0].productId").value("product-123"));

        verify(transactionService).getTransactionDetails(transactionId);
    }

    @Test
    void completeMultipleTransactions_Success() throws Exception {
        List<String> transactionIds = Collections.singletonList(transactionId);
        when(transactionService.batchCompleteTransactions(anyList())).thenReturn(1);

        mockMvc.perform(post("/api/transactions/batch/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionIds)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(transactionService).batchCompleteTransactions(anyList());
    }

    @Test
    void cancelMultipleTransactions_Success() throws Exception {
        List<String> transactionIds = Collections.singletonList(transactionId);
        when(transactionService.batchCancelTransactions(anyList())).thenReturn(1);

        mockMvc.perform(post("/api/transactions/batch/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionIds)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(transactionService).batchCancelTransactions(anyList());
    }

    @Test
    void completeMultipleTransactionsAsync_Success() throws Exception {
        List<String> transactionIds = Collections.singletonList(transactionId);
        when(transactionService.batchCompleteTransactionsAsync(anyList()))
                .thenReturn(CompletableFuture.completedFuture(1));

        MvcResult mvcResult = mockMvc.perform(post("/api/transactions/batch/complete/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionIds)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void cancelMultipleTransactionsAsync_Success() throws Exception {
        List<String> transactionIds = Collections.singletonList(transactionId);
        when(transactionService.batchCancelTransactionsAsync(anyList()))
                .thenReturn(CompletableFuture.completedFuture(1));

        MvcResult mvcResult = mockMvc.perform(post("/api/transactions/batch/cancel/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionIds)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
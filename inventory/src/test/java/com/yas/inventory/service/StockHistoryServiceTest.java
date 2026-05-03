package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockHistoryServiceTest {

    @Mock
    private StockHistoryRepository stockHistoryRepository;
    @Mock
    private ProductService productService;

    @InjectMocks
    private StockHistoryService stockHistoryService;

    @Test
    void createStockHistories_ShouldSaveAll() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setProductId(10L);
        stock.setWarehouse(new Warehouse());
        
        StockQuantityVm quantityVm = new StockQuantityVm(1L, 5L, "note");
        
        stockHistoryService.createStockHistories(List.of(stock), List.of(quantityVm));
        
        verify(stockHistoryRepository).saveAll(anyList());
    }

    @Test
    void getStockHistories_ShouldReturnList() {
        StockHistory history = StockHistory.builder()
            .productId(10L)
            .adjustedQuantity(5L)
            .note("note")
            .build();
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(10L, 1L))
            .thenReturn(List.of(history));
        
        ProductInfoVm productInfo = new ProductInfoVm(10L, "P", "S", true);
        when(productService.getProduct(10L)).thenReturn(productInfo);

        StockHistoryListVm result = stockHistoryService.getStockHistories(10L, 1L);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).productName()).isEqualTo("P");
    }
}

package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductVectorSyncServiceTest {

    @Mock
    private ProductVectorRepository productVectorRepository;

    @InjectMocks
    private ProductVectorSyncService productVectorSyncService;

    @Test
    void testCreateProductVector_whenProductIsPublished_shouldAdd() {
        var product = Product.builder().id(1L).isPublished(true).build();

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository).add(1L);
    }

    @Test
    void testCreateProductVector_whenProductIsNotPublished_shouldNotAdd() {
        var product = Product.builder().id(1L).isPublished(false).build();

        productVectorSyncService.createProductVector(product);

        verifyNoInteractions(productVectorRepository);
    }

    @Test
    void testUpdateProductVector_whenProductIsPublished_shouldUpdate() {
        var product = Product.builder().id(1L).isPublished(true).build();

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).update(1L);
    }

    @Test
    void testUpdateProductVector_whenProductIsNotPublished_shouldDelete() {
        var product = Product.builder().id(1L).isPublished(false).build();

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).delete(1L);
    }

    @Test
    void testDeleteProductVector_shouldDelete() {
        productVectorSyncService.deleteProductVector(1L);

        verify(productVectorRepository).delete(1L);
    }
}

package com.yas.recommendation.kafka.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.READ;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSyncServiceTest {

    @Mock
    private ProductVectorSyncService productVectorSyncService;

    @InjectMocks
    private ProductSyncService productSyncService;

    @Test
    void testSync_whenCreateEvent_shouldCreateProductVector() {
        var key = ProductMsgKey.builder().id(1L).build();
        var message = ProductCdcMessage.builder()
            .op(CREATE)
            .after(Product.builder().id(1L).isPublished(true).build())
            .build();

        productSyncService.sync(key, message);

        verify(productVectorSyncService).createProductVector(message.getAfter());
    }

    @Test
    void testSync_whenReadEvent_shouldCreateProductVector() {
        var key = ProductMsgKey.builder().id(1L).build();
        var message = ProductCdcMessage.builder()
            .op(READ)
            .after(Product.builder().id(1L).isPublished(true).build())
            .build();

        productSyncService.sync(key, message);

        verify(productVectorSyncService).createProductVector(message.getAfter());
    }

    @Test
    void testSync_whenUpdateEvent_shouldUpdateProductVector() {
        var key = ProductMsgKey.builder().id(1L).build();
        var message = ProductCdcMessage.builder()
            .op(UPDATE)
            .after(Product.builder().id(1L).isPublished(true).build())
            .build();

        productSyncService.sync(key, message);

        verify(productVectorSyncService).updateProductVector(message.getAfter());
    }

    @Test
    void testSync_whenDeleteEvent_shouldDeleteProductVector() {
        var key = ProductMsgKey.builder().id(1L).build();
        var message = ProductCdcMessage.builder()
            .op(DELETE)
            .after(Product.builder().id(1L).isPublished(true).build())
            .build();

        productSyncService.sync(key, message);

        verify(productVectorSyncService).deleteProductVector(key.getId());
    }

    @Test
    void testSync_whenNullMessage_shouldDeleteProductVector() {
        var key = ProductMsgKey.builder().id(1L).build();

        productSyncService.sync(key, null);

        verify(productVectorSyncService).deleteProductVector(key.getId());
    }

    @Test
    void testSync_whenNoAfterField_shouldNotInteract() {
        var key = ProductMsgKey.builder().id(1L).build();
        var message = ProductCdcMessage.builder()
            .op(CREATE)
            .build();

        productSyncService.sync(key, message);

        verifyNoInteractions(productVectorSyncService);
    }
}

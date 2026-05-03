package com.yas.product.viewmodel.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductVariationPutVmTest {

    @Test
    void testProductVariationPutVm() {
        ProductVariationPutVm vm = new ProductVariationPutVm(1L, "Name", "slug", "SKU", "GTIN", 100.0, 100L,
            java.util.List.of(1L, 2L), java.util.Map.of(1L, "Red"));

        assertEquals(1L, vm.id());
        assertEquals("Name", vm.name());
        assertEquals("slug", vm.slug());
        assertEquals("SKU", vm.sku());
        assertEquals("GTIN", vm.gtin());
        assertEquals(100.0, vm.price());
        assertEquals(100L, vm.thumbnailMediaId());
        assertEquals(2, vm.productImageIds().size());
        assertEquals("Red", vm.optionValuesByOptionId().get(1L));
    }
}

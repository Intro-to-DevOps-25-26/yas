package com.yas.product.viewmodel.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionValueDisplayTest {

    @Test
    void testProductOptionValueDisplay() {
        ProductOptionValueDisplay vm = new ProductOptionValueDisplay(1L, "dropdown", 1, "Red");

        assertEquals(1L, vm.productOptionId());
        assertEquals("dropdown", vm.displayType());
        assertEquals(1, vm.displayOrder());
        assertEquals("Red", vm.value());
    }
}

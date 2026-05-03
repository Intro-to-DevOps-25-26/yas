package com.yas.product.viewmodel.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductDetailGetVmTest {

    @Test
    void testProductDetailGetVm() {
        ProductDetailGetVm vm = new ProductDetailGetVm(
            1L, "Name", "Brand", java.util.List.of("Category"),
            java.util.List.of(), "Short Desc", "Desc", "Spec",
            true, true, true, true, 100.0, "thumbnail", java.util.List.of("img1"));

        assertEquals(1L, vm.id());
        assertEquals("Name", vm.name());
        assertEquals("Brand", vm.brandName());
        assertEquals(1, vm.productCategories().size());
        assertEquals(true, vm.isAllowedToOrder());
        assertEquals(true, vm.isPublished());
        assertEquals(true, vm.isFeatured());
        assertEquals(true, vm.hasOptions());
        assertEquals(100.0, vm.price());
        assertEquals("thumbnail", vm.thumbnailMediaUrl());
        assertEquals(1, vm.productImageMediaUrls().size());
    }
}

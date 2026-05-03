package com.yas.product.viewmodel.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductGetDetailVmTest {

    @Test
    void testProductGetDetailVm() {
        ProductGetDetailVm vm = new ProductGetDetailVm(1L, "Name", "slug");

        assertEquals(1L, vm.id());
        assertEquals("Name", vm.name());
        assertEquals("slug", vm.slug());
    }

    @Test
    void testProductGetDetailVm_FromModel() {
        com.yas.product.model.Product product = new com.yas.product.model.Product();
        product.setId(1L);
        product.setName("Test");
        product.setSlug("test-slug");

        ProductGetDetailVm vm = ProductGetDetailVm.fromModel(product);

        assertEquals(1L, vm.id());
        assertEquals("Test", vm.name());
        assertEquals("test-slug", vm.slug());
    }
}

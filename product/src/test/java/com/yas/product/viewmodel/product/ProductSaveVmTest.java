package com.yas.product.viewmodel.product;

import com.yas.product.model.enumeration.DimensionUnit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductPostVmTest {

    @Test
    void testProductPostVm() {
        ProductPostVm vm = new ProductPostVm(
            "Name", "slug", 1L, List.of(1L),
            "Short Desc", "Desc", "Spec",
            "SKU", "GTIN", 1.0, DimensionUnit.CM,
            10.0, 5.0, 3.0,
            100.0, true, true, true, true, true,
            "metaTitle", "metaKeyword", "metaDescription",
            100L, List.of(1L, 2L),
            List.of(), List.of(), List.of(),
            List.of(), 1L);

        assertEquals("Name", vm.name());
        assertEquals("slug", vm.slug());
        assertEquals(1L, vm.brandId());
        assertEquals(1, vm.categoryIds().size());
        assertEquals("SKU", vm.sku());
        assertEquals("GTIN", vm.gtin());
        assertEquals(100.0, vm.price());
        assertEquals(100L, vm.thumbnailMediaId());
        assertEquals(10.0, vm.length());
        assertEquals(5.0, vm.width());
        assertEquals(3.0, vm.height());
        assertEquals(DimensionUnit.CM, vm.dimensionUnit());
        assertEquals(1L, vm.taxClassId());
        assertEquals(2, vm.productImageIds().size());
    }
}

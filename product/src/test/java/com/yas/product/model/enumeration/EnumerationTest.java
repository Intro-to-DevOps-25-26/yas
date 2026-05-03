package com.yas.product.model.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumerationTest {

    @Test
    void testDimensionUnit_Values() {
        DimensionUnit[] values = DimensionUnit.values();
        assertTrue(values.length > 0);
    }

    @Test
    void testDimensionUnit_ValueOf() {
        DimensionUnit cm = DimensionUnit.valueOf("CM");
        assertNotNull(cm);
    }

    @Test
    void testFilterExistInWhSelection_Values() {
        FilterExistInWhSelection[] values = FilterExistInWhSelection.values();
        assertTrue(values.length > 0);
    }

    @Test
    void testFilterExistInWhSelection_ValueOf() {
        FilterExistInWhSelection all = FilterExistInWhSelection.valueOf("ALL");
        assertNotNull(all);
    }
}

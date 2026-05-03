package com.yas.product.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductConverterTest {

    @Test
    void testToSlug_NormalString() {
        String result = ProductConverter.toSlug("Hello World");
        assertEquals("hello-world", result);
    }

    @Test
    void testToSlug_StringWithSpecialChars() {
        String result = ProductConverter.toSlug("Hello @#$ World!");
        assertEquals("hello-world-", result);
    }

    @Test
    void testToSlug_StringWithMultipleDashes() {
        String result = ProductConverter.toSlug("Hello---World");
        assertEquals("hello-world", result);
    }

    @Test
    void testToSlug_StringStartsWithDash() {
        String result = ProductConverter.toSlug("-hello-world");
        assertEquals("hello-world", result);
    }

    @Test
    void testToSlug_StringWithLeadingTrailingSpaces() {
        String result = ProductConverter.toSlug("  Hello World  ");
        assertEquals("hello-world", result);
    }

    @Test
    void testToSlug_AlreadyLowercase() {
        String result = ProductConverter.toSlug("already-lowercase");
        assertEquals("already-lowercase", result);
    }

    @Test
    void testToSlug_MixedCase() {
        String result = ProductConverter.toSlug("MiXeD_CaSe");
        assertEquals("mixed-case", result);
    }

    @Test
    void testToSlug_NumbersPreserved() {
        String result = ProductConverter.toSlug("Product 123 Name");
        assertEquals("product-123-name", result);
    }

    @Test
    void testToSlug_EmptyString() {
        String result = ProductConverter.toSlug("");
        assertEquals("", result);
    }

    @Test
    void testToSlug_OnlySpecialChars() {
        String result = ProductConverter.toSlug("@#$%^&*");
        assertEquals("", result);
    }

    @Test
    void testToSlug_SingleWord() {
        String result = ProductConverter.toSlug("Hello");
        assertEquals("hello", result);
    }

    @Test
    void testToSlug_WithUnderscores() {
        String result = ProductConverter.toSlug("hello_world_test");
        assertEquals("hello-world-test", result);
    }
}

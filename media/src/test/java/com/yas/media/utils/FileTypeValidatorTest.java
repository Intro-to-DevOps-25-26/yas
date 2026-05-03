package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator fileTypeValidator;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder builder;

    // A valid 1x1 transparent GIF byte array
    private static final byte[] VALID_GIF_BYTES = new byte[]{
        0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
        (byte)0x80, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x21,
        (byte)0xF9, 0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x2C, 0x00, 0x00,
        0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x01, 0x44,
        0x00, 0x3B
    };

    @BeforeEach
    void setUp() {
        fileTypeValidator = new FileTypeValidator();
        
        ValidFileType validFileType = mock(ValidFileType.class);
        when(validFileType.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png", "image/gif"});
        when(validFileType.message()).thenReturn("Invalid file type");
        
        fileTypeValidator.initialize(validFileType);

        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintViolationBuilder.class);
        
        when(context.buildConstraintViolationWithTemplate("Invalid file type")).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValid_WhenNullFile_ShouldReturnFalse() {
        assertFalse(fileTypeValidator.isValid(null, context));
    }

    @Test
    void isValid_WhenNullContentType_ShouldReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", null, "content".getBytes());
        assertFalse(fileTypeValidator.isValid(file, context));
    }

    @Test
    void isValid_WhenInvalidContentType_ShouldReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        assertFalse(fileTypeValidator.isValid(file, context));
    }

    @Test
    void isValid_WhenValidContentTypeButNotImage_ShouldReturnFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.gif", "image/gif", "invalid image content".getBytes());
        assertFalse(fileTypeValidator.isValid(file, context));
    }

    @Test
    void isValid_WhenValidContentTypeAndImage_ShouldReturnTrue() {
        MockMultipartFile file = new MockMultipartFile("file", "test.gif", "image/gif", VALID_GIF_BYTES);
        assertTrue(fileTypeValidator.isValid(file, context));
    }
}

package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.ProductApplication;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest(classes = ProductApplication.class)
class CategoryServiceTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @MockitoBean
    private MediaService mediaService;
    @Autowired
    private CategoryService categoryService;

    private Category category;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setName("name");
        category.setSlug("slug");
        category.setDescription("description");
        category.setMetaKeyword("metaKeyword");
        category.setMetaDescription("metaDescription");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);
        categoryRepository.save(category);

        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
    }

    @AfterEach
    void tearDown() {
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getCategoryById_Success() {
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        CategoryGetDetailVm categoryGetDetailVm = categoryService.getCategoryById(category.getId());
        assertNotNull(categoryGetDetailVm);
        assertEquals("name", categoryGetDetailVm.name());
    }

    @Test
    void getCategories_Success() {
        when(mediaService.getMedia(any())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(1, categoryService.getCategories("name").size());
        CategoryGetVm categoryGetVm = categoryService.getCategories("name").getFirst();
        assertEquals("name", categoryGetVm.name());
    }

    @Test
    void getCategoriesPageable_Success() {
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(1, categoryService.getPageableCategories(0, 1).categoryContent().size());
        CategoryGetVm categoryGetVm = categoryService.getCategories("a").getFirst();
        assertEquals("name", categoryGetVm.name());
    }

    @Test
    void getCategoryById_NotFound_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(999L));
    }

    @Test
    void getCategoryById_WithNullImageId() {
        Category catWithoutImage = new Category();
        catWithoutImage.setName("No Image");
        catWithoutImage.setSlug("no-image");
        catWithoutImage.setDisplayOrder((short) 0);
        catWithoutImage.setImageId(null);
        categoryRepository.save(catWithoutImage);

        CategoryGetDetailVm result = categoryService.getCategoryById(catWithoutImage.getId());

        assertNotNull(result);
        assertEquals("No Image", result.name());
    }

    @Test
    void getCategoryById_WithParentCategory() {
        Category parent = new Category();
        parent.setName("Parent");
        parent.setSlug("parent");
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Child");
        child.setSlug("child");
        child.setDisplayOrder((short) 0);
        child.setParent(parent);
        categoryRepository.save(child);

        CategoryGetDetailVm result = categoryService.getCategoryById(child.getId());

        assertNotNull(result);
        assertEquals(parent.getId(), result.parentId());
    }

    @Test
    void createCategory_Success() {
        CategoryPostVm postVm = new CategoryPostVm("New Category", "new-cat", "Desc", null,
            "meta", "keywords", (short) 1, true, null);

        Category result = categoryService.create(postVm);

        assertNotNull(result);
        assertEquals("New Category", result.getName());
    }

    @Test
    void createCategory_WithParent_Success() {
        Category parent = new Category();
        parent.setName("Parent");
        parent.setSlug("parent");
        categoryRepository.save(parent);

        CategoryPostVm postVm = new CategoryPostVm("Child Category", "child-cat", "Desc", parent.getId(),
            "meta", "keywords", (short) 1, true, null);

        Category result = categoryService.create(postVm);

        assertNotNull(result);
        assertEquals("Child Category", result.getName());
    }

    @Test
    void createCategory_ParentNotFound_ThrowsBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Child Category", "child-cat", "Desc", 999L,
            "meta", "keywords", (short) 1, true, null);

        assertThrows(BadRequestException.class, () -> categoryService.create(postVm));
    }

    @Test
    void createCategory_DuplicateName_ThrowsDuplicatedException() {
        CategoryPostVm postVm = new CategoryPostVm("name", "duplicate-slug", "Desc", null,
            "meta", "keywords", (short) 1, true, null);

        assertThrows(DuplicatedException.class, () -> categoryService.create(postVm));
    }

    @Test
    void updateCategory_Success() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Name", "updated-slug", "Updated Desc", null,
            "new meta", "new keywords", (short) 2, false, null);

        categoryService.update(postVm, category.getId());

        Category updated = categoryRepository.findById(category.getId()).orElseThrow();
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void updateCategory_NotFound_ThrowsNotFoundException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated", "updated", "Desc", null,
            "meta", "keywords", (short) 1, true, null);

        assertThrows(NotFoundException.class, () -> categoryService.update(postVm, 999L));
    }

    @Test
    void updateCategory_ParentNotFound_ThrowsBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated", "updated", "Desc", 999L,
            "meta", "keywords", (short) 1, true, null);

        assertThrows(BadRequestException.class, () -> categoryService.update(postVm, category.getId()));
    }

    @Test
    void updateCategory_ParentIsItself_ThrowsBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated", "updated", "Desc", category.getId(),
            "meta", "keywords", (short) 1, true, null);

        assertThrows(BadRequestException.class, () -> categoryService.update(postVm, category.getId()));
    }

    @Test
    void updateCategory_SetParentToNull() {
        Category parent = new Category();
        parent.setName("Parent");
        parent.setSlug("parent");
        categoryRepository.save(parent);

        Category child = new Category();
        child.setName("Child");
        child.setSlug("child");
        child.setParent(parent);
        categoryRepository.save(child);

        CategoryPostVm postVm = new CategoryPostVm("Child Updated", "child-updated", "Desc", null,
            "meta", "keywords", (short) 1, true, null);

        categoryService.update(postVm, child.getId());

        Category updatedChild = categoryRepository.findById(child.getId()).orElseThrow();
        assertEquals(null, updatedChild.getParent());
    }

    @Test
    void getCategories_EmptyResults() {
        List<CategoryGetVm> result = categoryService.getCategories("nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPageableCategories_ReturnsPaginatedResults() {
        CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

        assertNotNull(result);
        assertEquals(0, result.pageNo());
    }

    @Test
    void getCategoryByIds_Success() {
        List<Long> ids = List.of(category.getId());

        List<CategoryGetVm> result = categoryService.getCategoryByIds(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTopNthCategories_Success() {
        List<String> result = categoryService.getTopNthCategories(5);

        assertNotNull(result);
    }
}
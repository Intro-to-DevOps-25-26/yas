package com.yas.product.controller;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.service.BrandService;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BrandController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandRepository brandRepository;

    @MockitoBean
    private BrandService brandService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListBrands() throws Exception {

        when(brandRepository.findByNameContainingIgnoreCase(any())).thenReturn(Arrays.asList(
                createBrand(1L, "Brand 1"),
                createBrand(2L, "Brand 2")
        ));

        mockMvc.perform(get("/backoffice/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Brand 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Brand 2"));
    }

    @Test
    void testGetBrand() throws Exception {
        Brand brand = createBrand(1L, "Brand 1");

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        mockMvc.perform(get("/backoffice/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Brand 1"));
    }

    @Test
    void testGetBrandNotFound() throws Exception {
        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/backoffice/brands/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBrand() throws Exception {
        BrandPostVm brandPostVm = new BrandPostVm("New Brand", "newB", true);
        Brand brand = createBrand(1L, "New Brand");

        when(brandService.create(any(BrandPostVm.class))).thenReturn(brand);

        mockMvc.perform(post("/backoffice/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandPostVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Brand"));
    }

    @Test
    void testUpdateBrand() throws Exception {
        BrandPostVm brandPostVm = new BrandPostVm("Updated Brand", "update-b", true);

        mockMvc.perform(put("/backoffice/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandPostVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBrand() throws Exception {
        Brand brand = createBrand(1L, "Brand 1");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        mockMvc.perform(delete("/backoffice/brands/1"))
                .andExpect(status().isNoContent());
    }

    private Brand createBrand(Long id, String name) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setProducts(Collections.emptyList());
        return brand;
    }

    @Test
    void testGetPageableBrands() throws Exception {
        BrandListGetVm brandList = new BrandListGetVm(
                List.of(new BrandVm(1L, "Brand 1", "brand-1", true), new BrandVm(2L, "Brand 2", "brand-2", true)),
                0, 10, 2, 1, true
        );
        when(brandService.getBrands(0, 10)).thenReturn(brandList);

        mockMvc.perform(get("/backoffice/brands/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandContent").isArray())
                .andExpect(jsonPath("$.brandContent[0].name").value("Brand 1"));
    }

    @Test
    void testGetBrandsByIds() throws Exception {
        List<BrandVm> brandVms = List.of(
                new BrandVm(1L, "Brand 1", "brand-1", true),
                new BrandVm(2L, "Brand 2", "brand-2", true)
        );
        when(brandService.getBrandsByIds(List.of(1L, 2L))).thenReturn(brandVms);

        mockMvc.perform(get("/backoffice/brands/by-ids")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].name").value("Brand 2"));
    }

    @Test
    void testDeleteBrandWhenBrandHasProducts() throws Exception {
        Brand brand = createBrand(1L, "Brand with products");
        brand.setProducts(List.of(new Product()));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        doThrow(new BadRequestException(Constants.ErrorCode.MAKE_SURE_BRAND_DONT_CONTAINS_ANY_PRODUCT))
                .when(brandService).delete(1L);

        mockMvc.perform(delete("/backoffice/brands/1"))
                .andExpect(status().isBadRequest());
    }
}


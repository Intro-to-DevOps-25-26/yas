package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

class TaxClassControllerTest {

    private TaxClassService taxClassService;
    private TaxClassController taxClassController;

    @BeforeEach
    void setUp() {
        taxClassService = mock(TaxClassService.class);
        taxClassController = new TaxClassController(taxClassService);
    }

    @Test
    void getPageableTaxClasses_ShouldReturnOk() {
        TaxClassListGetVm vm = new TaxClassListGetVm(List.of(), 0, 10, 0, 0, true);
        when(taxClassService.getPageableTaxClasses(anyInt(), anyInt())).thenReturn(vm);

        ResponseEntity<TaxClassListGetVm> response = taxClassController.getPageableTaxClasses(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vm);
    }

    @Test
    void listTaxClasses_ShouldReturnOk() {
        List<TaxClassVm> list = List.of(new TaxClassVm(1L, "Name"));
        when(taxClassService.findAllTaxClasses()).thenReturn(list);

        ResponseEntity<List<TaxClassVm>> response = taxClassController.listTaxClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getTaxClass_ShouldReturnOk() {
        TaxClassVm vm = new TaxClassVm(1L, "Name");
        when(taxClassService.findById(1L)).thenReturn(vm);

        ResponseEntity<TaxClassVm> response = taxClassController.getTaxClass(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vm);
    }

    @Test
    void createTaxClass_ShouldReturnCreated() {
        TaxClassPostVm postVm = new TaxClassPostVm("id", "New");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("New");
        when(taxClassService.create(any())).thenReturn(taxClass);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<TaxClassVm> response = taxClassController.createTaxClass(postVm, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().name()).isEqualTo("New");
    }

    @Test
    void updateTaxClass_ShouldReturnNoContent() {
        TaxClassPostVm postVm = new TaxClassPostVm("id", "Update");

        ResponseEntity<Void> response = taxClassController.updateTaxClass(1L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).update(postVm, 1L);
    }

    @Test
    void deleteTaxClass_ShouldReturnNoContent() {
        ResponseEntity<Void> response = taxClassController.deleteTaxClass(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).delete(1L);
    }
}

package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

class TaxRateControllerTest {

    private TaxRateService taxRateService;
    private TaxRateController taxRateController;

    @BeforeEach
    void setUp() {
        taxRateService = mock(TaxRateService.class);
        taxRateController = new TaxRateController(taxRateService);
    }

    @Test
    void getPageableTaxRates_ShouldReturnOk() {
        TaxRateListGetVm vm = new TaxRateListGetVm(List.of(), 0, 10, 0, 0, true);
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(vm);

        ResponseEntity<TaxRateListGetVm> response = taxRateController.getPageableTaxRates(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vm);
    }

    @Test
    void getTaxRate_ShouldReturnOk() {
        TaxRateVm vm = new TaxRateVm(1L, 10.0, "12345", 1L, 1L, 1L);
        when(taxRateService.findById(1L)).thenReturn(vm);

        ResponseEntity<TaxRateVm> response = taxRateController.getTaxRate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vm);
    }

    @Test
    void createTaxRate_ShouldReturnCreated() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).build();
        when(taxRateService.createTaxRate(any())).thenReturn(taxRate);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<TaxRateVm> response = taxRateController.createTaxRate(postVm, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateTaxRate_ShouldReturnNoContent() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", 1L, 1L, 1L);

        ResponseEntity<Void> response = taxRateController.updateTaxRate(1L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).updateTaxRate(postVm, 1L);
    }

    @Test
    void deleteTaxRate_ShouldReturnNoContent() {
        ResponseEntity<Void> response = taxRateController.deleteTaxRate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).delete(1L);
    }

    @Test
    void getTaxPercentByAddress_ShouldReturnOk() {
        when(taxRateService.getTaxPercent(1L, 1L, 1L, "12345")).thenReturn(10.0);

        ResponseEntity<Double> response = taxRateController.getTaxPercentByAddress(1L, 1L, 1L, "12345");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(10.0);
    }

    @Test
    void getBatchTaxPercentsByAddress_ShouldReturnOk() {
        List<TaxRateVm> list = List.of(new TaxRateVm(1L, 10.0, "12345", 1L, 1L, 1L));
        when(taxRateService.getBulkTaxRate(any(), any(), any(), any())).thenReturn(list);

        ResponseEntity<List<TaxRateVm>> response = taxRateController.getBatchTaxPercentsByAddress(List.of(1L), 1L, 1L, "12345");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }
}

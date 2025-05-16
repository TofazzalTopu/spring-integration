package com.spring.integration.controller;

import com.spring.integration.constant.AppConstants;
import com.spring.integration.dto.CurrencyRequest;
import com.spring.integration.model.Currency;
import com.spring.integration.model.CurrencyRate;
import com.spring.integration.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
    @GetMapping
    public List<CurrencyRate> getAllRates() {
        return currencyService.findAll();
    }

    @GetMapping("/{code}")
    public CurrencyRate getRate(@PathVariable String code) {
        return currencyService.findByCurrencyCode(code);
    }

    @GetMapping("/codes")
    @Operation(description = "Fetch Currency list.")
    public ResponseEntity<List<String>> listCurrencies() {
        return ResponseEntity.ok().body(currencyService.getAllCurrencyCodes());
    }

    @PostMapping
    @Operation(description = "Save Currency.")
    public ResponseEntity<Currency> addCurrency(@Valid @RequestBody CurrencyRequest currencyRequest) throws URISyntaxException {
        Currency currency = currencyService.addCurrency(currencyRequest.getCurrencyCode().trim().toUpperCase());
        return ResponseEntity.created(new URI(AppConstants.CURRENCY_API_URL)).body(currency);
    }
}


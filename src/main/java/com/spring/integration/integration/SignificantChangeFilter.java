package com.spring.integration.integration;

import com.spring.integration.model.CurrencyRate;
import com.spring.integration.service.CurrencyService;
import org.springframework.integration.core.GenericSelector;

import java.util.List;

public class SignificantChangeFilter implements GenericSelector<List<CurrencyRate>> {

    private final CurrencyService service;

    public SignificantChangeFilter(CurrencyService service) {
        this.service = service;
    }

    @Override
    public boolean accept(List<CurrencyRate> rates) {
        return service.hasSignificantChange(rates); // True if any rate deviates beyond threshold
    }
}


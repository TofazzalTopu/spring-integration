package com.spring.integration.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.integration.model.CurrencyRate;

import java.time.LocalDateTime;
import java.util.List;

public class CurrencyParser {
    public static List<CurrencyRate> parse(List<CurrencyRate> currencyRates) {
        try {
            ObjectMapper mapper = new ObjectMapper();
//            List<CurrencyRate> rates = mapper.readValue(json, new TypeReference<List<CurrencyRate>>() {});
//            rates.forEach(rate -> rate.setLastUpdated(LocalDateTime.now()));
            return currencyRates;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse currency rates", e);
        }
    }
}

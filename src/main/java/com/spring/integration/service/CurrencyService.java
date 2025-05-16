package com.spring.integration.service;

import com.spring.integration.constant.AppConstants;
import com.spring.integration.model.Currency;
import com.spring.integration.model.CurrencyRate;
import com.spring.integration.repository.CurrencyRateRepository;
import com.spring.integration.repository.CurrencyRepository;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CurrencyService {
    private final Logger logger = LoggerFactory.getLogger(CurrencyService.class.getName());

    @Autowired
    private CurrencyRateRepository currencyRateRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    ExchangeRateFetchService exchangeRateFetchService;
    @Autowired
    private RedisTemplate<String, CurrencyRate> redisTemplate;

    public List<CurrencyRate> findAll() {
        return currencyRateRepository.findAll();
    }

    public CurrencyRate findByCurrencyCode(String code) {
        return currencyRateRepository.findByCurrencyCode(code).orElse(null);
    }

    public void saveRates(List<CurrencyRate> rates) {
        logger.info("Saving rates to DB: {}", rates);
        for (CurrencyRate rate : rates) {
            CurrencyRate existing = currencyRateRepository.findById(rate.getCurrencyCode()).orElse(null);
            if (existing != null) {
                rate.setPreviousRate(existing.getRate());
            }
            currencyRateRepository.save(rate);
        }
    }

    public void cacheRates(List<CurrencyRate> rates) {
        logger.info("Cache rates to Redis: {}", rates);
        for (CurrencyRate rate : rates) {
            redisTemplate.opsForValue().set("RATE::" + rate.getCurrencyCode(), rate);
        }
    }

    public boolean hasSignificantChange(List<CurrencyRate> rates) {
        return rates.stream().anyMatch(rate -> Math.abs(rate.getChangePercent()) > 5);
    }

    public void updateRates(Object apiResponse) {
        // Parse, transform, and save to DB
        List<CurrencyRate> rates = parseApiResponse(apiResponse);
        currencyRateRepository.saveAll(rates);
    }

    private List<CurrencyRate> parseApiResponse(Object apiResponse) {
        // Convert raw response to CurrencyRate entities
        return new ArrayList<>();
    }

    public Currency addCurrency(@NotBlank String code) {
        Optional<Currency> optionalCurrency = currencyRepository.findByCode(code);
        if (optionalCurrency.isPresent()) throw new RuntimeException("Currency already exists with code: " + code);

        Currency currency = new Currency();
        currency.setCode(code.toUpperCase());
        return currencyRepository.save(currency);
    }
    public List<CurrencyRate> fetchCurrencyRates() {
        List<CurrencyRate> currencyRates = new ArrayList<>();
        try {
            List<String> currencies = currencyRepository.findAll().stream().map(Currency::getCode).toList();
            if (currencies.isEmpty()) return currencyRates;
            Map<String, Double> rates = exchangeRateFetchService.fetchExchangeRates("USD");
            if (rates == null || rates.isEmpty()) {
                throw new RuntimeException(AppConstants.EXCHANGE_RATE_NOT_FOUND_FOR_BASE_CURRENCY + "USD");
            }
            currencies.forEach(currency -> {
                Number number = rates.get(currency.toUpperCase());
                if (number == null) {
                    logger.error("No exchange rate found for currency: {}", currency);
                    throw new RuntimeException(AppConstants.EXCHANGE_RATE_NOT_FOUND + currency);
                }
                Double rate = number.doubleValue();
                currencyRates.add(saveCurrencyRate(currency, rate));
            });
            logger.info(AppConstants.CURRENCY_RATE_FETCH_SUCCESSFULLY);
        } catch (Exception e) {
            logger.error(AppConstants.ERROR_FETCHING_CURRENCY_RATE, e.getMessage());
        }
        return currencyRates;
    }


    private CurrencyRate saveCurrencyRate(String currency, double rate) {
        CurrencyRate entity = new CurrencyRate();
        Optional<CurrencyRate> existingRate = currencyRateRepository.findByCurrencyCode(currency);
        if (existingRate.isPresent()) {
            entity = existingRate.get();
            entity.setPreviousRate(entity.getRate());
        }
        entity.setCurrencyCode(currency.toUpperCase());
        entity.setRate(rate);
        return currencyRateRepository.save(entity);
    }


    public List<String> getAllCurrencyCodes() {
        return currencyRateRepository.findAll().stream()
                .map(CurrencyRate::getCurrencyCode)
                .toList();
    }
}

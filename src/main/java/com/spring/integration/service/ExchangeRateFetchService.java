package com.spring.integration.service;


import com.spring.integration.constant.AppConstants;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ExchangeRateFetchService {
    private final Logger logger = LoggerFactory.getLogger(ExchangeRateFetchService.class.getName());

    private final RestTemplate restTemplate;

    @Value("${exchange-rate-api.base-url}")
    private String exchangeRateApiBaseUrl;

    @Value("${exchange-rate-api.app-id}")
    private String exchangeRateAppId;

    public ExchangeRateFetchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> fetchExchangeRates(@NotBlank String baseCurrency) {
        try {
            String apiUrl = String.format("%s/latest.json?app_id=%s&base=%s", exchangeRateApiBaseUrl, exchangeRateAppId, baseCurrency);
            logger.info("Fetching exchange rates from API: {}", apiUrl);
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            boolean isResponseValid = responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.hasBody() && Objects.nonNull(responseEntity.getBody());
            if (isResponseValid) {
                Map<String, Object> body = responseEntity.getBody();
                Map<String, Double> rates = body.get("rates") != null ? (Map<String, Double>) body.get("rates") : new HashMap<>();
                if (rates == null || rates.isEmpty()) {
                    throw new RuntimeException(AppConstants.EXCHANGE_RATE_NOT_FOUND_FOR_BASE_CURRENCY + baseCurrency);
                }
                return rates;
            } else {
                throw new RuntimeException(AppConstants.ERROR_FETCHING_CURRENCY_RATE_FROM_API);
            }
        } catch (RestClientException ex) {
            throw new RuntimeException(AppConstants.ERROR_FETCHING_CURRENCY_RATE_FROM_API, ex);
        }
    }


}

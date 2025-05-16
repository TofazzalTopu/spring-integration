package com.spring.integration.utility;

public class ExternalCurrencyApi {
    public static String fetchRates() {
        // Mocked JSON or XML string from a 3rd-party API
        return "[{\"currencyCode\": \"USD\", \"rate\": 1.10}, {\"currencyCode\": \"EUR\", \"rate\": 0.91}]";
    }
}

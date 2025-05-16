package com.spring.integration.constant;

public class AppConstants {


    private AppConstants() {
    }


    public static final String KAFKA_TOPIC_CURRENCY_RATE = "kafka-topic-currency-rate";
    public static final String KAFKA_GROUP_CURRENCY_RATE = "kafka-group-currency-rate";
    public static final String KAFKA_LISTENER_CURRENCY_RATE_CONTAINER_FACTORY = "KafkaListenerCurrencyRateContainerFactory";
    public static final String KAFKA_LISTENER_CONTAINER_FACTORY_STRING = "strKafkaListener";


    public static final String CURRENCY_API_URL = "/currencies";
    public static final String EXCHANGE_RATE_NOT_FOUND = "No exchange rate found for currency: ";
    public static final String EXCHANGE_RATE_NOT_FOUND_FOR_BASE_CURRENCY = "No exchange rate found for currency: ";

    public static final String ERROR_FETCHING_CURRENCY_RATE = "Error fetching exchange rates: {}";

    public static final String ERROR_FETCHING_CURRENCY_RATE_FROM_API = "Failed to fetch exchange rates from external API";
    public static final String CURRENCY_RATE_FETCH_SUCCESSFULLY = "Exchange rates updated successfully for base currency: {}";

}

package com.spring.integration.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.integration.model.CurrencyRate;
import com.spring.integration.service.CurrencyService;
import com.spring.integration.utility.CurrencyParser;
import com.spring.integration.utility.ExternalCurrencyApi;
import com.spring.integration.utility.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.endpoint.MethodInvokingMessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.GenericMessage;

import java.util.List;

@Configuration
@EnableIntegration
public class CurrencyIntegrationFlow {
    private final Logger logger = LoggerFactory.getLogger(CurrencyIntegrationFlow.class.getName());

    @Bean
    public MethodInvokingMessageSource currencyRateSource(CurrencyService currencyService) {
        MethodInvokingMessageSource source = new MethodInvokingMessageSource();
        source.setObject(currencyService);
        source.setMethodName("fetchCurrencyRates"); // Method must return List<CurrencyRate>
        return source;
    }

    @Bean
    public IntegrationFlow currencyRatePipeline(CurrencyService currencyService,
                                                KafkaTemplate<String, String> kafkaTemplate) {
        return IntegrationFlow
                .from(currencyRateSource(currencyService), c -> c.poller(Pollers.fixedRate(3600000, 10000)))
                .transform(transformToEntities())
                .filter(new SignificantChangeFilter(currencyService))
                .handle(updateDatabase(currencyService))
                .handle(cacheToRedis(currencyService))
                .handle(publishToKafka(kafkaTemplate))
                .handle(sendNotification())
                .get();
    }

    @Bean
    public MessageSource<String> triggerSource() {
        return () -> new GenericMessage<>("trigger");
    }


    @Bean
    public GenericHandler<String> fetchCurrencyRates() {
        return (payload, headers) -> ExternalCurrencyApi.fetchRates(); // Make actual API call
    }

    @Bean
    public GenericTransformer<Object, List<CurrencyRate>> transformToEntities() {
        return raw -> CurrencyParser.parse((List<CurrencyRate>) raw); // Convert JSON/XML into List<CurrencyRate>
    }

    @Bean
    public GenericHandler<List<CurrencyRate>> updateDatabase(CurrencyService service) {
        return (rates, headers) -> {
            service.saveRates(rates);
            return rates;
        };
    }

    @Bean
    public GenericHandler<List<CurrencyRate>> cacheToRedis(CurrencyService service) {
        return (rates, headers) -> {
            service.cacheRates(rates);
            return rates;
        };
    }

    @Bean
    public GenericHandler<List<CurrencyRate>> publishToKafka(KafkaTemplate<String, String> kafkaTemplate) {
        return (rates, headers) -> {
            try {
                logger.info("Publishing rates to Kafka: {}", rates);
                kafkaTemplate.send("currency-rate-topic", new ObjectMapper().writeValueAsString(rates));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return rates;
        };
    }

    @Bean
    public GenericHandler<List<CurrencyRate>> sendNotification() {
        return (rates, headers) -> {
            // Trigger email/SMS/log
            NotificationService.notifyIfNeeded(rates);
            return null;
        };
    }

}

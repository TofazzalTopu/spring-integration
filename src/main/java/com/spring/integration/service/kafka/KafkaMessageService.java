package com.spring.integration.service.kafka;

import com.spring.integration.constant.AppConstants;
import com.spring.integration.model.CurrencyRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageService {

    private static Logger logger = LoggerFactory.getLogger(KafkaMessageService.class);


    @KafkaListener(topics = AppConstants.KAFKA_TOPIC_CURRENCY_RATE, groupId = AppConstants.KAFKA_GROUP_CURRENCY_RATE, containerFactory = AppConstants.KAFKA_LISTENER_CURRENCY_RATE_CONTAINER_FACTORY)
    public void listen(CurrencyRate currencyRate,
                       @Header(name = "kafka_receivedMessageKey", required = false) String key,
                       @Header(name = "kafka_receivedPartitionId", required = false) int partition,
                       @Header(name = "kafka_receivedTopic", required = false) String topic,
                       @Header(name = "kafka_offset", required = false) long offset) {
        logger.info("Received message with correlation ID: {}", currencyRate);
        logger.info("Key: {}, Partition: {}, Topic: {}, Offset: {}", key, partition, topic, offset);
    }

}

package com.spring.integration.config;

import com.spring.integration.constant.AppConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

//@Configuration
public class TopicConfig {

//    @Bean
    public NewTopic orders() {
        return TopicBuilder.name(AppConstants.KAFKA_TOPIC_CURRENCY_RATE)
                .partitions(3)      // Topic will have 3 partitions
                .compact()          // Log compaction enabled
                .build();
    }

}

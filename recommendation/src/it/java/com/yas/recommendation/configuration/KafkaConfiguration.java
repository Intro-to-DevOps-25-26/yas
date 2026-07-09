package com.yas.recommendation.configuration;

import com.yas.recommendation.kafka.config.consumer.ProductCdcKafkaListenerConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(ProductCdcKafkaListenerConfig.class)
public class KafkaConfiguration {
}

package com.yas.search.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.0.9"));

    static {
        kafka.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            ctx,
            "spring.kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
            "spring.kafka.consumer.bootstrap-servers=" + kafka.getBootstrapServers(),
            "spring.kafka.producer.bootstrap-servers=" + kafka.getBootstrapServers(),
            "spring.kafka.consumer.auto-offset-reset=earliest"
        );
        ctx.getBeanFactory().registerSingleton("kafkaContainer", kafka);
    }
}

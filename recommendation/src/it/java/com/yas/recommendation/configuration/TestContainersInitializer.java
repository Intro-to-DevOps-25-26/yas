package com.yas.recommendation.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.0.9"));

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        DockerImageName.parse("pgvector/pgvector:pg16")
            .asCompatibleSubstituteFor("postgres"));

    static {
        kafka.start();
        postgres.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            ctx,
            "spring.kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
            "spring.kafka.consumer.bootstrap-servers=" + kafka.getBootstrapServers(),
            "spring.kafka.producer.bootstrap-servers=" + kafka.getBootstrapServers(),
            "spring.kafka.consumer.auto-offset-reset=earliest",
            "spring.datasource.url=" + postgres.getJdbcUrl(),
            "spring.datasource.username=" + postgres.getUsername(),
            "spring.datasource.password=" + postgres.getPassword()
        );
        ctx.getBeanFactory().registerSingleton("kafkaContainer", kafka);
        ctx.getBeanFactory().registerSingleton("pgvectorContainer", postgres);
    }
}

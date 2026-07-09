package com.yas.search.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

@TestConfiguration
public class SearchIntegrationTestConfiguration {

    @Value("${elasticsearch.version}")
    private String elasticSearchVersion;

    @Bean
    @ServiceConnection
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer(elasticSearchVersion);
    }

    @Bean(destroyMethod = "stop")
    public KeycloakContainer keycloakContainer() {
        return new KeycloakContainer()
            .withRealmImportFiles("/test-realm.json")
            .withReuse(true);
    }

    @Bean
    public DynamicPropertyRegistrar keycloakDynamicProperties(KeycloakContainer keycloakContainer) {
        return registry -> {
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/quarkus"
            );
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloakContainer.getAuthServerUrl()
                    + "/realms/quarkus/protocol/openid-connect/certs"
            );
        };
    }

}

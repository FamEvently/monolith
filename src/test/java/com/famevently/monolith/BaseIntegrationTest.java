package com.famevently.monolith;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

@Tag("integration")
@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public abstract class BaseIntegrationTest
{

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("resource")
    protected static final MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>(
            DockerImageName.parse("mariadb:10.11")
    ).withDatabaseName("famevently")
     .withUsername("test")
     .withPassword("test");

    protected static final WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.wireMockConfig().dynamicPort()
    );

    static
    {
        mariaDBContainer.start();
        wireMockServer.start();
    }

    @TestConfiguration
    static class InfrastructureConfig
    {

        @Bean(destroyMethod = "stop")
        WireMockServer wireMockServer()
        {
            return wireMockServer;
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected Environment environment;

    @BeforeEach
    void setupGlobalConfig()
    {
        WireMock.configureFor("localhost", wireMockServer.port());
        LOGGER.info("WireMock active on port {}", wireMockServer.port());
    }

    @AfterEach
    public void tearDown()
    {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry)
    {
        // WireMock properties
        final String root = "http://localhost:" + wireMockServer.port();
        registry.add("services.user-sessions.base-url",
                () -> root + "/api-gateway-service/");

        // Database properties
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
    }
}


package ru.sweetbun.initializer;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class BaseDataInitializerTests {

    @Container
    public static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock")
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("kudago.api.url", () -> "http://localhost:" + wiremock.getMappedPort(8080));
    }

    @BeforeEach
    void setUp() {
        String host = wiremock.getHost();
        int port = wiremock.getMappedPort(8080);
        WireMock.configureFor(host, port);
    }

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }
}

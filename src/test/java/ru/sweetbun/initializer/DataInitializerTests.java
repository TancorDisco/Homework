package ru.sweetbun.initializer;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class DataInitializerTests {

    @Container
    public static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock")
            .withExposedPorts(8080);

    @Autowired
    private KudaGoService kudaGoService;

    @Autowired
    private Storage<Category> categoryStorage;

    @Autowired
    private Storage<Location> locationStorage;

    private static final String tailCategoryUrl = "/public-api/v1.4/places/categories";
    private static final String tailLocationUrl = "/public-api/v1.4/locations";

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

    @Test
    void run_LocationInitialization_IsSuccess() throws Exception {
        WireMock.stubFor(get(urlPathEqualTo(tailLocationUrl))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"slug\":\"ekb\",\"name\":\"Екатеринбург\"}," +
                                " {\"slug\":\"nsk\",\"name\":\"Новосибирск\"}]")));
        new DataInitializer(kudaGoService, categoryStorage, locationStorage).run();

        List<Location> locations = kudaGoService.fetchAll(wiremock.getBaseUrl() + tailLocationUrl, Location[].class);

        assertNotNull(locations);
        assertEquals(2, locations.size());
        assertEquals("Екатеринбург", locations.get(0).getName());
        assertEquals("nsk", locations.get(1).getSlug());
    }

    @Test
    void run_CategoryInitialization_IsSuccess() throws Exception {
        WireMock.stubFor(get(urlPathEqualTo(tailCategoryUrl))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"slug\":\"anticafe\",\"name\":\"Антикафе\"}," +
                                "{\"id\":2,\"slug\":\"airports\",\"name\":\"Аэропорты\"}]")));
        new DataInitializer(kudaGoService, categoryStorage, locationStorage).run();

        List<Category> categories = kudaGoService.fetchAll(wiremock.getBaseUrl() + tailCategoryUrl, Category[].class);

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Антикафе", categories.get(0).getName());
        assertEquals("airports", categories.get(1).getSlug());
    }
}
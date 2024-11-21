package ru.sweetbun.initializer;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sweetbun.entity.Location;
import ru.sweetbun.service.KudaGoService;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InitializerLocationsCommandTests extends BaseDataInitializerTests{

    @Autowired
    private KudaGoService<Location> locationKudaGoService;

    private static final String tailLocationUrl = "/public-api/v1.4/locations";

    @Test
    void run_LocationInitialization_IsSuccess() throws Exception {
        WireMock.stubFor(get(urlPathEqualTo(tailLocationUrl))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"slug\":\"ekb\",\"name\":\"Екатеринбург\"}," +
                                " {\"slug\":\"nsk\",\"name\":\"Новосибирск\"}]")));

        List<Location> locations =
                locationKudaGoService.fetchAll(wiremock.getBaseUrl() + tailLocationUrl, Location[].class);

        assertNotNull(locations);
        assertEquals(2, locations.size());
        assertEquals("Екатеринбург", locations.get(0).getName());
        assertEquals("nsk", locations.get(1).getSlug());
    }
}
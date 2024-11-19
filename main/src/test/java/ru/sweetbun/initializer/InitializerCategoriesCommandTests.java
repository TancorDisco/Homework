package ru.sweetbun.initializer;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sweetbun.entity.Category;
import ru.sweetbun.service.KudaGoService;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InitializerCategoriesCommandTests extends BaseDataInitializerTests{

    @Autowired
    private KudaGoService<Category> categoryKudaGoService;

    private static final String tailCategoryUrl = "/public-api/v1.4/places/categories";

    @Test
    void run_CategoryInitialization_IsSuccess() throws Exception {
        WireMock.stubFor(get(urlPathEqualTo(tailCategoryUrl))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"slug\":\"anticafe\",\"name\":\"Антикафе\"}," +
                                "{\"id\":2,\"slug\":\"airports\",\"name\":\"Аэропорты\"}]")));

        List<Category> categories =
                categoryKudaGoService.fetchAll(wiremock.getBaseUrl() + tailCategoryUrl, Category[].class);

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Антикафе", categories.get(0).getName());
        assertEquals("airports", categories.get(1).getSlug());
    }
}
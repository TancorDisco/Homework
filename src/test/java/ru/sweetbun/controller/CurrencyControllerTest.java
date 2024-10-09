package ru.sweetbun.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.sweetbun.DTO.ConvertRequest;
import ru.sweetbun.exception.CurrencyServiceUnavailableException;
import ru.sweetbun.service.CurrencyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void getCurrencyRate_WhenInvalidCurrency_ShouldReturnNotFound() throws Exception {
        when(currencyService.getCurrencyRate(anyString())).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get("/currencies/rates/INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrencyRate_WhenCurrencyValid_ShouldReturnOk() throws Exception {
        when(currencyService.getCurrencyRate(anyString())).thenReturn(100.0);

        mockMvc.perform(get("/currencies/rates/USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(100.0));
    }

    @Test
    void convertCurrency_WhenValidRequest_ShouldReturnConvertedAmount() throws Exception {
        when(currencyService.convertCurrency(Mockito.any(ConvertRequest.class))).thenReturn(1.1);

        String jsonRequest = """
            {
              "fromCurrency": "USD",
              "toCurrency": "EUR",
              "amount": 1
            }
            """;

        mockMvc.perform(post("/currencies/convert")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(1.1));
    }

    @Test
    void convertCurrency_WhenAmountMissing_ShouldReturnBadRequest() throws Exception {
        when(currencyService.convertCurrency(any())).thenThrow(IllegalArgumentException.class);

        String jsonRequest = """
            {
              "fromCurrency": "USD",
              "toCurrency": "RUB"
            }
            """;

        mockMvc.perform(post("/currencies/convert")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrencyRate_WhenServiceUnavailable_ShouldReturnServiceUnavailable() throws Exception {
        when(currencyService.getCurrencyRate(anyString())).thenThrow(new CurrencyServiceUnavailableException("Currency service is unavailable"));

        mockMvc.perform(get("/currencies/rates/USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Retry-After", "3600"));
    }
}

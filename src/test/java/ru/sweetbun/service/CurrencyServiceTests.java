package ru.sweetbun.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.sweetbun.DTO.ConvertRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyServiceTests {

    @Mock
    private ExternalCurrencyService externalCurrencyService;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "'', USD, 100",
            "USD, '', 100",
            "USD, EUR, -1",
            "INVALID, USD, 100",
            "USD, INVALID, 100",
            "USD, EUR, 0",
            ",,100",
            ",,"
    })
    void validateConvertRequest_WhenInvalidInputs_ShouldThrowException(String fromCurrency, String toCurrency,
                                                                       Double amount) {
        ConvertRequest request = new ConvertRequest(fromCurrency, toCurrency, amount);

        assertThrows(IllegalArgumentException.class, () -> {
            currencyService.convertCurrency(request);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "'',",
            "INVALID"
    })
    void validateCurrencyCode_WhenInvalidCurrencyCode_ShouldThrowException(String currencyCode) {
        assertThrows(IllegalArgumentException.class, () -> {
            currencyService.getCurrencyRate(currencyCode);
        });
    }
}
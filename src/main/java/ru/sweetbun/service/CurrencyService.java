package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.DTO.ConvertRequest;

import java.util.Currency;

@Slf4j
@Service
public class CurrencyService {

    private final ExternalCurrencyService externalCurrencyService;

    @Autowired
    public CurrencyService(ExternalCurrencyService externalCurrencyService) {
        this.externalCurrencyService = externalCurrencyService;
    }

    public Double getCurrencyRate(String code) {
        validateCurrencyCode(code);

        return externalCurrencyService.getCurrencyRate(code);
    }

    public Double convertCurrency(ConvertRequest convertRequest) {
        validateConvertRequest(convertRequest);

        Double fromRate = getCurrencyRate(convertRequest.getFromCurrency());
        Double toRate = getCurrencyRate(convertRequest.getToCurrency());
        return convertRequest.getAmount() * (fromRate / toRate);
    }

    private void validateCurrencyCode(String code) {
        if (isInvalidCurrency(code)) {
            throw new IllegalArgumentException("Currency not exist");
        }
    }

    private void validateConvertRequest(ConvertRequest convertRequest) {
        if (convertRequest == null) {
            throw new IllegalArgumentException("Request must not be null");
        }
        if (convertRequest.getFromCurrency() == null || convertRequest.getFromCurrency().isEmpty() ||
                convertRequest.getToCurrency() == null || convertRequest.getToCurrency().isEmpty()) {
            throw new IllegalArgumentException("Currency codes must not be null or empty");
        }
        if (convertRequest.getAmount() == null || convertRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (isInvalidCurrency(convertRequest.getFromCurrency()) || isInvalidCurrency(convertRequest.getToCurrency())) {
            throw new IllegalArgumentException("Currency not exist");
        }
    }

    public boolean isInvalidCurrency(String code) {
        try {
            Currency.getInstance(code);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}

package ru.sweetbun.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.exception.CurrencyNotFoundException;
import ru.sweetbun.exception.CurrencyServiceUnavailableException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Service
public class ExternalCurrencyService {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    @Autowired
    public ExternalCurrencyService(RestTemplate restTemplate,
                                   @Value("${currency.api-url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @Cacheable(value = "currencyRates", key = "#code", unless = "#result == null", cacheManager = "cacheManager")
    @CircuitBreaker(name = "currencyService", fallbackMethod = "getCurrencyRateFallback")
    public Double getCurrencyRate(String code) {
        String url = apiUrl + "?date_req=" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            log.error("Currency service is unavailable", e);
            throw new CurrencyServiceUnavailableException("Currency service is unavailable");
        }
        log.info("Request with code: {}", code);
        if (response.getStatusCode() == HttpStatus.OK) {
            return parseCurrencyRateFromXml(response.getBody(), code);
        }
        return null;
    }

    private Double parseCurrencyRateFromXml(String xmlData, String code) {
        try {
            Document doc = Jsoup.parse(xmlData, "", Parser.xmlParser());
            Element valuteElement = doc.selectFirst("Valute:has(CharCode:matchesOwn(" + code + "))");
            if (valuteElement != null) {
                String rateString = Objects.requireNonNull(valuteElement.selectFirst("Value"))
                        .text().replace(",", ".");
                return Double.parseDouble(rateString);
            } else {
                throw new CurrencyNotFoundException("Currency not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getCurrencyRateFallback(String code, Throwable throwable) {
        return null;
    }
}

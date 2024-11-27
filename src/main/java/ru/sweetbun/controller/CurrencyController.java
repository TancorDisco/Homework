package ru.sweetbun.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.ConvertRequest;
import ru.sweetbun.DTO.ConvertResponse;
import ru.sweetbun.DTO.CurrencyRateResponse;
import ru.sweetbun.DTO.ErrorResponse;
import ru.sweetbun.service.CurrencyService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/currencies" )
public class CurrencyController {

    private final CurrencyService currencyService;
    private final Counter counter;

    @Autowired
    public CurrencyController(CurrencyService currencyService, MeterRegistry meterRegistry) {
        this.currencyService = currencyService;
        this.counter = meterRegistry.counter("my-counter");
    }

    @Operation(summary = "Get the currency rate by currency code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved currency rate",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyRateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Currency service unavailable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("rates/{code}")
    public ResponseEntity<?> getCurrencyRate(@PathVariable String code) {
        this.counter.increment();
        String requestId = UUID.randomUUID().toString();

        try (var ignored = MDC.putCloseable("requestId", requestId)) {
            log.info("Processing request");
            try (var ignored2 = MDC.putCloseable(("code"), code)) {
                Double rate = currencyService.getCurrencyRate(code);
                var response = new CurrencyRateResponse(code, rate);
                return ResponseEntity.ok(response);
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Convert currency from one to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted currency",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConvertResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Currency service unavailable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("convert")
    public ResponseEntity<?> convertCurrencies(@RequestBody ConvertRequest convertRequest) {
        Double convertedAmount = currencyService.convertCurrency(convertRequest);
        var response = new ConvertResponse(convertRequest.getFromCurrency(),
                convertRequest.getToCurrency(), convertedAmount);
        return ResponseEntity.ok(response);
    }
}

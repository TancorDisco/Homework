package ru.sweetbun.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.ConvertRequest;
import ru.sweetbun.DTO.ConvertResponse;
import ru.sweetbun.DTO.CurrencyRateResponse;
import ru.sweetbun.DTO.ErrorResponse;
import ru.sweetbun.exception.CurrencyNotFoundException;
import ru.sweetbun.exception.CurrencyServiceUnavailableException;
import ru.sweetbun.service.CurrencyService;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
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
        Double rate = currencyService.getCurrencyRate(code);
        var response = new CurrencyRateResponse(code, rate);
        return ResponseEntity.ok(response);
    }

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyNotFoundException(CurrencyNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(CurrencyServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyServiceUnavailableException(CurrencyServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", "3600")
                .body(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage()));
    }
}

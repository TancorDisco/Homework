package ru.sweetbun.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyRateResponse {

    private String currency;
    private Double rate;
}

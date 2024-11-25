package ru.sweetbun.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertResponse {

    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
}

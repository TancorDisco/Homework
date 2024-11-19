package ru.sweetbun.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertRequest {

    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}

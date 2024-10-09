package ru.sweetbun.DTO;

import lombok.Data;

@Data
public class ConvertRequest {

    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}

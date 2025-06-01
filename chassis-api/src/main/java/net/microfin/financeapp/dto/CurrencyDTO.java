package net.microfin.financeapp.dto;

import java.math.BigDecimal;

public record CurrencyDTO(String name, String code, BigDecimal value) {
}

package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ExchangeOperationResultDTO implements OperationResult {
    @EqualsAndHashCode.Include
    @NotNull
    private Integer operationId;
    private String status;
    private String message;
    private BigDecimal targetBalance;
    private BigDecimal sourceBalance;
    @NotNull
    private Integer targetAccountId;
    @NotNull
    private Integer sourceAccountId;
}

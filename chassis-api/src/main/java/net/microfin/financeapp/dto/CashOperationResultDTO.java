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
public class CashOperationResultDTO {
    @EqualsAndHashCode.Include
    @NotNull
    private Integer operationId;
    @NotNull
    private BigDecimal newBalance;
    @NotNull
    private String status;
    @NotNull
    private String message;
}

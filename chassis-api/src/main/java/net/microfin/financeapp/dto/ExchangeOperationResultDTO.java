package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeOperationResultDTO implements OperationResult {
    @EqualsAndHashCode.Include
    @NotNull
    private Integer operationId;
    @NotNull
    private OperationStatus status;
    private String message;
    private BigDecimal targetBalance;
    private BigDecimal sourceBalance;
    @NotNull
    private Integer targetAccountId;
    @NotNull
    private Integer sourceAccountId;
}

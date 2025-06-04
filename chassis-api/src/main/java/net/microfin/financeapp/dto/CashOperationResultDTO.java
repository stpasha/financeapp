package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CashOperationResultDTO implements OperationResult {
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

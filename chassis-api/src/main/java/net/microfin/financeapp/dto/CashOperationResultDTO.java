package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CashOperationResultDTO implements OperationResult {
    @EqualsAndHashCode.Include
    @NotNull
    private UUID operationId;
    @NotNull
    private BigDecimal newBalance;
    @NotNull
    private OperationStatus status;
    @NotNull
    private String message;
}

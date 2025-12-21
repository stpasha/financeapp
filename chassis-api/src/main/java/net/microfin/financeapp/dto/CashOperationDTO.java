package net.microfin.financeapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashOperationDTO implements GenericOperationDTO {

    @EqualsAndHashCode.Include
    private UUID id;

    private UUID accountId;

    @NotNull
    private OperationType operationType;

    @NotNull
    private UUID userId;

    @NotNull
    private Currency currencyCode;

    @NotNull
    @DecimalMin(value = "0.00", message = "Amount must be 0 or positive")
    private BigDecimal amount;

    private LocalDateTime createdAt;

    @NotNull
    private OperationStatus status;
}

package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashOperationDTO {

    @EqualsAndHashCode.Include
    private Integer id;

    private Integer accountId;

    @NotNull
    private OperationType operationType;

    @NotNull
    private Integer userId;

    @NotNull
    private Currency currencyCode;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime createdAt;

    @NotNull
    private OperationStatus status;
}

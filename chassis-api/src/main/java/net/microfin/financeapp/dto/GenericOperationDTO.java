package net.microfin.financeapp.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class GenericOperationDTO {
    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    private OperationType operationType;

    private Integer accountId;

    @NotNull
    private Integer userId;

    private Currency currencyCode;

    @NotNull
    private BigDecimal amount;

    private BigDecimal targetAmount;

    @PastOrPresent
    private LocalDateTime createdAt;

    @NotNull
    private OperationStatus status;

    private Integer targetAccountId;

    private Integer sourceAccountId;

}

package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
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
public class ExchangeOperationDTO {

    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    private OperationType operationType;

    @NotNull
    private Integer targetAccountId;

    @NotNull
    private Integer sourceAccountId;

    private Integer userId;

    @NotNull
    private BigDecimal amount;

    private BigDecimal targetAmount;

    @PastOrPresent
    private LocalDateTime createdAt;

    @NotNull
    private OperationStatus status;
}

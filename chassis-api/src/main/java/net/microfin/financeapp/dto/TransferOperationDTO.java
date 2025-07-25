package net.microfin.financeapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransferOperationDTO implements GenericOperationDTO {

    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer targetAccountId;

    @NotNull
    private Integer sourceAccountId;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    private BigDecimal targetAmount;

    @PastOrPresent
    private LocalDateTime createdAt;

    @NotNull
    private OperationType operationType;

    @NotNull
    private OperationStatus status;
}

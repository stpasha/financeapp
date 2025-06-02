package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.microfin.financeapp.util.OperationStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class TransferOperationResultDTO implements OperationResult {
    @EqualsAndHashCode.Include
    @NotNull
    private Integer operationId;
    private OperationStatus status;
    private String message;
    private BigDecimal targetBalance;
    private BigDecimal sourceBalance;
    @NotNull
    private Integer targetAccountId;
    @NotNull
    private Integer sourceAccountId;
}

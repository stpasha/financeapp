package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.microfin.financeapp.util.OperationStatus;

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
}

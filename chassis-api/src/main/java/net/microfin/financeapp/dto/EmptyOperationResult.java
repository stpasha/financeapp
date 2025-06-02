package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.microfin.financeapp.util.OperationStatus;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EmptyOperationResult implements OperationResult {
    @EqualsAndHashCode.Include
    private Integer operationId;
    @NotNull
    private OperationStatus status;
    private String message;
}

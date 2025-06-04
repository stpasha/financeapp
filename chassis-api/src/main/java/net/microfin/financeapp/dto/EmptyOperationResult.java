package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EmptyOperationResult implements OperationResult {
    @EqualsAndHashCode.Include
    private Integer operationId;
    @NotNull
    private OperationStatus status;
    private String message;
}

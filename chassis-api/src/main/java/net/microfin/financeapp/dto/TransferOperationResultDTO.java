package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.microfin.financeapp.util.OperationStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TransferOperationResultDTO implements OperationResult {
    @EqualsAndHashCode.Include
    @NotNull
    private UUID operationId;
    private OperationStatus status;
    private String message;
}

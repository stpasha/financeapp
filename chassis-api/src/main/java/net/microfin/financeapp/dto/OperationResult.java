package net.microfin.financeapp.dto;

import net.microfin.financeapp.util.OperationStatus;

public interface OperationResult {
    String getMessage();
    OperationStatus getStatus();
}

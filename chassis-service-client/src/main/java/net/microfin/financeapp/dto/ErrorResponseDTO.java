package net.microfin.financeapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(int code, String message, List<String> errors, LocalDateTime timestamp) {
}

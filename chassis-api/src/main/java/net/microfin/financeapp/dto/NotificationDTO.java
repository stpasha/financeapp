package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationDTO {
    @NotNull
    private UUID id;
    @NotNull
    private String operationType;
    @Size(max = 255, message = "Notification Description should be not greater than 255 symbols")
    private String notificationDescription;
    @PastOrPresent
    private LocalDateTime createdAt;
    private UUID userId;
    private boolean delivered;
}

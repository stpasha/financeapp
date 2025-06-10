package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationDTO {
    @NotNull
    private Integer id;
    @NotNull
    private String operationType;
    @Size(max = 255, message = "Notification Description should be not greater than 255 symbols")
    private String notificationDescription;
    @PastOrPresent
    private LocalDateTime createdAt;
    private Integer userId;
    private boolean delivered;
}

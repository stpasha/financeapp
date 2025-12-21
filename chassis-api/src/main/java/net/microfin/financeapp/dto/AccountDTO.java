package net.microfin.financeapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @EqualsAndHashCode.Include
    private UUID id;
    private UserDTO user;
    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal balance;
    @NotNull
    @Size(max = 3, message = "Currency code should be not greater than 3 symbols")
    private String currencyCode;
    @NotNull
    private Boolean active;
}

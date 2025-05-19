package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter
@Setter
@Builder
public class CashOperationDTO {

    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    private Integer operationType;

    private Integer accountId;

    @NotNull
    private Integer userId;

    @NotNull
    @Size(max = 3, message = "Currency code should be not greater than 3 symbols")
    private String currencyCode;

    @NotNull
    private BigDecimal amount;

    @PastOrPresent
    private LocalDateTime createdAt;

    @NotNull
    private Integer statusId;
}

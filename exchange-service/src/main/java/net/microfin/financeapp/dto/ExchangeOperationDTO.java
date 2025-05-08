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
public class ExchangeOperationDTO {

    @EqualsAndHashCode.Include
    private Integer id;

    @NotNull
    private Integer operationType;

    @NotNull
    private Integer targetAccountId;

    @NotNull
    private Integer sourceAccountId;

    @NotNull
    private BigDecimal amount;

    @PastOrPresent
    private LocalDateTime createdAt;

    @NotNull
    private Integer statusId;
}

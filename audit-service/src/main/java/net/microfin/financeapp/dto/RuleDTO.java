package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class RuleDTO {
    @EqualsAndHashCode.Include
    private Integer id;
    @NotNull
    private Integer operationType;
    @Size(max = 255, message = "Rule condition length 255 symbols max")
    @NotNull
    private String ruleCondition;
    @Size(max = 100, message = "Rule condition length 100 symbols max")
    @NotNull
    private String field;
}

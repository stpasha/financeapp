package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RuleDTO {
    @EqualsAndHashCode.Include
    private Integer id;
    @NotNull
    private String operationType;
    @Size(max = 255, message = "Rule condition length 255 symbols max")
    @NotNull
    private String ruleCondition;
    @Size(max = 100, message = "Rule field length 100 symbols max")
    @NotNull
    private String field;
}

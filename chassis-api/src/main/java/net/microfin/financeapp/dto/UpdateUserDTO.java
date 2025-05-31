package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    @EqualsAndHashCode.Include
    private Integer id;
    @Size(max = 255, min = 5, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String fullName;
    @PastOrPresent
    private LocalDate dob;
}

package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Size(max = 25, min = 5, message = "Фамилия Имя должны быть от 5 до 25 символов")
    @NotNull
    private String fullName;
    @Past(message = "Дата должна быть в прошлом")
    private LocalDate dob;
}

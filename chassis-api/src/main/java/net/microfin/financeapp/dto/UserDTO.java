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

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @EqualsAndHashCode.Include
    private Integer id;
    private UUID keycloakId;
    @EqualsAndHashCode.Include
    @Size(max = 255, message = "Username should be not greater than 255 symbols")
    @NotNull
    private String username;
    @Size(max = 255, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String password;
    @Size(max = 255, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String confirmPassword;
    @Size(max = 255, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String fullName;
    @PastOrPresent
    private LocalDate dob;
    @NotNull
    private Boolean enabled;
}

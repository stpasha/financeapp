package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    @EqualsAndHashCode.Include
    @NotNull
    private Integer id;
    @EqualsAndHashCode.Include
    private UUID keycloakId;
    @NotNull
    private String password;
    @Size(max = 255, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String confirmPassword;
}

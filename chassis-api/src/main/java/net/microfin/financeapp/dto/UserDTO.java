package net.microfin.financeapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@Builder
public class UserDTO {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Include
    @Size(max = 255, message = "Username should be not greater than 255 symbols")
    @NotNull
    private String username;
    @Size(max = 255, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String password;
    @Size(max = 255, message = "Password should be not greater than 255 symbols")
    @NotNull
    private String fullName;
    @PastOrPresent
    private LocalDateTime dob;
    @NotNull
    private Boolean active;
    private List<AccountDTO> enabled;
}

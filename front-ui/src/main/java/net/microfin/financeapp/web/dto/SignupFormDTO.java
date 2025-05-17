package net.microfin.financeapp.web.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class SignupFormDTO {
    @EqualsAndHashCode.Include
    private String username;
    private String password;
    private String fullName;
    private LocalDateTime dob;
}

package net.microfin.financeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileDTO {
    private UserDTO user;
    private List<AccountDTO> accounts;
    private PasswordDTO password;
}

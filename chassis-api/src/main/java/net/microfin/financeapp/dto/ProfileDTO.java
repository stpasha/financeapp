package net.microfin.financeapp.dto;

import lombok.*;

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

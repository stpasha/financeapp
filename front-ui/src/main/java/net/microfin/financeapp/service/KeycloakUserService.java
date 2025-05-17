package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.web.dto.SignupFormDTO;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private Keycloak keycloak;

    public void createUser(SignupFormDTO signupFormDTO) {
        return;
    }
}

package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.UserClient;
import net.microfin.financeapp.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KeycloakUserService keycloakUserService;
    private final UserClient userClient;

    @Transactional
    public void create(UserDTO userDTO) {
        try {
            RestClient client = RestClient.create("http://gateway-service:8082/user");
            RestClient.ResponseSpec responseSpec = client.post().body(userDTO).contentType(MediaType.APPLICATION_JSON).retrieve();
            ResponseEntity<UserDTO> userResponse = responseSpec.toEntity(UserDTO.class);
            if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                UserRepresentation keycloakResp = null;
                try {
                    keycloakResp = keycloakUserService.createUser(userDTO);
                } catch (Exception e) {
                    UserDTO createdDto = userResponse.getBody();
                    createdDto.setEnabled(false);
                    client.post().body(createdDto).contentType(MediaType.APPLICATION_JSON).retrieve();
                    return;
                }
                UserDTO createdDto = userResponse.getBody();
                createdDto.setEnabled(true);
                createdDto.setKeycloakId(UUID.fromString(keycloakResp.getId()));
                client.put().body(createdDto).contentType(MediaType.APPLICATION_JSON).retrieve();
            }
        } catch (Exception e) {
            //TODO new Exception
            throw new RuntimeException(e);
        }
    }
}

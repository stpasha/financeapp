package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.UserClient;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserClient userClient;

    @Transactional
    public void create(UserDTO userDTO) {
        try {
            RestClient client = RestClient.create("http://gateway-service:8082/user");
            RestClient.ResponseSpec responseSpec = client.post().body(userDTO).contentType(MediaType.APPLICATION_JSON).retrieve();
            ResponseEntity<UserDTO> userResponse = responseSpec.toEntity(UserDTO.class);
        } catch (Exception e) {
            //TODO new Exception
            throw new RuntimeException(e);
        }
    }
}

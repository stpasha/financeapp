package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.UserClient;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {
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

    @Transactional
    public void changePassword(PasswordDTO passwordDTO) {
        userClient.updatePassword(passwordDTO);
    }

    @Transactional
    public UserDTO updateUser(UpdateUserDTO userDTO) {
        ResponseEntity<UserDTO> responseEntity = userClient.update(userDTO);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to update user");
    }

    public UserDTO queryUserInfo(String userName) {
        return userClient.getUserByName(userName).getBody();
    }

}

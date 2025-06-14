package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.UserClient;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    @Transactional
    public void create(UserDTO userDTO) {
        userClient.create(userDTO);
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

    public List<UserDTO> queryTargeUsers() {
        ResponseEntity<List<UserDTO>> responseEntity = userClient.getUsers();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return List.of();
    }

}

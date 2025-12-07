package net.microfin.financeapp.service;

import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.AccountClient;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AccountClient userClient;

    @Transactional
    public boolean create(UserDTO userDTO) {
        ResponseEntity<UserDTO> entity = userClient.create(userDTO);
        return HttpStatus.CREATED.equals(entity.getStatusCode());
    }

    @Transactional
    public boolean changePassword(PasswordDTO passwordDTO) {
        ResponseEntity<UserDTO> entity = userClient.updatePassword(passwordDTO);
        return HttpStatus.ACCEPTED.equals(entity.getStatusCode());
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

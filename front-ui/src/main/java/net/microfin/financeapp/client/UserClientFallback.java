package net.microfin.financeapp.client;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public ResponseEntity<UserDTO> create(UserDTO userDTO) {
        log.warn("Fallback triggered for create user due to service unavailability");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> update(UpdateUserDTO userDTO) {
        log.warn("Fallback triggered for update user due to service unavailability");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> updatePassword(PasswordDTO userDTO) {
        log.warn("Fallback triggered for updatePassword due to service unavailability");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> getUserByName(String username) {
        log.warn("Fallback triggered for getUserByName with username={} due to service unavailability", username);
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<List<UserDTO>> getUsers() {
        log.warn("Fallback triggered for getUsers due to service unavailability");
        return ResponseEntity.ok(Collections.emptyList());
    }
}

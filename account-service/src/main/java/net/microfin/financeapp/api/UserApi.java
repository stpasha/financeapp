package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.mapper.UserLegacyMapper;
import net.microfin.financeapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;
    private final UserLegacyMapper userLegacyMapper;
    private final ExceptionsProperties exceptionsProperties;

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO) throws JsonProcessingException {
        return userService.createUser(userDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED))
                .orElseThrow(() -> new IllegalStateException(exceptionsProperties.getMakeUserFailure() + userDTO.getUsername()));
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UpdateUserDTO updateDTO) {
        return userService.updateUser(updateDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new IllegalStateException("Пользователь не отредактирован " + updateDTO.getFullName()));
    }

    @PutMapping("/password")
    ResponseEntity updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) throws JsonProcessingException {
        return userService.updatePassword(passwordDTO).map(password -> new ResponseEntity(HttpStatus.ACCEPTED))
                .orElseThrow(() -> new IllegalStateException(exceptionsProperties.getPassEditFailure()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByName(@PathVariable("username") String username) {
        return userService.getUserByUsername(username)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new IllegalStateException(exceptionsProperties.getSearchUserFailure() + username));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }
}

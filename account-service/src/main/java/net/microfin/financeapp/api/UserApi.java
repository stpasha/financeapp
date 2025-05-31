package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class UserApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO) throws JsonProcessingException {
        return userService.createUser(userDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED))
                .orElseThrow(() -> new IllegalStateException("Пользователь не создан " + userDTO.getUsername()));
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UpdateUserDTO updateDTO) {
        UserDTO userDTO = userMapper.toDto(updateDTO);
        return userService.updateUser(userDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new IllegalStateException("Пользователь не отредактирован " + userDTO.getUsername()));
    }

    @PutMapping("/password")
    ResponseEntity updatePassword(@Valid @RequestBody PasswordDTO passwordDTO) throws JsonProcessingException {
        return userService.updatePassword(passwordDTO).map(password -> new ResponseEntity(HttpStatus.ACCEPTED))
                .orElseThrow(() -> new RuntimeException("Password is not updated"));
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUserByName(@RequestParam(name = "username", required = true) String username) {
        return userService.getUserByUsername(username)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден по имени " + username));
    }
}

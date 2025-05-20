package net.microfin.financeapp.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class UserApi {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.CREATED))
                .orElseThrow(() -> new ResourceAccessException("Пользователь не создан " + userDTO.getUsername()));
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceAccessException("Пользователь не отредактирован " + userDTO.getUsername()));
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUserByName(@RequestParam(name = "username", required = true) String username) {
        return userService.getUserByUsername(username)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResourceAccessException("Пользователь не найден по имени " + username));
    }
}

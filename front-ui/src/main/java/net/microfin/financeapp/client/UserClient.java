package net.microfin.financeapp.client;

import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-client", url = "http://finance.local", fallback = UserClientFallback.class)
public interface UserClient {
    @PostMapping("/api/user")
    ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO);

    @PutMapping("/api/user")
    ResponseEntity<UserDTO> update(@RequestBody UpdateUserDTO userDTO);

    @PutMapping("/api/user/password")
    ResponseEntity<UserDTO> updatePassword(@RequestBody PasswordDTO userDTO);

    @GetMapping("/api/user/{username}")
    ResponseEntity<UserDTO> getUserByName(@PathVariable(name = "username") String username);

    @GetMapping("/api/user")
    ResponseEntity<List<UserDTO>> getUsers();
}

package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-client", url = "http://gateway-service:8082")
public interface UserClient {
    @PostMapping("/user")
    ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO);

    @PutMapping("/user")
    ResponseEntity<UserDTO> update(@RequestBody UpdateUserDTO userDTO);

    @PutMapping("/user/password")
    ResponseEntity<UserDTO> updatePassword(@RequestBody PasswordDTO userDTO);

    @GetMapping("/user/{username}")
    ResponseEntity<UserDTO> getUserByName(@PathVariable(name = "username") String username);

    @GetMapping("/user")
    ResponseEntity<List<UserDTO>> getUsers();
}

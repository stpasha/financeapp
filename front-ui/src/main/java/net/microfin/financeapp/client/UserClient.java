package net.microfin.financeapp.client;

import jakarta.validation.Valid;
import net.microfin.financeapp.config.FeignConfig;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "external-api", url = "http://gateway-service:8082", configuration = FeignConfig.class)
public interface UserClient {
    @PostMapping("/user")
    ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO);

    @PutMapping("/user")
    ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO);

    @PutMapping("/user/password")
    ResponseEntity<UserDTO> updatePassword(@RequestBody PasswordDTO userDTO);

    @GetMapping("/user")
    ResponseEntity<UserDTO> getUserByName(@RequestParam(name = "username", required = true) String username);
}

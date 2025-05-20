package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> getUserByUsername(String username);
    Optional<UserDTO> createUser(UserDTO userDTO);
    Optional<UserDTO> updateUser(UserDTO userDTO);
}

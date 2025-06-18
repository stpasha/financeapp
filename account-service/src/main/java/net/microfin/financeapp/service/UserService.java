package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDTO> getUserByUsername(String username);
    Optional<UserDTO> createUser(UserDTO userDTO) throws JsonProcessingException;
    Optional<UserDTO> updateUser(UserDTO userDTO);
    Optional<PasswordDTO> updatePassword(PasswordDTO passwordDTO) throws JsonProcessingException;
    List<UserDTO> getUsers();
}

package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.UserDTO;

public interface UserService {
    UserDTO getUserByUsername();
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
}

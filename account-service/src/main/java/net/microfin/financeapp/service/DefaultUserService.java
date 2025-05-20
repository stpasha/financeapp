package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findUsersByUsername(username).map(user -> userMapper.toDto(user));
    }

    @Override
    public Optional<UserDTO> createUser(UserDTO userDTO) {
        return Optional.of(userMapper.toDto(userRepository.save(userMapper.toEntity(userDTO))));
    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return userRepository.findById(userDTO.getId()).map(dto -> userMapper.toDto(userRepository.save(userMapper.toEntity(userDTO))));
    }
}

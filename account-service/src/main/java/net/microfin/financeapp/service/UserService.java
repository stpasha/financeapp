package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.util.OperationType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OutboxEventRepository eventRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;


    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findUsersByUsername(username).map(user -> userMapper.toDto(user));
    }

    public List<UserDTO> getUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional
    public Optional<UserDTO> createUser(UserDTO userDTO) throws JsonProcessingException {
        if (LocalDate.now().getYear() - userDTO.getDob().getYear() < 18) {
            throw new IllegalStateException("Incorrect age");
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords should match");
        }
        User user = userRepository.save(userMapper.toEntity(userDTO));
        userDTO.setId(user.getId());
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(user.getId())
                .aggregateType("USER")
                .operationType(OperationType.USER_CREATED)
                .payload(objectMapper.writeValueAsString(userDTO))
                .build();
        eventRepository.save(outboxEvent);
        return Optional.of(userMapper.toDto(user));
    }

    @Transactional
    public Optional<PasswordDTO> updatePassword(PasswordDTO passwordDTO) throws JsonProcessingException {
        if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords should match");
        }
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(passwordDTO.getId())
                .aggregateType("USER")
                .operationType(OperationType.PASSWORD_CHANGED)
                .payload(objectMapper.writeValueAsString(passwordDTO))
                .build();
        eventRepository.save(outboxEvent);
        return Optional.of(passwordDTO);
    }

    @Transactional
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        if (LocalDate.now().getYear() - userDTO.getDob().getYear() < 18) {
            throw new IllegalStateException("Incorrect age");
        }
        return userRepository.findById(userDTO.getId())
                .map(existing -> Optional.of(userMapper.toDto(userRepository.save(userMapper.toPatchedEntity(userDTO, existing)))))
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userDTO.getId()));
    }
}

package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.jooq.tables.records.UsersRecord;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.repository.OutboxEventWriteRepository;
import net.microfin.financeapp.repository.UserReadRepository;
import net.microfin.financeapp.repository.UserWriteRepository;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserWriteRepository userWriteRepository;
    private final UserReadRepository userReadRepository;
    private final OutboxEventWriteRepository eventRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;


    public Optional<UserDTO> getUserByUsername(String username) {
        return userReadRepository.findUsersByUsername(username).map(user -> userMapper.toDto(user));
    }

    public List<UserDTO> getUsers() {
        return userMapper.toDtoList(userReadRepository.findAll());
    }

    @Transactional
    public Optional<UserDTO> createUser(UserDTO userDTO) throws JsonProcessingException {
        if (LocalDate.now().getYear() - userDTO.getDob().getYear() < 18) {
            throw new IllegalStateException("Incorrect age");
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords should match");
        }
        UsersRecord user = userWriteRepository.insert(userMapper.toRecord(userDTO));
        userDTO.setId(user.getUserId());
        OutboxEventsRecord outboxEvent = new OutboxEventsRecord(UuidCreator.getTimeOrderedEpoch(),
                "USER", null, user.getUserId(), OperationType.USER_CREATED.name(),
                objectMapper.writeValueAsString(userDTO), OperationStatus.PENDING.name(), 0, null, null, LocalDateTime.now(), LocalDateTime.now());
        eventRepository.insert(outboxEvent);
        return Optional.of(userMapper.toDto(user));
    }

    @Transactional
    public Optional<PasswordDTO> updatePassword(PasswordDTO passwordDTO) throws JsonProcessingException {
        if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords should match");
        }

        OutboxEventsRecord outboxEventsRecord = new OutboxEventsRecord(UuidCreator.getTimeOrderedEpoch(), "USER", null,
                passwordDTO.getId(), OperationType.PASSWORD_CHANGED.name(), objectMapper.writeValueAsString(passwordDTO),
                OperationStatus.PENDING.name(), 0, null, null, LocalDateTime.now(), LocalDateTime.now());
        eventRepository.insert(outboxEventsRecord);
        return Optional.of(passwordDTO);
    }

    @Transactional
    public Optional<UserDTO> updateUser(UpdateUserDTO userDTO) {
        if (LocalDate.now().getYear() - userDTO.getDob().getYear() < 18) {
            throw new IllegalStateException("Incorrect age");
        }
        return userReadRepository.findById(userDTO.getId())
                .map(existing -> Optional.of(userMapper.toDto(userWriteRepository.update(userMapper.toPatchedRecord(userDTO)))));
    }
}

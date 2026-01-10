package net.microfin.financeapp.mapper;

import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.jooq.tables.records.UsersRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    /* =========================
       DTO -> Record (create)
       ========================= */
    public UsersRecord toRecord(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        UsersRecord record = new UsersRecord();
        record.setUserId(dto.getId());
        record.setKeycloakId(dto.getKeycloakId());
        record.setUserName(dto.getUsername());
        record.setFullName(dto.getFullName());
        record.setDob(dto.getDob());
        record.setIsEnabled(dto.getEnabled());
        return record;
    }

    /* =========================
       Record -> DTO (read)
       ========================= */
    public UserDTO toDto(UsersRecord record) {
        if (record == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(record.getUserId());
        dto.setKeycloakId(record.getKeycloakId());
        dto.setUsername(record.getUserName());
        dto.setFullName(record.getFullName());
        dto.setDob(record.getDob());
        dto.setEnabled(record.getIsEnabled());

        // security: не маппим пароли
        dto.setPassword(null);
        dto.setConfirmPassword(null);

        return dto;
    }

    public List<UserDTO> toDtoList(List<UsersRecord> records) {
        return records.stream()
                .map(this::toDto)
                .toList();
    }

    /* =========================
       PATCH (UpdateUserDTO)
       ========================= */
    public UsersRecord toPatchedRecord(UpdateUserDTO dto) {
        UsersRecord record = new UsersRecord();

        if (dto.getFullName() != null) {
            record.setFullName(dto.getFullName());
        }
        if (dto.getDob() != null) {
            record.setDob(dto.getDob());
        }
        record.setUserId(dto.getId());

        return record;
    }
}


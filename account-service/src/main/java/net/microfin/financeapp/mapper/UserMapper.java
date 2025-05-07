package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserMapper {
    @Mapping(target = "accounts", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "accounts", ignore = true)
    UserDTO toDto(User user);
}

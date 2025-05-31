package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.UpdateUserDTO;
import net.microfin.financeapp.dto.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserMapper {
    @Mapping(target = "accounts", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "confirmPassword", ignore = true)
    UserDTO toDto(User user);

    UserDTO toDto(UpdateUserDTO updateUserDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toPatchedEntity(UserDTO userDTO, @MappingTarget User user);
}

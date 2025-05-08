package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.Rule;
import net.microfin.financeapp.dto.RuleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RuleMapper {
    Rule toEntity(RuleDTO dto);
    RuleDTO toDto(Rule entity);
}


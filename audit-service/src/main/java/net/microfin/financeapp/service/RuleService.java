package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.GenericOperationDTO;

public interface RuleService {
    boolean checkRulesForOperation(GenericOperationDTO genericOperationDTO);
}

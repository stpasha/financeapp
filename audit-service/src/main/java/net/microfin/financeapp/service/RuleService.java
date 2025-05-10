package net.microfin.financeapp.service;

import java.util.Map;

public interface RuleService {
    boolean checkRulesForOperation(Map<String,Object> operation);
}

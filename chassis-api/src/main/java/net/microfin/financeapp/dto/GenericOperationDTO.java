package net.microfin.financeapp.dto;

import net.microfin.financeapp.util.OperationType;

import java.util.UUID;


public interface GenericOperationDTO {

    OperationType getOperationType();

    UUID getId();

}

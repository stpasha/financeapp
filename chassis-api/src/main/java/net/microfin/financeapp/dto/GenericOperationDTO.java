package net.microfin.financeapp.dto;

import net.microfin.financeapp.util.OperationType;


public interface GenericOperationDTO {

    OperationType getOperationType();

    Integer getId();

}

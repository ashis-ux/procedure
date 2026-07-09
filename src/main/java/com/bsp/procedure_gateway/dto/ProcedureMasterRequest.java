package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.ProcedureType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcedureMasterRequest {

    private Long databaseId;

    private String schemaName;

    private String packageName;

    private String procedureName;

    private String description;

    private ProcedureType procedureType;

    private Integer timeoutSeconds;

    private ActiveStatus active;

}
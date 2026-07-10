package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.ProcedureType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProcedureMasterResponse {

    private Long procedureId;

    private String procedureUuid;

    private Long databaseId;

    private String databaseName;

    private String schemaName;

    private String packageName;

    private String procedureName;

    private String description;

    private ProcedureType procedureType;
   
    private String httpMethod;

    private Integer timeoutSeconds;

    private ActiveStatus active;

}
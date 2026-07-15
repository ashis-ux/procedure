package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProcedureMappingResponse {

    private Long procedureId;

    private String procedureUuid;

    private String schemaName;

    private String packageName;

    private String procedureName;

    private String httpMethod;

    private Boolean assigned;

    private ActiveStatus active;

}
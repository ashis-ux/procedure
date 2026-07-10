package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.DataType;
import com.bsp.procedure_gateway.enums.ParameterMode;

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
public class ProcedureParameterResponse {

    private Long parameterId;

    private Long procedureId;	

    private Integer parameterOrder;

    private String parameterName;

    private String dataType;

    private ParameterMode parameterMode;

    private String defaultValue;

    private String required;

    private String description;

    private ActiveStatus active;

}
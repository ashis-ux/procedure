package com.bsp.procedure_gateway.dto;


import com.bsp.procedure_gateway.enums.ActiveStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientProcedureRequest {

    private Long procedureId;

    private ActiveStatus active;

}
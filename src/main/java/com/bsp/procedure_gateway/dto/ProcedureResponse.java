package com.bsp.procedure_gateway.dto;

 

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProcedureResponse {

    private String requestId;
    
    private String message;

    private boolean success;

    private Object data;

}
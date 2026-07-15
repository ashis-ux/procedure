package com.bsp.procedure_gateway.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProcedureRequest {

    private String token;
    
    private String clientToken;

    private Map<String, Object> parameters;

}
package com.bsp.procedure_gateway.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientProcedureMappingRequest {

    @NotEmpty(message = "Procedure list cannot be empty.")
    private List<ClientProcedureRequest> procedures;
    
    

}
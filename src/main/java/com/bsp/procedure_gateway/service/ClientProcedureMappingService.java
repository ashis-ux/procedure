package com.bsp.procedure_gateway.service;

import java.util.List;

import com.bsp.procedure_gateway.dto.ClientProcedureMappingRequest;
import com.bsp.procedure_gateway.dto.ClientProcedureMappingResponse;

public interface ClientProcedureMappingService {

    /**
     * Returns all procedures with assigned flag.
     */
    List<ClientProcedureMappingResponse> getClientProcedures(
            Integer clientId);

    /**
     * Save mappings.
     */
    void saveMappings(
            Integer clientId,
            ClientProcedureMappingRequest request);

}
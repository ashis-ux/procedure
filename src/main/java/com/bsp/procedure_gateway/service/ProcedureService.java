package com.bsp.procedure_gateway.service;

import com.bsp.procedure_gateway.dto.ProcedureRequest;
import com.bsp.procedure_gateway.dto.ProcedureResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface ProcedureService {
    ProcedureResponse execute(
            String procedureUuid,
            ProcedureRequest request,
            HttpServletRequest httpRequest);

}
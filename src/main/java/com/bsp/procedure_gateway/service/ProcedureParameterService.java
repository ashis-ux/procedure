package com.bsp.procedure_gateway.service;

import java.util.List;

import com.bsp.procedure_gateway.entity.ProcedureParameter;

public interface ProcedureParameterService {

    List<ProcedureParameter> getParametersByProcedureId(
            Long procedureId);

}
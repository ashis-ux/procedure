package com.bsp.procedure_gateway.service;

import com.bsp.procedure_gateway.entity.ProcedureMaster;

public interface ProcedureMetadataService {

    ProcedureMaster getProcedure(
            String procedureUuid);

}

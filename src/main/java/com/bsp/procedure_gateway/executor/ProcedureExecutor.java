package com.bsp.procedure_gateway.executor;

 

import javax.sql.DataSource;

import com.bsp.procedure_gateway.dto.ProcedureRequest;
import com.bsp.procedure_gateway.dto.ProcedureResponse;
import com.bsp.procedure_gateway.entity.ProcedureMaster;

public interface ProcedureExecutor {

    ProcedureResponse execute(
            ProcedureMaster procedure,
            ProcedureRequest request,
            DataSource dataSource);

}

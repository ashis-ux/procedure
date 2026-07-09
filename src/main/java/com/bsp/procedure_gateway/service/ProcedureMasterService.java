package com.bsp.procedure_gateway.service;

import org.springframework.data.domain.Page;

import com.bsp.procedure_gateway.dto.ProcedureMasterRequest;
import com.bsp.procedure_gateway.dto.ProcedureMasterResponse;
import com.bsp.procedure_gateway.enums.ActiveStatus;

public interface ProcedureMasterService {

    /**
     * Create Procedure
     */
    ProcedureMasterResponse createProcedure(
            ProcedureMasterRequest request);
    
    Page<ProcedureMasterResponse> searchProcedures(

            String searchText,

            Long databaseId,

            ActiveStatus status,

            int page,

            int size);
    
    ProcedureMasterResponse getProcedure(
            Long procedureId);


    ProcedureMasterResponse updateProcedure(
            Long procedureId,
            ProcedureMasterRequest request);

}
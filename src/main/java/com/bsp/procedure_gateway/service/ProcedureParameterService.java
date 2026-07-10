package com.bsp.procedure_gateway.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.procedure_gateway.dto.ProcedureParameterRequest;
import com.bsp.procedure_gateway.dto.ProcedureParameterResponse;
import com.bsp.procedure_gateway.entity.ProcedureParameter;

public interface ProcedureParameterService {

    
    Page<ProcedureParameterResponse> getParameters(

            Long procedureId,

            int page,

            int size);

    ProcedureParameterResponse createParameter(

            Long procedureId,

            ProcedureParameterRequest request);

    ProcedureParameterResponse getParameter(

            Long parameterId);

    ProcedureParameterResponse updateParameter(

            Long parameterId,

            ProcedureParameterRequest request);

    void deleteParameter(

            Long parameterId);
    
    
    
    


}
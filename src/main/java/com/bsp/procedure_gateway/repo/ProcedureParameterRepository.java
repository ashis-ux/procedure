package com.bsp.procedure_gateway.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.entity.ProcedureParameter;

import java.util.List;

@Repository
public interface ProcedureParameterRepository
        extends JpaRepository<ProcedureParameter, Long> {

    List<ProcedureParameter> findByProcedureMasterOrderByParameterOrderAsc(
            ProcedureMaster procedureMaster
    );
    
//    List<ProcedureParameter> getParametersByProcedureId(
//            Long procedureId);

}

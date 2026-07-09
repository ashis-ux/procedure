package com.bsp.procedure_gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.ProcedureMaster;

@Repository
public interface ProcedureRepository extends

        JpaRepository<ProcedureMaster, Long>,
        JpaSpecificationExecutor<ProcedureMaster> {

    boolean existsByProcedureNameIgnoreCaseAndDatabaseMaster_DatabaseId(

            String procedureName,

            Long databaseId);

}
package com.bsp.procedure_gateway.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;

@Repository
public interface ProcedureMasterRepository extends
        JpaRepository<ProcedureMaster, Long>,
        JpaSpecificationExecutor<ProcedureMaster> {

    @EntityGraph(attributePaths = {
            "databaseMaster",
            "parameters"
    })
    Optional<ProcedureMaster> findByProcedureUuidAndActive(
            String procedureUuid,
            ActiveStatus active);

    Optional<ProcedureMaster> findByProcedureNameAndActive(
            String procedureName,
            ActiveStatus active);

    boolean existsByProcedureNameIgnoreCaseAndDatabaseMaster_DatabaseId(
            String procedureName,
            Long databaseId);
    
    boolean existsByProcedureNameIgnoreCaseAndDatabaseMaster_DatabaseIdAndProcedureIdNot(

            String procedureName,

            Long databaseId,

            Long procedureId);
    
    Optional<ProcedureMaster> findTopByOrderByProcedureIdDesc();

}
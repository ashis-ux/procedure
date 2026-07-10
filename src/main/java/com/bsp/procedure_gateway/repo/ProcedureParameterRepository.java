package com.bsp.procedure_gateway.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.entity.ProcedureParameter;
import com.bsp.procedure_gateway.enums.ActiveStatus;

@Repository
public interface ProcedureParameterRepository
        extends JpaRepository<ProcedureParameter, Long> {

    /*
     * Parameter List
     */
    List<ProcedureParameter> findByProcedureMasterOrderByParameterOrderAsc(
            ProcedureMaster procedureMaster);

    /*
     * Parameter List By Procedure Id
     */
    List<ProcedureParameter> findByProcedureMaster_ProcedureIdOrderByParameterOrderAsc(
            Long procedureId);

    /*
     * Active Parameter List
     */
    List<ProcedureParameter> findByProcedureMaster_ProcedureIdAndActiveOrderByParameterOrderAsc(
            Long procedureId,
            ActiveStatus active);

    /*
     * Search with Pagination
     */
    Page<ProcedureParameter> findByProcedureMaster_ProcedureId(
            Long procedureId,
            Pageable pageable);

    /*
     * Duplicate Parameter Name
     */
    boolean existsByProcedureMaster_ProcedureIdAndParameterNameIgnoreCase(
            Long procedureId,
            String parameterName);

    /*
     * Duplicate Parameter Name During Update
     */
    boolean existsByProcedureMaster_ProcedureIdAndParameterNameIgnoreCaseAndParameterIdNot(
            Long procedureId,
            String parameterName,
            Long parameterId);

    /*
     * Duplicate Parameter Order
     */
    boolean existsByProcedureMaster_ProcedureIdAndParameterOrder(
            Long procedureId,
            Integer parameterOrder);

    /*
     * Duplicate Parameter Order During Update
     */
    boolean existsByProcedureMaster_ProcedureIdAndParameterOrderAndParameterIdNot(
            Long procedureId,
            Integer parameterOrder,
            Long parameterId);

    /*
     * Latest Parameter Id
     */
    @Query("""
            SELECT NVL(MAX(p.parameterId),0)
            FROM ProcedureParameter p
            """)
    Long findMaxParameterId();

}
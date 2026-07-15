package com.bsp.procedure_gateway.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bsp.procedure_gateway.entity.ClientProcedureMapping;
import com.bsp.procedure_gateway.enums.ActiveStatus;

public interface ClientProcedureMappingRepository
        extends JpaRepository<ClientProcedureMapping, Integer> {

    /**
     * Fetch all mappings for a client.
     */
    List<ClientProcedureMapping> findByClient_ClientId(
            Integer clientId);
    
    @Query("""
    	       SELECT cpm
    	       FROM ClientProcedureMapping cpm
    	       JOIN FETCH cpm.procedureMaster
    	       WHERE cpm.client.clientId = :clientId
    	       """)
    	List<ClientProcedureMapping> findMappingsByClientId(
    	        @Param("clientId")
    	        Integer clientId);

    /**
     * Fetch mapping for Client + Procedure.
     */
    Optional<ClientProcedureMapping>
            findByClient_ClientIdAndProcedureMaster_ProcedureId(

                    Integer clientId,

                    Long procedureId);

    /**
     * Generate Mapping Id.
     */
    @Query("""
            SELECT COALESCE(MAX(c.mappingId),0)
            FROM ClientProcedureMapping c
            """)
    Integer findMaxMappingId();
    
    
    @Query("""
            SELECT cpm
            FROM ClientProcedureMapping cpm
            JOIN cpm.client c
            JOIN cpm.procedureMaster p
            WHERE c.clientUuid = :clientUuid
              AND p.procedureUuid = :procedureUuid
            """)
        Optional<ClientProcedureMapping> findActiveStatusByClientUuidAndProcedureUuid(
                @Param("clientUuid") String clientUuid,
                @Param("procedureUuid") String procedureUuid);
    
    
    
    
}
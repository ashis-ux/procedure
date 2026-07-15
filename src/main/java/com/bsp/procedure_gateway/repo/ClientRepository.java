package com.bsp.procedure_gateway.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.Client;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
	
	Optional<Client> findByClientId(Integer clientId);

	Optional<Client> findByClientUuid(String clientUuid);

	boolean existsByClientNameIgnoreCase(String clientName);
	
    Optional<Client> findByClientNameIgnoreCase(String clientName);
    
    @Query("""
    	       SELECT COALESCE(MAX(c.clientId),0)
    	       FROM Client c
    	       """)
    	Integer findMaxClientId();

   
    boolean existsByClientUuid(String clientUuid);
    
    boolean existsByClientNameIgnoreCaseAndClientIdNot(
            String clientName,
            Integer clientId);
    
    Page<Client> findByClientNameContainingIgnoreCase(
            String clientName,
            Pageable pageable);
    
    Page<Client> findByActive(
            ActiveStatus active,
            Pageable pageable);

    Page<Client> findByClientNameContainingIgnoreCaseAndActive(
            String clientName,
            ActiveStatus active,
            Pageable pageable);
 
    Optional<Client> findByClientUuidAndActive(
            String clientUuid,
            ActiveStatus active);

}
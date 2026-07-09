package com.bsp.procedure_gateway.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.DatabaseMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;

@Repository
public interface DatabaseRepository extends
        JpaRepository<DatabaseMaster, Long>,
        JpaSpecificationExecutor<DatabaseMaster> {

    boolean existsByDatabaseId(Long databaseId);
    
    boolean existsByDatabaseNameIgnoreCase(
            String databaseName);
    
    boolean existsByDatabaseNameIgnoreCaseAndDatabaseIdNot(
            String databaseName,
            Long databaseId);
    
    List<DatabaseMaster> findByActiveOrderByDatabaseNameAsc(
            ActiveStatus active);

}
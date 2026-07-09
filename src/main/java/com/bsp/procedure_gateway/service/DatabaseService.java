package com.bsp.procedure_gateway.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.procedure_gateway.dto.DatabaseDropdownResponse;
import com.bsp.procedure_gateway.dto.DatabaseMasterRequest;
import com.bsp.procedure_gateway.dto.DatabaseMasterResponse;
import com.bsp.procedure_gateway.enums.ActiveStatus;

public interface DatabaseService {

    DatabaseMasterResponse createDatabase(
            DatabaseMasterRequest request);

    Page<DatabaseMasterResponse> searchDatabase(

            String searchText,

            ActiveStatus status,

            int page,

            int size);
    DatabaseMasterResponse updateDatabase(
            Long databaseId,
            DatabaseMasterRequest request);
    
    public DatabaseMasterResponse getDatabaseById(
	        Long databaseId);
    
    List<DatabaseDropdownResponse> getActiveDatabases();

}

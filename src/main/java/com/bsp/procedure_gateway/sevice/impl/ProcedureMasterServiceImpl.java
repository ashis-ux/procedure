package com.bsp.procedure_gateway.sevice.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.procedure_gateway.dto.ProcedureMasterRequest;
import com.bsp.procedure_gateway.dto.ProcedureMasterResponse;
import com.bsp.procedure_gateway.entity.DatabaseMaster;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.ProcedureType;
import com.bsp.procedure_gateway.exception.BadRequestException;
import com.bsp.procedure_gateway.exception.ResourceAlreadyExistsException;
import com.bsp.procedure_gateway.exception.ResourceNotFoundException;
import com.bsp.procedure_gateway.repo.DatabaseRepository;
import com.bsp.procedure_gateway.repo.ProcedureMasterRepository;
import com.bsp.procedure_gateway.service.ProcedureMasterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcedureMasterServiceImpl implements ProcedureMasterService{
	
	private final DatabaseRepository databaseRepository;
	
	private final ProcedureMasterRepository procedureMasterRepository;
	
	@Override
	@Transactional
	public ProcedureMasterResponse createProcedure(
	        ProcedureMasterRequest request) {

	    log.info("Creating new Procedure.");

	    /*
	     * Validate Request
	     */
	    

	    if (request == null) {

	        log.error("Procedure Request is null.");

	        throw new BadRequestException(
	                "Request cannot be null.");

	    }

	    validateProcedureRequest(request);

	    /*
	     * Validate Database
	     */

	    DatabaseMaster database =
	            databaseRepository.findById(
	                    request.getDatabaseId())

	            .orElseThrow(() -> {

	                log.error(
	                        "Database not found. Id : {}",
	                        request.getDatabaseId());

	                return new ResourceNotFoundException(
	                        "Database not found.");

	            });

	    /*
	     * Check Duplicate Procedure
	     */

	    if (procedureMasterRepository
	            .existsByProcedureNameIgnoreCaseAndDatabaseMaster_DatabaseId(

	                    request.getProcedureName(),

	                    request.getDatabaseId())) {

	        log.error(
	                "Procedure already exists : {}",
	                request.getProcedureName());

	        throw new ResourceAlreadyExistsException(

	                "Procedure already exists in selected database.");

	    }

	    /*
	     * Prepare Entity
	     */

	    ProcedureMaster procedure =
	            new ProcedureMaster();

	    procedure.setProcedureUuid(
	            UUID.randomUUID().toString());

	    procedure.setDatabaseMaster(database);

	    procedure.setSchemaName(
	            request.getSchemaName().trim());

	    procedure.setPackageName(
	            request.getPackageName());

	    procedure.setProcedureName(
	            request.getProcedureName().trim());

	    procedure.setDescription(
	            request.getDescription());

	    procedure.setProcedureType(
	            request.getProcedureType());

	    /*
	     * Always POST
	     */

	    procedure.setHttpMethod("POST");

	    procedure.setTimeoutSeconds(
	            request.getTimeoutSeconds());

	    procedure.setActive(
	            request.getActive());

	    /*
	     * Save
	     */
	    
	    Long nextId = procedureMasterRepository
	            .findTopByOrderByProcedureIdDesc()
	            .map(p -> p.getProcedureId() + 1)
	            .orElse(1L);
	    

	    procedure.setProcedureId(nextId);
	    
	    procedure.setCreatedBy("SYSTEM");  // add this
	    procedure.setCreatedDate(LocalDateTime.now());

	    procedure.setUpdatedBy("SYSTEM");  // add this
	    procedure.setUpdatedDate(LocalDateTime.now());


	    ProcedureMaster savedProcedure =
	    		procedureMasterRepository.save(procedure);

	    log.info(
	            "Procedure created successfully. Id : {}",
	            savedProcedure.getProcedureId());

	    return convertToResponse(savedProcedure);

	}
	
	private void validateProcedureRequest(
	        ProcedureMasterRequest request) {

	    validateDatabase(request.getDatabaseId());

	    validateSchemaName(request.getSchemaName());

	    validateProcedureName(request.getProcedureName());

	    validateProcedureType(request.getProcedureType());

	    validateTimeout(request.getTimeoutSeconds());

	}
	
	private void validateDatabase(Long databaseId) {

	    if (databaseId == null) {

	        log.error("Database Id is mandatory.");

	        throw new BadRequestException(
	                "Database is mandatory.");

	    }

	}
	
	private void validateSchemaName(
	        String schemaName) {

	    if (schemaName == null ||
	            schemaName.trim().isEmpty()) {

	        log.error("Schema Name is mandatory.");

	        throw new BadRequestException(
	                "Schema Name is mandatory.");

	    }

	    if (schemaName.length() > 100) {

	        throw new BadRequestException(
	                "Schema Name cannot exceed 100 characters.");

	    }

	}
	
	private void validateProcedureName(
	        String procedureName) {

	    if (procedureName == null ||
	            procedureName.trim().isEmpty()) {

	        log.error("Procedure Name is mandatory.");

	        throw new BadRequestException(
	                "Procedure Name is mandatory.");

	    }

	    if (procedureName.length() > 100) {

	        throw new BadRequestException(
	                "Procedure Name cannot exceed 100 characters.");

	    }

	}
	
	private void validateProcedureType(
	        ProcedureType procedureType) {

	    if (procedureType == null) {

	        log.error("Procedure Type is mandatory.");

	        throw new BadRequestException(
	                "Procedure Type is mandatory.");

	    }

	}
	
	private void validateTimeout(
	        Integer timeout) {

	    if (timeout == null) {

	        throw new BadRequestException(
	                "Timeout is mandatory.");

	    }

	    if (timeout <= 0) {

	        throw new BadRequestException(
	                "Timeout must be greater than zero.");

	    }

	    if (timeout > 99999) {

	        throw new BadRequestException(
	                "Invalid Timeout.");

	    }

	}
	
	private ProcedureMasterResponse convertToResponse(
	        ProcedureMaster entity) {

	    return ProcedureMasterResponse

	            .builder()

	            .procedureId(
	                    entity.getProcedureId())

	            .procedureUuid(
	                    entity.getProcedureUuid())

	            .databaseId(
	                    entity.getDatabaseMaster()
	                            .getDatabaseId())

	            .databaseName(
	                    entity.getDatabaseMaster()
	                            .getDatabaseName())

	            .schemaName(
	                    entity.getSchemaName())

	            .packageName(
	                    entity.getPackageName())

	            .procedureName(
	                    entity.getProcedureName())

	            .description(
	                    entity.getDescription())

	            .procedureType(
	                    entity.getProcedureType())

	            .httpMethod(
	                    entity.getHttpMethod())

	            .timeoutSeconds(
	                    entity.getTimeoutSeconds())

	            .active(
	                    entity.getActive())

	            .build();

	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<ProcedureMasterResponse> searchProcedures(

	        String searchText,

	        Long databaseId,

	        ActiveStatus status,

	        int page,

	        int size) {

	    log.info("Searching Procedures.");

	    Pageable pageable =
	            PageRequest.of(

	                    page,

	                    size,

	                    Sort.by("procedureId")
	                            .descending());

	    Specification<ProcedureMaster> specification =
	            Specification.where(null);

	    /*
	     * Search Text
	     */

	    if (searchText != null &&
	            !searchText.trim().isEmpty()) {

	        String search =
	                "%" +
	                searchText.trim().toLowerCase() +
	                "%";

	        specification =
	                specification.and((root, query, cb) ->

	                        cb.or(

	                                cb.like(

	                                        cb.lower(
	                                                root.get("procedureName")),

	                                        search),

	                                cb.like(

	                                        cb.lower(
	                                                root.get("schemaName")),

	                                        search),

	                                cb.like(

	                                        cb.lower(
	                                                root.get("packageName")),

	                                        search)

	                        ));

	    }

	    /*
	     * Database Filter
	     */

	    if (databaseId != null) {

	        specification =
	                specification.and((root, query, cb) ->

	                        cb.equal(

	                                root.get("databaseMaster")
	                                        .get("databaseId"),

	                                databaseId));

	    }

	    /*
	     * Status
	     */

	    if (status != null) {

	        specification =
	                specification.and((root, query, cb) ->

	                        cb.equal(

	                                root.get("active"),

	                                status));

	    }

	    Page<ProcedureMaster> procedures =
	            procedureMasterRepository.findAll(

	                    specification,

	                    pageable);

	    log.info(
	            "Total Procedures Found : {}",
	            procedures.getTotalElements());

	    return procedures.map(this::convertToResponse);

	}
	
	@Override
	@Transactional(readOnly = true)
	public ProcedureMasterResponse getProcedure(
	        Long procedureId) {

	    log.info(
	            "Fetching Procedure. Id : {}",
	            procedureId);

	    ProcedureMaster procedure =

	            procedureMasterRepository

	                    .findById(procedureId)

	                    .orElseThrow(() -> {

	                        log.error(
	                                "Procedure not found : {}",
	                                procedureId);

	                        return new ResourceNotFoundException(

	                                "Procedure not found.");

	                    });

	    return convertToResponse(procedure);

	}
	
	@Override
	@Transactional
	public ProcedureMasterResponse updateProcedure(

	        Long procedureId,

	        ProcedureMasterRequest request) {

	    log.info(
	            "Updating Procedure. Id : {}",
	            procedureId);

	    validateProcedureRequest(request);

	    ProcedureMaster procedure =

	            procedureMasterRepository

	                    .findById(procedureId)

	                    .orElseThrow(() ->

	                            new ResourceNotFoundException(

	                                    "Procedure not found."));

	    DatabaseMaster database =

	            databaseRepository

	                    .findById(request.getDatabaseId())

	                    .orElseThrow(() ->

	                            new ResourceNotFoundException(

	                                    "Database not found."));

	    /*
	     * Duplicate Check
	     */

	    if (procedureMasterRepository

	            .existsByProcedureNameIgnoreCaseAndDatabaseMaster_DatabaseIdAndProcedureIdNot(

	                    request.getProcedureName(),

	                    request.getDatabaseId(),

	                    procedureId)) {

	        throw new ResourceAlreadyExistsException(

	                "Procedure already exists in selected database.");

	    }

	    /*
	     * Update Entity
	     */

	    procedure.setDatabaseMaster(database);

	    procedure.setSchemaName(

	            request.getSchemaName().trim());

	    procedure.setPackageName(

	            request.getPackageName());

	    procedure.setProcedureName(

	            request.getProcedureName().trim());

	    procedure.setDescription(

	            request.getDescription());

	    procedure.setProcedureType(

	            request.getProcedureType());

	    procedure.setTimeoutSeconds(

	            request.getTimeoutSeconds());

	    procedure.setActive(

	            request.getActive());

	    /*
	     * HTTP Method always POST
	     */

	    procedure.setHttpMethod("POST");

	    ProcedureMaster updated =

	            procedureMasterRepository.save(procedure);

	    log.info(
	            "Procedure updated successfully. Id : {}",
	            updated.getProcedureId());

	    return convertToResponse(updated);

	}

}

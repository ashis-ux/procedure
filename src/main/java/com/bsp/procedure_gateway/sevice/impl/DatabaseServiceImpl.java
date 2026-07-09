package com.bsp.procedure_gateway.sevice.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.procedure_gateway.dto.DatabaseDropdownResponse;
import com.bsp.procedure_gateway.dto.DatabaseMasterRequest;
import com.bsp.procedure_gateway.dto.DatabaseMasterResponse;
import com.bsp.procedure_gateway.entity.DatabaseMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.DatabaseType;
import com.bsp.procedure_gateway.exception.BadRequestException;
import com.bsp.procedure_gateway.exception.ResourceAlreadyExistsException;
import com.bsp.procedure_gateway.exception.ResourceNotFoundException;
import com.bsp.procedure_gateway.repo.DatabaseRepository;
import com.bsp.procedure_gateway.service.DatabaseService;
import com.bsp.procedure_gateway.util.PasswordEncryptor;

import jakarta.persistence.criteria.Predicate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DatabaseServiceImpl implements DatabaseService {

	private final DatabaseRepository repository;
	
	private final PasswordEncryptor tokenUtil;

	@Override
	@Transactional
	public DatabaseMasterResponse createDatabase(DatabaseMasterRequest request) {

		log.info("Received request to create database : {}", request.getDatabaseName());

		/*
		 * Mandatory Validation
		 */

		if (request == null) {

			log.error("Database request is null.");

			throw new BadRequestException("Request cannot be null.");

		}

		if (request.getDatabaseName() == null || request.getDatabaseName().trim().isEmpty()) {

			throw new BadRequestException("Database Name is mandatory.");

		}

		if (request.getHost() == null || request.getHost().trim().isEmpty()) {

			throw new BadRequestException("Host is mandatory.");

		}

		if (request.getPort() == null) {

			throw new BadRequestException("Port is mandatory.");

		}

		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {

			throw new BadRequestException("Username is mandatory.");

		}

		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {

			throw new BadRequestException("Password is mandatory.");

		}

		/*
		 * Additional Validation
		 */

		validateDatabaseName(request.getDatabaseName());

		validateHost(request.getHost());

		validatePort(request.getPort());

		validateServiceNameOrSid(request.getServiceName(), request.getSid());

		/*
		 * Duplicate Validation
		 */

		if (repository.existsByDatabaseNameIgnoreCase(request.getDatabaseName().trim())) {

			log.warn("Database already exists : {}", request.getDatabaseName());

			throw new ResourceAlreadyExistsException(

					"Database Name already exists.");

		}

		/*
		 * Entity Mapping
		 */

		DatabaseMaster entity = new DatabaseMaster();

		entity.setDatabaseName(request.getDatabaseName().trim());

		/*
		 * Currently only Oracle is supported.
		 */

		entity.setDatabaseType(DatabaseType.ORACLE);

		entity.setHost(request.getHost().trim());

		entity.setPort(request.getPort());

		entity.setServiceName(request.getServiceName());

		entity.setSid(request.getSid());

		entity.setUsername(request.getUsername().trim());

		/*
		 * Encrypt password (Will implement in Part-2)
		 */

		entity.setPassword(
		        encryptPassword(request.getPassword())
		);

		entity.setActive(request.getActive());

		/*
		 * Save
		 */
		entity.setCreatedBy("system_user");
		DatabaseMaster saved = repository.save(entity);

		log.info("Database created successfully. Generated Id : {}", saved.getDatabaseId());

		return convertToResponse(saved);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<DatabaseMasterResponse> searchDatabase(String searchText, ActiveStatus status, int page, int size) {

		log.info("Searching databases. SearchText: {}, Status: {}, Page: {}, Size: {}", searchText, status, page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "databaseId"));

		Specification<DatabaseMaster> specification = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			/*
			 * Search by Database ID or Database Name
			 */
			if (searchText != null && !searchText.trim().isEmpty()) {

				String keyword = searchText.trim();

				List<Predicate> searchPredicates = new ArrayList<>();

				/*
				 * Database ID Search
				 */
				if (keyword.matches("\\d+")) {

					searchPredicates.add(cb.equal(root.get("databaseId"), Long.parseLong(keyword)));
				}

				/*
				 * Database Name Search
				 */
				searchPredicates.add(

						cb.like(

								cb.upper(root.get("databaseName")),

								"%" + keyword.toUpperCase() + "%"));

				predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
			}

			/*
			 * Status Filter
			 */
			if (status != null) {

				predicates.add(

						cb.equal(

								root.get("active"),

								status));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};

		Page<DatabaseMaster> databasePage = repository.findAll(specification, pageable);

		log.info("Total Records Found : {}", databasePage.getTotalElements());

		return databasePage.map(this::convertToResponse);
	}

	private DatabaseMasterResponse convertToResponse(
	        DatabaseMaster entity) {

	    return DatabaseMasterResponse.builder()

	            .databaseId(entity.getDatabaseId())

	            .databaseName(entity.getDatabaseName())

	            .databaseType(entity.getDatabaseType())

	            .host(entity.getHost())

	            .port(entity.getPort())

	            .serviceName(entity.getServiceName())

	            .sid(entity.getSid())

	            .username(entity.getUsername())

	            .password(entity.getPassword())

	            .active(entity.getActive())

	            .build();

	}

	/*
	 * ========================================================= DATABASE NAME
	 * VALIDATION =========================================================
	 */
	private void validateDatabaseName(String databaseName) {

		if (databaseName.length() > 100) {

			log.error("Database Name exceeds maximum length.");

			throw new BadRequestException("Database Name cannot exceed 100 characters.");

		}

	}

	/*
	 * ========================================================= HOST VALIDATION
	 * =========================================================
	 */
	private void validateHost(String host) {

		String ipRegex = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)" + "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$";

		if (!host.matches(ipRegex)) {

			log.error("Invalid Host IP Address : {}", host);

			throw new BadRequestException("Invalid Host IP Address.");

		}

	}

	/*
	 * ========================================================= PORT VALIDATION
	 * =========================================================
	 */
	private void validatePort(Integer port) {

		if (port < 1 || port > 65535) {

			log.error("Invalid Port : {}", port);

			throw new BadRequestException("Port must be between 1 and 65535.");

		}

	}

	/*
	 * ========================================================= SERVICE NAME / SID
	 * VALIDATION =========================================================
	 */
	private void validateServiceNameOrSid(String serviceName, String sid) {

		boolean serviceNameBlank = serviceName == null || serviceName.trim().isEmpty();

		boolean sidBlank = sid == null || sid.trim().isEmpty();

		if (serviceNameBlank && sidBlank) {

			log.error("Both Service Name and SID are empty.");

			throw new BadRequestException("Either Service Name or SID is mandatory.");

		}

	}

	/*
	 * ========================================================= PASSWORD ENCRYPTION
	 * =========================================================
	 */
	private String encryptPassword(String password) {

	    try {
	        return tokenUtil.encode(password);
	    }
	    catch (Exception ex) {
	        log.error("Unable to encrypt password.", ex);
	        throw new RuntimeException("Unable to encrypt password.");
	    }
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public DatabaseMasterResponse getDatabaseById(
	        Long databaseId) {

	    log.info("Fetching database details for ID : {}", databaseId);

	    if (databaseId == null) {

	        log.error("Database Id is null.");

	        throw new BadRequestException(
	                "Database Id is mandatory.");

	    }

	    DatabaseMaster database = repository
	            .findById(databaseId)
	            .orElseThrow(() -> {

	                log.error("Database not found for ID : {}", databaseId);

	                return new ResourceNotFoundException(
	                        "Database not found with ID : " + databaseId);

	            });

	    log.info("Database found successfully : {}",
	            database.getDatabaseName());

	    return convertToResponse(database);

	}
	
	
	@Override
	@Transactional
	public DatabaseMasterResponse updateDatabase(
	        Long databaseId,
	        DatabaseMasterRequest request) {

	    log.info("Updating Database : {}", databaseId);

	    if (databaseId == null) {

	        throw new BadRequestException(
	                "Database Id is mandatory.");

	    }

	    if (request == null) {

	        throw new BadRequestException(
	                "Request cannot be null.");

	    }

	    DatabaseMaster database =
	    		repository.findById(databaseId)

	            .orElseThrow(() -> {

	                log.error("Database not found : {}",
	                        databaseId);

	                return new ResourceNotFoundException(
	                        "Database not found.");

	            });

	    /*
	     * Validation
	     */

	    validateDatabaseName(
	            request.getDatabaseName());

	    validateHost(
	            request.getHost());

	    validatePort(
	            request.getPort());

	    validateServiceNameOrSid(

	            request.getServiceName(),

	            request.getSid());

	    /*
	     * Duplicate Name Validation
	     */

	    if (repository
	            .existsByDatabaseNameIgnoreCaseAndDatabaseIdNot(

	                    request.getDatabaseName(),

	                    databaseId)) {

	        log.warn("Database Name already exists : {}",
	                request.getDatabaseName());

	        throw new ResourceAlreadyExistsException(
	                "Database Name already exists.");

	    }
	    
	    database.setDatabaseName(
	            request.getDatabaseName().trim());

	    database.setDatabaseType(
	            DatabaseType.ORACLE);

	    database.setHost(
	            request.getHost().trim());

	    database.setPort(
	            request.getPort());

	    database.setServiceName(
	            request.getServiceName());

	    database.setSid(
	            request.getSid());

	    database.setUsername(
	            request.getUsername().trim());

	    database.setActive(
	            request.getActive());
	    
	    /*
	     * Encrypt Password
	     */

	    database.setPassword(
	            encryptPassword(request.getPassword()));

	    /*
	     * Save
	     */

	    DatabaseMaster updatedDatabase =
	    		repository.save(database);

	    log.info(
	            "Database updated successfully. Database Id : {}",
	            updatedDatabase.getDatabaseId());

	    return convertToResponse(updatedDatabase);

	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<DatabaseDropdownResponse> getActiveDatabases() {

	    log.info("Fetching active databases.");

	    return repository
	            .findByActiveOrderByDatabaseNameAsc(
	                    ActiveStatus.Y)
	            .stream()
	            .map(database ->
	                    DatabaseDropdownResponse.builder()
	                            .databaseId(database.getDatabaseId())
	                            .databaseName(database.getDatabaseName())
	                            .build())
	            .toList();

	}
	 
}
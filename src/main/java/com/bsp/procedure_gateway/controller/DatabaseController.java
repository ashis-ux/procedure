package com.bsp.procedure_gateway.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.procedure_gateway.dto.DatabaseMasterRequest;
import com.bsp.procedure_gateway.dto.DatabaseMasterResponse;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.service.DatabaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import com.bsp.procedure_gateway.dto.DatabaseDropdownResponse;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
@Slf4j
public class DatabaseController {

    private final DatabaseService databaseService;

    /**
     * Create Database
     */
    @PostMapping
    public ResponseEntity<DatabaseMasterResponse> createDatabase(
            @Valid @RequestBody DatabaseMasterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseService.createDatabase(request));
    }

    /**
     * Search Databases
     */
    @GetMapping("/search")
    public ResponseEntity<Page<DatabaseMasterResponse>> searchDatabase(

            @RequestParam(required = false) String searchText,

            @RequestParam(required = false) ActiveStatus status,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                databaseService.searchDatabase(
                        searchText,
                        status,
                        page,
                        size));

    }
    
    /**
     * Get Database By Id
     */
    @GetMapping("/{databaseId}")
    public ResponseEntity<DatabaseMasterResponse> getDatabaseById(
            @PathVariable Long databaseId) {

        log.info("Received request to fetch database : {}", databaseId);

        return ResponseEntity.ok(
                databaseService.getDatabaseById(databaseId));

    }
    
    /**
     * Update Database
     */
    @PutMapping("/{databaseId}")
    public ResponseEntity<DatabaseMasterResponse> updateDatabase(
            @PathVariable Long databaseId,
            @Valid @RequestBody DatabaseMasterRequest request) {

        log.info("Received request to update database : {}", databaseId);

        return ResponseEntity.ok(
                databaseService.updateDatabase(
                        databaseId,
                        request));

    }
    
    /**
     * Database Dropdown
     */
    @GetMapping("/dropdown")
    public ResponseEntity<List<DatabaseDropdownResponse>>
            getDatabaseDropdown() {

        log.info("Fetching active databases.");

        return ResponseEntity.ok(
                databaseService.getActiveDatabases());

    }

}

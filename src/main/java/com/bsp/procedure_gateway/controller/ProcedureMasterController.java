package com.bsp.procedure_gateway.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsp.procedure_gateway.dto.ProcedureMasterRequest;
import com.bsp.procedure_gateway.dto.ProcedureMasterResponse;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.service.ProcedureMasterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/procedure")
@PreAuthorize("hasAuthority('APP_PROCEDUREAPIGATEWAY')")
public class ProcedureMasterController {

    private final ProcedureMasterService procedureService;

    /**
     * Create Procedure
     */
    @PostMapping
    public ResponseEntity<ProcedureMasterResponse> createProcedure(

            @Valid
            @RequestBody
            ProcedureMasterRequest request) {

        log.info("Received request to create Procedure.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(procedureService.createProcedure(request));

    }
    
    /**
     * Search Procedures
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProcedureMasterResponse>> searchProcedures(

            @RequestParam(required = false)
            String searchText,

            @RequestParam(required = false)
            Long databaseId,

            @RequestParam(required = false)
            ActiveStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        log.info(
                "Searching Procedures. SearchText : {}, Database : {}, Status : {}, Page : {}, Size : {}",
                searchText,
                databaseId,
                status,
                page,
                size);

        return ResponseEntity.ok(

                procedureService.searchProcedures(

                        searchText,

                        databaseId,

                        status,

                        page,

                        size));

    }
    
    /**
     * Get Procedure By Id
     */
    @GetMapping("/{procedureId}")
    public ResponseEntity<ProcedureMasterResponse> getProcedure(

            @PathVariable
            Long procedureId) {

        log.info("Fetching Procedure. Id : {}", procedureId);

        return ResponseEntity.ok(

                procedureService.getProcedure(procedureId));

    }


    /**
     * Update Procedure
     */
    @PutMapping("/{procedureId}")
    public ResponseEntity<ProcedureMasterResponse> updateProcedure(

            @PathVariable
            Long procedureId,

            @Valid
            @RequestBody
            ProcedureMasterRequest request) {

        log.info("Updating Procedure. Id : {}", procedureId);

        return ResponseEntity.ok(

                procedureService.updateProcedure(

                        procedureId,

                        request));

    }
    
    

}
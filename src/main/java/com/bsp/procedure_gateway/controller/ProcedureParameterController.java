package com.bsp.procedure_gateway.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.procedure_gateway.dto.ProcedureParameterRequest;
import com.bsp.procedure_gateway.dto.ProcedureParameterResponse;
import com.bsp.procedure_gateway.service.ProcedureParameterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/procedure")
@RequiredArgsConstructor
@Slf4j
public class ProcedureParameterController {

    private final ProcedureParameterService procedureParameterService;

    /**
     * Get Parameters By Procedure
     */
    @GetMapping("/{procedureId}/parameters")
    public ResponseEntity<Page<ProcedureParameterResponse>> getParameters(

            @PathVariable
            Long procedureId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        log.info("Fetching Parameters. Procedure Id : {}", procedureId);

        return ResponseEntity.ok(

                procedureParameterService.getParameters(

                        procedureId,

                        page,

                        size));

    }

    /**
     * Create Parameter
     */
    @PostMapping("/{procedureId}/parameters")
    public ResponseEntity<ProcedureParameterResponse> createParameter(

            @PathVariable
            Long procedureId,

            @Valid
            @RequestBody
            ProcedureParameterRequest request) {

        log.info("Creating Parameter. Procedure Id : {}", procedureId);

        return ResponseEntity.ok(

                procedureParameterService.createParameter(

                        procedureId,

                        request));

    }

    /**
     * Get Parameter
     */
    @GetMapping("/parameter/{parameterId}")
    public ResponseEntity<ProcedureParameterResponse> getParameter(

            @PathVariable
            Long parameterId) {

        log.info("Fetching Parameter : {}", parameterId);

        return ResponseEntity.ok(

                procedureParameterService.getParameter(parameterId));

    }

    /**
     * Update Parameter
     */
    @PutMapping("/parameter/{parameterId}")
    public ResponseEntity<ProcedureParameterResponse> updateParameter(

            @PathVariable
            Long parameterId,

            @Valid
            @RequestBody
            ProcedureParameterRequest request) {

        log.info("Updating Parameter : {}", parameterId);

        return ResponseEntity.ok(

                procedureParameterService.updateParameter(

                        parameterId,

                        request));

    }

    /**
     * Delete Parameter
     */
    @DeleteMapping("/parameter/{parameterId}")
    public ResponseEntity<Void> deleteParameter(

            @PathVariable
            Long parameterId) {

        log.info("Deleting Parameter : {}", parameterId);

        procedureParameterService.deleteParameter(parameterId);

        return ResponseEntity.noContent().build();

    }

}
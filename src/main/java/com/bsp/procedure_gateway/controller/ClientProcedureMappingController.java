package com.bsp.procedure_gateway.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bsp.procedure_gateway.dto.ClientProcedureMappingRequest;
import com.bsp.procedure_gateway.dto.ClientProcedureMappingResponse;
import com.bsp.procedure_gateway.dto.ClientProcedureRequest;
import com.bsp.procedure_gateway.service.ClientProcedureMappingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/client-procedure-mappings")
@RequiredArgsConstructor
@Slf4j
public class ClientProcedureMappingController {

    private final ClientProcedureMappingService
            clientProcedureMappingService;

    /**
     * Fetch all procedures for a client.
     */
    
    
    @GetMapping("/{clientId}")
    public ResponseEntity<List<ClientProcedureMappingResponse>>
            getClientProcedures(

                    @PathVariable
                    Integer clientId) {

        log.info(
                "Fetching procedure mapping for Client Id : {}",
                clientId);

        List<ClientProcedureMappingResponse> response =
                clientProcedureMappingService
                        .getClientProcedures(clientId);

        return ResponseEntity.ok(response);
    }

    /**
     * Save client procedure mapping.
     */
    @PostMapping("/{clientId}")
    public ResponseEntity<Void> saveMappings(

            @PathVariable
            Integer clientId,

            @RequestBody
            @Valid
            ClientProcedureMappingRequest request) {

        log.info(
                "Saving procedure mapping for Client Id : {}",
                request.toString());
        for (ClientProcedureRequest id : request.getProcedures()) {
        
        log.info(
                "Saving procedure mapping for Client Id : {}",
                id.getProcedureId(),id.getActive().toString());
        log.info(
                "Saving procedure mapping for Client Id : {}", id.getActive().toString());
        }

        clientProcedureMappingService
                .saveMappings(clientId, request);

        log.info(
                "Procedure mapping saved successfully for Client Id : {}",
                clientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
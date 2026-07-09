package com.bsp.procedure_gateway.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bsp.procedure_gateway.dto.ProcedureRequest;
import com.bsp.procedure_gateway.dto.ProcedureResponse;
import com.bsp.procedure_gateway.service.ProcedureService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/procedures")
@RequiredArgsConstructor
public class ProcedureController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProcedureController.class);

    private final ProcedureService procedureService;
    
  

//    @PostMapping
//    public ResponseEntity<ProcedureResponse> executeProcedure(
//            @RequestBody @Valid ProcedureRequest request) {
//
//        LOGGER.info("Received procedure execution request.");
//
//        String uuid = tokenUil.decrypt(request.getToken());
//
//        validateUuid(uuid);
//
//        LOGGER.info("Executing procedure for UUID: {}", uuid);
//
//        ProcedureResponse response = procedureService.execute(uuid, request);
//
//        LOGGER.info("Procedure executed successfully for UUID: {}", uuid);
//
//        return ResponseEntity.ok(response);
//    }
    
    @PostMapping("/{procedureName}")
    public ResponseEntity<ProcedureResponse> executeProcedure(
            @PathVariable String procedureName,
            @RequestBody @Valid ProcedureRequest request,
            HttpServletRequest httpRequest) {

        LOGGER.info("Received request for procedure: {}", procedureName);

//        String uuid = tokenUil.decrypt(request.getToken());
//
//        validateUuid(uuid);

        ProcedureResponse response =
                procedureService.execute(procedureName, request,httpRequest);

        return ResponseEntity.ok(response);
    }
    

    /**
     * Validate UUID Format
     */
    private void validateUuid(String uuid) {

        try {
            UUID.fromString(uuid);
        }
        catch (IllegalArgumentException exception) {

            LOGGER.error(
                    "Invalid UUID : {}",
                    uuid);

            throw new IllegalArgumentException(
                    "Invalid UUID Format");

        }

    }

}
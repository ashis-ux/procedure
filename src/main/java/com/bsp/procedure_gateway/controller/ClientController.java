package com.bsp.procedure_gateway.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsp.procedure_gateway.dto.ClientRequest;
import com.bsp.procedure_gateway.dto.ClientResponse;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('APP_PROCEDUREAPIGATEWAY')")
public class ClientController {

    private final ClientService clientService;

    /**
     * Create Client
     */
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(

            @RequestBody
            @Valid
            ClientRequest request) {

        log.info("Received request to create client.");

        ClientResponse response =
                clientService.createClient(request);

        log.info("Client created successfully.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Update Client
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(

            @PathVariable
            Integer clientId,

            @RequestBody
            @Valid
            ClientRequest request) {

        log.info(
                "Received request to update client : {}",
                clientId);

        ClientResponse response =
                clientService.updateClient(
                        clientId,
                        request);

        log.info(
                "Client updated successfully : {}",
                clientId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get Client
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(

            @PathVariable
            Integer clientId) {

        log.info(
                "Received request to fetch client : {}",
                clientId);

        ClientResponse response =
                clientService.getClient(clientId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get Clients
     */
    @GetMapping
    public ResponseEntity<Page<ClientResponse>> getClients(

            @RequestParam(required = false)
            String clientName,

            @RequestParam(required = false)
            ActiveStatus status,

            @RequestParam(defaultValue = "0")
            Integer page,

            @RequestParam(defaultValue = "10")
            Integer size) {

        return ResponseEntity.ok(
                clientService.getClients(
                        clientName,
                        status,
                        page,
                        size));
    }

    /**
     * Regenerate UUID
     */
    @PutMapping("/{clientId}/regenerate-uuid")
    public ResponseEntity<ClientResponse> regenerateUuid(

            @PathVariable
            Integer clientId) {

        log.info(
                "Received request to regenerate UUID for client : {}",
                clientId);

        ClientResponse response =
                clientService.regenerateClientUuid(
                        clientId);

        log.info(
                "Client UUID regenerated successfully : {}",
                clientId);

        return ResponseEntity.ok(response);
    }

}
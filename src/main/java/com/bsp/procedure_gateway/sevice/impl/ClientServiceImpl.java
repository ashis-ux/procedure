package com.bsp.procedure_gateway.sevice.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.procedure_gateway.dto.ClientRequest;
import com.bsp.procedure_gateway.dto.ClientResponse;
import com.bsp.procedure_gateway.entity.Client;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.exception.BadRequestException;
import com.bsp.procedure_gateway.exception.ProcedureGatewayException;
import com.bsp.procedure_gateway.exception.ResourceAlreadyExistsException;
import com.bsp.procedure_gateway.exception.ResourceNotFoundException;
import com.bsp.procedure_gateway.repo.ClientRepository;
import com.bsp.procedure_gateway.service.ClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientServiceImpl implements ClientService {

    

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public ClientResponse createClient(
            ClientRequest request) {

        log.info("Creating Client : {}", request.getClientName());

        /*
         * Validate Request
         */
        if (request == null) {

            log.error("Client Request is null.");

            throw new BadRequestException(
                    "Request cannot be null.");
        }

        validateRequest(request);

        /*
         * Duplicate Client Name
         */
        if (clientRepository.existsByClientNameIgnoreCase(
                request.getClientName().trim())) {

            log.error(
                    "Client already exists : {}",
                    request.getClientName());

            throw new ResourceAlreadyExistsException(
                    "Client Name already exists.");
        }

        /*
         * Create Entity
         */
        Client client = new Client();

        client.setClientId(
                clientRepository.findMaxClientId() + 1);

        client.setClientUuid(
                UUID.randomUUID().toString());

        client.setClientName(
                request.getClientName()
                        .trim()
                        .toUpperCase());

        client.setClientDescription(
                request.getClientDescription());

        client.setActive(
                request.getActive());

         

        /*
         * Save
         */
        Client savedClient =
                clientRepository.save(client);

        log.info(
                "Client created successfully. Id : {}",
                savedClient.getClientId());

        return convertToResponse(savedClient);
    }
    
    private void validateRequest(
            ClientRequest request) {

        if (request.getClientName() == null ||
                request.getClientName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Client Name is mandatory.");
        }

        if (request.getClientName().length() > 100) {

            throw new BadRequestException(
                    "Client Name cannot exceed 100 characters.");
        }

        if (request.getClientDescription() != null &&
                request.getClientDescription().contains(" ")) {

            throw new BadRequestException(
                    "Client Description cannot contain spaces.");
        }

        if (request.getActive() == null) {

            throw new BadRequestException(
                    "Status is mandatory.");
        }
    }
    
    private ClientResponse convertToResponse(
            Client entity) {

        return ClientResponse.builder()
        		.clientId(entity.getClientId())

                .clientUuid(entity.getClientUuid())

                .clientName(entity.getClientName())

                .clientDescription(entity.getClientDescription())

                .active(entity.getActive())

                .build();
    }
    
    @Override
    @Transactional
    public ClientResponse updateClient(

            Integer clientId,

            ClientRequest request) {

        log.info("Updating Client. Id : {}", clientId);

        /*
         * Validate Request
         */
        validateRequest(request);

        /*
         * Validate Client
         */
        Client client =
                clientRepository.findById(clientId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Client not found. Id : {}",
                                    clientId);

                            return new ResourceNotFoundException(
                                    "Client not found.");

                        });

        /*
         * Duplicate Client Name
         */
        if (clientRepository
                .existsByClientNameIgnoreCaseAndClientIdNot(

                        request.getClientName().trim(),

                        clientId)) {

            log.error(
                    "Client Name already exists : {}",
                    request.getClientName());

            throw new ResourceAlreadyExistsException(
                    "Client Name already exists.");
        }

        /*
         * Update Entity
         */
        client.setClientName(
                request.getClientName()
                        .trim()
                        .toUpperCase());

        client.setClientDescription(
                request.getClientDescription());

        client.setActive(
                request.getActive());

         client.setClientUuid(request.getClientUuid());

        /*
         * Save
         */
        Client updatedClient =
                clientRepository.save(client);

        log.info(
                "Client updated successfully. Id : {}",
                updatedClient.getClientId());

        return convertToResponse(updatedClient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClient(
            Integer clientId) {

        log.info("Fetching Client. Id : {}", clientId);

        Client client =
                clientRepository.findById(clientId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Client not found. Id : {}",
                                    clientId);

                            return new ResourceNotFoundException(
                                    "Client not found.");

                        });

        return convertToResponse(client);

    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> getClients(

            String clientName,
            
            ActiveStatus status,


            int page,

            int size) {

    	Pageable pageable = PageRequest.of(
    	        page,
    	        size,
    	        Sort.by("clientName"));

    	Page<Client> clients;

    	boolean hasName =
    	        clientName != null &&
    	        !clientName.trim().isEmpty();

    	if (hasName && status != null) {

    	    clients = clientRepository
    	            .findByClientNameContainingIgnoreCaseAndActive(
    	                    clientName.trim(),
    	                    status,
    	                    pageable);

    	}
    	else if (hasName) {

    	    clients = clientRepository
    	            .findByClientNameContainingIgnoreCase(
    	                    clientName.trim(),
    	                    pageable);

    	}
    	else if (status != null) {

    	    clients = clientRepository
    	            .findByActive(
    	                    status,
    	                    pageable);

    	}
    	else {

    	    clients = clientRepository.findAll(pageable);

    	}

    	return clients.map(this::convertToResponse);

    }
    
    @Override
    @Transactional(readOnly = true)
    public ClientResponse regenerateClientUuid(
            Integer clientId) {

        log.info(
                "Regenerating Client UUID. Client Id : {}",
                clientId);

        Client client =
                clientRepository.findById(clientId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Client not found. Id : {}",
                                    clientId);

                            return new ResourceNotFoundException(
                                    "Client not found.");

                        });

        client.setClientUuid(
                UUID.randomUUID().toString());


        Client updatedClient =client;
                

        log.info(
                "Client UUID regenerated successfully. Client Id : {}",
                updatedClient.getClientId());

        return convertToResponse(updatedClient);

    }
}
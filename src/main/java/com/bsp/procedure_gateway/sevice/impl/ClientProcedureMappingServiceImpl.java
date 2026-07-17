package com.bsp.procedure_gateway.sevice.impl;

import java.time.LocalDateTime;
 
import java.util.List;
import java.util.Map;
 
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.procedure_gateway.dto.ClientProcedureMappingRequest;
import com.bsp.procedure_gateway.dto.ClientProcedureRequest;
import com.bsp.procedure_gateway.dto.ClientProcedureMappingResponse;
import com.bsp.procedure_gateway.entity.Client;
import com.bsp.procedure_gateway.entity.ClientProcedureMapping;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.exception.ResourceNotFoundException;
import com.bsp.procedure_gateway.repo.ClientProcedureMappingRepository;
import com.bsp.procedure_gateway.repo.ClientRepository;
import com.bsp.procedure_gateway.repo.ProcedureMasterRepository;
import com.bsp.procedure_gateway.service.ClientProcedureMappingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientProcedureMappingServiceImpl
        implements ClientProcedureMappingService {

    private final ClientRepository clientRepository;

    private final ProcedureMasterRepository procedureMasterRepository;

    private final ClientProcedureMappingRepository
            clientProcedureMappingRepository;

    /**
     * Fetch all procedures for selected client.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClientProcedureMappingResponse> getClientProcedures(
            Integer clientId) {

        log.info(
                "Fetching Procedure Mapping for Client Id : {}",
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

        List<ProcedureMaster> procedures =
                procedureMasterRepository.findAll();

        log.info(
                "Total Procedures Found : {}",
                procedures.size());

        return procedures.stream()

                .map(procedure ->

                        convertToResponse(
                                client,
                                procedure))

                .toList();

    }

    /**
     * Convert Entity into Response.
     */
    private ClientProcedureMappingResponse convertToResponse(

            Client client,

            ProcedureMaster procedure) {

    	ClientProcedureMapping mapping =
    	        clientProcedureMappingRepository
    	                .findByClient_ClientIdAndProcedureMaster_ProcedureId(
    	                        client.getClientId(),
    	                        procedure.getProcedureId())
    	                .orElse(null);

    	boolean assigned = false;

    	ActiveStatus status = ActiveStatus.N;

    	if (mapping != null) {

    	    status = mapping.getActive();

    	    assigned = status == ActiveStatus.Y;

    	}

        return ClientProcedureMappingResponse.builder()

                .procedureId(
                        procedure.getProcedureId())

                .procedureUuid(
                        procedure.getProcedureUuid())

                .schemaName(
                        procedure.getSchemaName())

                .packageName(
                        procedure.getPackageName())

                .procedureName(
                        procedure.getProcedureName())

                .httpMethod(
                        procedure.getHttpMethod())

                .assigned(
                        assigned)

                .active(
                        status)

                .build();

    }
    /**
     * Save Client Procedure Mapping.
     */
    @Override
    @Transactional
    public void saveMappings(
            Integer clientId,
            ClientProcedureMappingRequest request) {

        log.info("Saving Procedure Mapping. Client Id : {}", clientId);

        Client client =
                clientRepository.findById(clientId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Client not found."));

        List<ClientProcedureMapping> existingMappings =
                clientProcedureMappingRepository
                        .findMappingsByClientId(clientId);

        Map<Long, ClientProcedureMapping> mappingMap =
                existingMappings.stream()
                        .collect(Collectors.toMap(
                                m -> m.getProcedureMaster().getProcedureId(),
                                Function.identity()
                        ));

        Integer mappingId =
                clientProcedureMappingRepository.findMaxMappingId();
        
        log.info("latest mapping id: "+mappingId);

        for (ClientProcedureRequest procedureRequest : request.getProcedures()) {

            Long procedureId = procedureRequest.getProcedureId();

            ActiveStatus status = procedureRequest.getActive();

            ClientProcedureMapping mapping =
                    mappingMap.get(procedureId);

            if (mapping != null) {

                mapping.setActive(status);

                log.info(
                        "Updated Mapping : Client {}, Procedure {}, Status {}",
                        clientId,
                        procedureId,
                        status);
                
                clientProcedureMappingRepository.save(mapping);

                continue;
            }

            /*
             * Don't create inactive mapping.
             */
            if (status == ActiveStatus.N) {
                continue;
            }

            ProcedureMaster procedure =
                    procedureMasterRepository
                            .findById(procedureId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Procedure not found."));

            ClientProcedureMapping newMapping =
                    new ClientProcedureMapping();

            newMapping.setMappingId(++mappingId);

            newMapping.setClient(client);

            newMapping.setProcedureMaster(procedure);

            newMapping.setActive(ActiveStatus.Y);

            existingMappings.add(newMapping);

            mappingMap.put(procedureId, newMapping);

            log.info(
                    "Created Mapping : Client {}, Procedure {}",
                    clientId,
                    procedureId);

        }

        clientProcedureMappingRepository.saveAll(existingMappings);

        log.info(
                "Procedure Mapping updated successfully. Client Id : {}",
                clientId);
    }
}
    
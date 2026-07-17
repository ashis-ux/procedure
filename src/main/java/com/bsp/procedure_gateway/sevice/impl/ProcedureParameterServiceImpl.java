package com.bsp.procedure_gateway.sevice.impl;

 

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bsp.procedure_gateway.dto.ProcedureParameterRequest;
import com.bsp.procedure_gateway.dto.ProcedureParameterResponse;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.entity.ProcedureParameter;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.exception.BadRequestException;
import com.bsp.procedure_gateway.exception.ResourceAlreadyExistsException;
import com.bsp.procedure_gateway.exception.ResourceNotFoundException;
import com.bsp.procedure_gateway.repo.ProcedureMasterRepository;
import com.bsp.procedure_gateway.repo.ProcedureParameterRepository;
import com.bsp.procedure_gateway.service.ProcedureParameterService;

 
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcedureParameterServiceImpl
        implements ProcedureParameterService {

    private final ProcedureParameterRepository procedureParameterRepository;
    
    private final ProcedureMasterRepository procedureMasterRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProcedureParameterResponse> getParameters(

            Long procedureId,

            int page,

            int size) {

        log.info("Fetching Parameters. Procedure Id : {}", procedureId);

        /*
         * Validate Procedure
         */
        ProcedureMaster procedure =
                procedureMasterRepository.findById(procedureId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Procedure not found. Id : {}",
                                    procedureId);

                            return new ResourceNotFoundException(
                                    "Procedure not found.");

                        });

        Pageable pageable =

                PageRequest.of(

                        page,

                        size,

                        Sort.by("parameterOrder").ascending()
                                .ascending());

        Page<ProcedureParameter> parameters =

                procedureParameterRepository
                        .findByProcedureMaster_ProcedureId(

                                procedure.getProcedureId(),

                                pageable);

        log.info(
                "Total Parameters Found : {}",
                parameters.getTotalElements());

        return parameters.map(this::convertToResponse);

    }
    
    
    @Override
    @Transactional
    public ProcedureParameterResponse createParameter(

            Long procedureId,

            ProcedureParameterRequest request) {

        log.info("Creating Parameter for Procedure Id : {}", procedureId);

        /*
         * Validate Request
         */

        if (request == null) {

            log.error("Procedure Parameter Request is null.");

            throw new BadRequestException(
                    "Request cannot be null.");

        }

        validateRequest(request);

        /*
         * Validate Procedure
         */

        ProcedureMaster procedure =
                procedureMasterRepository.findById(procedureId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Procedure not found : {}",
                                    procedureId);

                            return new ResourceNotFoundException(
                                    "Procedure not found.");

                        });

        /*
         * Duplicate Parameter Name
         */

        if (procedureParameterRepository
                .existsByProcedureMaster_ProcedureIdAndParameterNameIgnoreCase(

                        procedureId,

                        request.getParameterName().trim())) {

            log.error(
                    "Parameter already exists : {}",
                    request.getParameterName());

            throw new ResourceAlreadyExistsException(

                    "Parameter Name already exists.");

        }

        /*
         * Duplicate Sequence Number
         */

        if (procedureParameterRepository
                .existsByProcedureMaster_ProcedureIdAndParameterOrder(
                        procedureId,
                        request.getParameterOrder())) {

            throw new ResourceAlreadyExistsException(
                    "Parameter Order already exists.");

        }

        /*
         * Create Entity
         */

        ProcedureParameter parameter =
                new ProcedureParameter();

        parameter.setProcedureMaster(procedure);

        parameter.setParameterName(
                request.getParameterName().trim().toUpperCase());

        parameter.setDataType(
                request.getDataType());

        parameter.setParameterMode(
                request.getParameterMode());

        

        parameter.setDefaultValue(
                request.getDefaultValue());

        

        parameter.setActive(
                request.getActive());
        
        parameter.setParameterOrder(
                request.getParameterOrder());

        parameter.setRequired(
                request.getRequired());

        /*
         * Save
         */
        
        parameter.setParameterId(
                procedureParameterRepository.findMaxParameterId() + 1

        );

        ProcedureParameter savedParameter =
                procedureParameterRepository.save(parameter);

        log.info(
                "Parameter created successfully. Id : {}",
                savedParameter.getParameterId());

        return convertToResponse(savedParameter);

    }
    private ProcedureParameterResponse convertToResponse(
            ProcedureParameter entity) {

    	return ProcedureParameterResponse.builder()

    	        .parameterId(entity.getParameterId())

    	        .procedureId(entity.getProcedureMaster().getProcedureId())

    	        .parameterOrder(entity.getParameterOrder())

    	        .parameterName(entity.getParameterName())

    	        .dataType(entity.getDataType().toString())

    	        .parameterMode(entity.getParameterMode())

    	        .required(entity.getRequired())

    	        .defaultValue(entity.getDefaultValue())

    	        .active(entity.getActive())

    	        .build();

    }
    
    private void validateRequest(
            ProcedureParameterRequest request) {

        if (request.getParameterName() == null ||
                request.getParameterName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Parameter Name is mandatory.");

        }

        if (request.getDataType() == null) {

            throw new BadRequestException(
                    "Data Type is mandatory.");

        }

        if (request.getParameterMode() == null) {

            throw new BadRequestException(
                    "Parameter Mode is mandatory.");

        }

        if (request.getParameterOrder() == null ||
                request.getParameterOrder() <= 0) {

            throw new BadRequestException(
                    "Parameter Order must be greater than zero.");

        }

        if (request.getRequired() == null ||
                request.getRequired().trim().isEmpty()) {

            throw new BadRequestException(
                    "Required flag is mandatory.");

        }

        if (request.getActive() == null) {

            throw new BadRequestException(
                    "Status is mandatory.");

        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProcedureParameterResponse getParameter(
            Long parameterId) {

        log.info("Fetching Parameter. Id : {}", parameterId);

        ProcedureParameter parameter =
                procedureParameterRepository.findById(parameterId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Parameter not found. Id : {}",
                                    parameterId);

                            return new ResourceNotFoundException(
                                    "Parameter not found.");

                        });

        return convertToResponse(parameter);

    }
    
    @Override
    @Transactional
    public ProcedureParameterResponse updateParameter(

            Long parameterId,

            ProcedureParameterRequest request) {

        log.info("Updating Parameter. Id : {}", parameterId);

        validateRequest(request);

        ProcedureParameter parameter =
                procedureParameterRepository.findById(parameterId)

                        .orElseThrow(() -> {

                            log.error(
                                    "Parameter not found. Id : {}",
                                    parameterId);

                            return new ResourceNotFoundException(
                                    "Parameter not found.");

                        });

        Long procedureId =
                parameter.getProcedureMaster().getProcedureId();

        /*
         * Duplicate Parameter Name
         */
        if (procedureParameterRepository
                .existsByProcedureMaster_ProcedureIdAndParameterNameIgnoreCaseAndParameterIdNot(

                        procedureId,

                        request.getParameterName(),

                        parameterId)) {

            throw new ResourceAlreadyExistsException(
                    "Parameter Name already exists.");

        }

        /*
         * Duplicate Parameter Order
         */
        if (procedureParameterRepository
                .existsByProcedureMaster_ProcedureIdAndParameterOrderAndParameterIdNot(

                        procedureId,

                        request.getParameterOrder(),

                        parameterId)) {

            throw new ResourceAlreadyExistsException(
                    "Parameter Order already exists.");

        }

        /*
         * Update Entity
         */
        parameter.setParameterName(
                request.getParameterName()
                        .trim()
                        .toUpperCase());

        parameter.setDataType(
                request.getDataType());

        parameter.setParameterMode(
                request.getParameterMode());

        parameter.setParameterOrder(
                request.getParameterOrder());

        parameter.setRequired(
                request.getRequired());

        parameter.setDefaultValue(
                request.getDefaultValue());

        parameter.setActive(
                request.getActive());

        ProcedureParameter updated =
                procedureParameterRepository.save(parameter);

        log.info(
                "Parameter updated successfully. Id : {}",
                updated.getParameterId());

        return convertToResponse(updated);

    }
    
    
    @Override
    @Transactional
    public void deleteParameter(Long parameterId) {

        log.info("Deleting Parameter. Id : {}", parameterId);

        ProcedureParameter parameter =
                procedureParameterRepository.findById(parameterId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Parameter not found."));

        procedureParameterRepository.delete(parameter);

        log.info(
                "Parameter deleted successfully. Id : {}",
                parameterId);
    }
    
    


	 


	 
}
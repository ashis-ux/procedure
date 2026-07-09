package com.bsp.procedure_gateway.sevice.impl;

 

 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.exception.InvalidProcedureException;
import com.bsp.procedure_gateway.repo.ProcedureMasterRepository;
import com.bsp.procedure_gateway.service.ProcedureMetadataService;

@Service
@RequiredArgsConstructor
public class ProcedureMetadataServiceImpl
        implements ProcedureMetadataService {

    private final ProcedureMasterRepository repository;

    @Override
    public ProcedureMaster getProcedure(
            String procedureUuid) {

        return repository
                .findByProcedureUuidAndActive(
                        procedureUuid,
                        ActiveStatus.Y
                )
                .orElseThrow(() ->
                        new InvalidProcedureException(
                                procedureUuid
                        ));

    }

}
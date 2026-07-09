package com.bsp.procedure_gateway.sevice.impl;
 

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bsp.procedure_gateway.datasource.DataSourceManager;
import com.bsp.procedure_gateway.dto.ProcedureRequest;
import com.bsp.procedure_gateway.dto.ProcedureResponse;
import com.bsp.procedure_gateway.entity.ApiRequestLog;
import com.bsp.procedure_gateway.entity.DatabaseMaster;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.ExecutionStatus;
import com.bsp.procedure_gateway.exception.ResourceNotFoundException;
import com.bsp.procedure_gateway.executor.ProcedureExecutor;
import com.bsp.procedure_gateway.repo.ProcedureMasterRepository;
import com.bsp.procedure_gateway.service.ProcedureMetadataService;
import com.bsp.procedure_gateway.service.ProcedureService;
import com.bsp.procedure_gateway.service.RequestLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcedureServiceImpl implements ProcedureService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProcedureServiceImpl.class);

    private final ProcedureMetadataService procedureMetadataService;

    private final DataSourceManager dataSourceManager;

    private final ProcedureExecutor procedureExecutor;

    private final RequestLogService requestLogService;
    
    private final ProcedureMasterRepository repository;

    private final ObjectMapper objectMapper;
    
    
    @Override
    public ProcedureResponse execute(
            String procdurename,
            ProcedureRequest request,HttpServletRequest httpRequest) {

        LOGGER.info("Starting Procedure Execution");
        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        ApiRequestLog requestLog = createRequestLog(
        		requestId,
                request.getToken(),
                request);
        
        repository
        .findByProcedureNameAndActive(procdurename, ActiveStatus.Y)
        .orElseThrow(() -> new ResourceNotFoundException(
                "Procedure '" + procdurename + "' does not exist or is inactive."));

        try {

            LOGGER.info("Fetching Procedure Metadata");

            ProcedureMaster procedure =
                    procedureMetadataService.getProcedure(request.getToken());
            
            if(procedure.getDatabaseMaster().getActive()==ActiveStatus.N)throw new ResourceNotFoundException(
                    "database '" + procedure.getDatabaseMaster().getDatabaseName() + "' does not exist or is inactive.");

            LOGGER.info("Procedure Found : {}",
                    procedure.getProcedureName());

            DatabaseMaster database =
                    procedure.getDatabaseMaster();

            LOGGER.info("Getting DataSource : {}",
                    database.getDatabaseName());

            DataSource dataSource =
                    dataSourceManager.getDataSource(database);

            LOGGER.info("Executing Procedure");

            ProcedureResponse response =
                    procedureExecutor.execute(
                            procedure,
                            request,
                            dataSource);

            requestLog.setStatus(
                    ExecutionStatus.SUCCESS);

            String responseJson = null;
            response.setRequestId(requestId);
            if (response != null) {
                try {
                    responseJson = objectMapper.writeValueAsString(response);
                } catch (Exception e) {
                    LOGGER.warn("Failed to serialize response", e);
                    responseJson = response.toString();
                }
            }

            requestLog.setResponseJson(responseJson);

            requestLog.setResponseTime(
                    LocalDateTime.now());
            requestLog.setDatabaseName(procedure.getDatabaseMaster().getDatabaseName());
            requestLog.setProcedureName(procdurename);
            String appName = httpRequest.getHeader("X-Application-Name");
            if(appName==null)appName="UNKNOWN";
            requestLog.setApplicationName(appName);
           
            requestLog.setApplicationName(
                    httpRequest.getHeader("X-Application-Name"));

            requestLog.setClientIp(getClientIp(httpRequest));

            requestLog.setRequestMethod(httpRequest.getMethod());

            requestLog.setRequestHeaders(getHeaders(httpRequest));

            long end = System.currentTimeMillis();
            requestLog.setExecutionTimeMs(end - start);    

            LOGGER.info("Procedure Executed Successfully");
          
            return response;

        }
        catch (Exception exception) {

            LOGGER.error(
                    "Procedure Execution Failed",
                    exception);

            requestLog.setStatus(
                    ExecutionStatus.FAILED);

            requestLog.setErrorMessage(
                    exception.getMessage());

            requestLog.setResponseTime(
                    LocalDateTime.now());

            requestLog.setHttpStatus(500);  
            requestLog.setResponseTime(LocalDateTime.now());

            requestLogService.save(requestLog);

            throw exception;

        }

    }
//    
//  

    /**
     * Creates Request Log
     */
    private ApiRequestLog createRequestLog(
    		String requestId,
            String procedureUuid,
            ProcedureRequest request) {

        ApiRequestLog requestLog =
                new ApiRequestLog();

        requestLog.setRequestId(requestId);

        requestLog.setProcedureUuid(procedureUuid);

        String requestJson = null;

        if (request != null) {
            try {
                requestJson = objectMapper.writeValueAsString(request);
            } catch (Exception e) {
                requestJson = request.toString();
            }
        }

        requestLog.setRequestJson(requestJson);

        requestLog.setRequestTime(
                LocalDateTime.now());

        requestLog.setStatus(
                ExecutionStatus.IN_PROGRESS);

        ApiRequestLog log= requestLogService.save(requestLog);

        LOGGER.info("Request Log Created : {}",
        		log.getRequestId());

        return requestLog;

    }
    
    private String getClientIp(HttpServletRequest request) {

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0].trim();
    }
    
    private String getHeaders(HttpServletRequest request) {

        StringBuilder headers = new StringBuilder();

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.append(name)
                   .append(": ")
                   .append(request.getHeader(name))
                   .append("\n");
        }

        return headers.toString();
    }

}
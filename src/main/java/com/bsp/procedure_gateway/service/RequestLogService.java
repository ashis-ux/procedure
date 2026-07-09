package com.bsp.procedure_gateway.service;

import com.bsp.procedure_gateway.entity.ApiRequestLog;

public interface RequestLogService {

    ApiRequestLog save(ApiRequestLog log);

    ApiRequestLog update(ApiRequestLog log);
    
    

}
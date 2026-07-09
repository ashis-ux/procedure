package com.bsp.procedure_gateway.sevice.impl;

import org.springframework.stereotype.Service;

import com.bsp.procedure_gateway.entity.ApiRequestLog;
import com.bsp.procedure_gateway.repo.ApiRequestLogRepository;
import com.bsp.procedure_gateway.service.RequestLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestLogServiceImpl
        implements RequestLogService {

    private final ApiRequestLogRepository repository;

    @Override
    public ApiRequestLog save(ApiRequestLog log) {

        return repository.save(log);

    }

    @Override
    public ApiRequestLog update(ApiRequestLog log) {

        return repository.save(log);

    }

}

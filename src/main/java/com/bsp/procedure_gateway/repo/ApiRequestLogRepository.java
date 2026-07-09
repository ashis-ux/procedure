package com.bsp.procedure_gateway.repo;

 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsp.procedure_gateway.entity.ApiRequestLog;

import java.util.Optional;

@Repository
public interface ApiRequestLogRepository
        extends JpaRepository<ApiRequestLog, Long> {

    Optional<ApiRequestLog> findByRequestId(String requestId);

}

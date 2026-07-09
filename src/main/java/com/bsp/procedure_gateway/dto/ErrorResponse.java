package com.bsp.procedure_gateway.dto;
 

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorResponse {

    private String requestId;

    private String errorCode;

    private String errorMessage;

    private LocalDateTime timestamp;

}

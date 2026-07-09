package com.bsp.procedure_gateway.dto;
 

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse1 {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

}
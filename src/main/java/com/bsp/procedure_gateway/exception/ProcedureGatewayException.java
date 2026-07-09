package com.bsp.procedure_gateway.exception;

 

import lombok.Getter;

@Getter
public class ProcedureGatewayException extends RuntimeException {

    private final String errorCode;

    public ProcedureGatewayException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProcedureGatewayException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

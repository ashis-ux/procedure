package com.bsp.procedure_gateway.exception;

 

public class ValidationException
        extends ProcedureGatewayException {

    public ValidationException(String message) {

        super(
                "REQ_001",
                message
        );

    }

}

package com.bsp.procedure_gateway.exception;

 

public class ProcedureExecutionException
        extends ProcedureGatewayException {

    public ProcedureExecutionException(String message) {

        super(
                "PROC_500",
                message
        );

    }

}

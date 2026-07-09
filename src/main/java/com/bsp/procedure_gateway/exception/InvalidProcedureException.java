package com.bsp.procedure_gateway.exception;
 

public class InvalidProcedureException
        extends ProcedureGatewayException {

    public InvalidProcedureException(String uuid) {

        super(
                "PROC_404",
                "Invalid Procedure UUID : " + uuid
        );

    }

}

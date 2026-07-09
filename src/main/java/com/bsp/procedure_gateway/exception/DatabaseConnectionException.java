package com.bsp.procedure_gateway.exception;
 

public class DatabaseConnectionException
        extends ProcedureGatewayException {

    public DatabaseConnectionException(String database) {

        super(
                "DB_001",
                "Unable to connect to database : " + database
        );

    }

}

package com.bsp.procedure_gateway.executor;


import org.springframework.stereotype.Component;

import java.sql.Types;

@Component
public class OracleTypeMapper {

    public int getSqlType(String dataType) {

        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }

        return switch (dataType.toUpperCase()) {

            case "VARCHAR2", "VARCHAR", "CHAR" ->
                    Types.VARCHAR;

            case "NUMBER", "INTEGER", "INT" ->
                    Types.NUMERIC;

            case "DATE" ->
                    Types.DATE;

            case "TIMESTAMP" ->
                    Types.TIMESTAMP;

            case "CLOB" ->
                    Types.CLOB;

            case "BLOB" ->
                    Types.BLOB;

            case "REF_CURSOR", "SYS_REFCURSOR" ->
                    -10;        // OracleTypes.CURSOR

            default ->
                    Types.VARCHAR;
        };

    }

}

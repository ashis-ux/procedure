package com.bsp.procedure_gateway.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.dialect.OracleTypes;
import org.springframework.stereotype.Component;

import com.bsp.procedure_gateway.dto.ProcedureRequest;
import com.bsp.procedure_gateway.dto.ProcedureResponse;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.entity.ProcedureParameter;
import com.bsp.procedure_gateway.enums.DataType;
import com.bsp.procedure_gateway.enums.ParameterMode;
import com.bsp.procedure_gateway.exception.ProcedureExecutionException;
import com.bsp.procedure_gateway.exception.ValidationException;

import javax.sql.DataSource;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallableStatementExecutor implements ProcedureExecutor {

    private final OracleTypeMapper oracleTypeMapper;

    @Override
    public ProcedureResponse execute(
            ProcedureMaster procedure,
            ProcedureRequest request,
            DataSource dataSource) {

        String sql = buildCallSql(procedure);

        Connection connection = null;

        long startTime = 0;

        try {

            connection = dataSource.getConnection();

            connection.setAutoCommit(false);

            try (CallableStatement callableStatement =
                         connection.prepareCall(sql)) {

                log.info("Executing procedure : {}",
                        procedure.getProcedureName());


                registerParameters(
                        callableStatement,
                        procedure.getParameters()
                );


                setInputParameters(
                        callableStatement,
                        procedure.getParameters(),
                        request
                );


                Integer timeout =
                        procedure.getTimeoutSeconds();


                if (timeout != null && timeout > 0) {

                    callableStatement.setQueryTimeout(timeout);

                    log.info(
                        "Procedure timeout configured : {} seconds",
                        timeout
                    );
                }


                startTime = System.currentTimeMillis();


                executeWithTimeout(
                        callableStatement,
                        connection,
                        timeout
                );


                long executionTime =
                        System.currentTimeMillis() - startTime;


                log.info(
                    "Procedure execution time : {} ms",
                    executionTime
                );


                /*
                 * Manual timeout validation.
                 * This handles cases where JDBC timeout is ignored
                 */
                if (timeout != null
                        && timeout > 0
                        && executionTime > timeout * 1000L) {


                    log.error(
                        "Procedure exceeded timeout. Rolling back. Execution time : {} ms",
                        executionTime
                    );


                    connection.rollback();


                    throw new ProcedureExecutionException(
                            "Procedure execution timed out after "
                            + timeout
                            + " seconds."
                    );
                }


                Object response =
                        extractResponse(
                                callableStatement,
                                procedure
                        );


                connection.commit();


                log.info(
                    "Procedure executed successfully : {}",
                    procedure.getProcedureName()
                );


                return ProcedureResponse.builder()
                        .success(true)
                        .message(
                            "Procedure executed successfully."
                        )
                        .data(response)
                        .build();
            }


        } catch (ValidationException ex) {


            log.warn(
                "Validation failed : {}",
                ex.getMessage()
            );


            rollback(connection);


            throw ex;


        } catch (SQLException ex) {


            log.error(
                "Database error while executing procedure {}",
                procedure.getProcedureName(),
                ex
            );


            rollback(connection);


            /*
             * Oracle user cancel / timeout
             */
            if (ex.getErrorCode() == 1013
                    || (ex.getMessage() != null
                    && ex.getMessage()
                    .contains("ORA-01013"))) {


                throw new ProcedureExecutionException(
                        "Procedure execution timed out after "
                        + procedure.getTimeoutSeconds()
                        + " seconds."
                );
            }


            throw new ProcedureExecutionException(
                    ex.getMessage()
            );


        } catch (ProcedureExecutionException ex) {


            rollback(connection);

            throw ex;


        } catch (Exception ex) {


            log.error(
                "Unexpected error while executing procedure {}",
                procedure.getProcedureName(),
                ex
            );


            rollback(connection);


            throw new ProcedureExecutionException(
                    "An unexpected error occurred while processing the request."
            );


        } finally {


            if (connection != null) {

                try {

                    connection.close();

                    log.debug(
                        "Database connection closed"
                    );

                } catch (SQLException ex) {

                    log.error(
                        "Failed to close database connection",
                        ex
                    );
                }
            }
        }
    }
    
    private void rollback(Connection connection) {

        if (connection != null) {

            try {

                if (!connection.getAutoCommit()) {

                    connection.rollback();

                    log.info(
                        "Transaction rolled back successfully"
                    );
                }

            } catch (SQLException ex) {

                log.error(
                    "Rollback failed",
                    ex
                );
            }
        }
    }
    // =========================
    // SQL BUILDER
    // =========================
    private String buildCallSql(ProcedureMaster procedure) {

        StringBuilder sql = new StringBuilder("{call ");

        if (procedure.getSchemaName() != null && !procedure.getSchemaName().isBlank()) {
            sql.append(procedure.getSchemaName()).append(".");
        }

        if (procedure.getPackageName() != null && !procedure.getPackageName().isBlank()) {
            sql.append(procedure.getPackageName()).append(".");
        }

        sql.append(procedure.getProcedureName());

        sql.append("(");

        int size = procedure.getParameters().size();

        for (int i = 0; i < size; i++) {
            sql.append("?");
            if (i < size - 1) {
                sql.append(",");
            }
        }

        sql.append(")}");

        return sql.toString();
    }

    // =========================
    // REGISTER PARAMETERS
    // =========================
    private void registerParameters(
            CallableStatement cs,
            List<ProcedureParameter> params
    ) throws SQLException {


        log.info("Registering procedure parameters");


        int index = 1;


        for (ProcedureParameter p : params) {


            switch (p.getParameterMode()) {


                case REF_CURSOR:

                    cs.registerOutParameter(
                            index,
                            OracleTypes.CURSOR
                    );

                    break;


                case OUT:
                case INOUT:

                    cs.registerOutParameter(
                            index,
                            oracleTypeMapper.getSqlType(
                                    p.getDataType()
                                            .toString()
                            )
                    );

                    break;


                case IN:

                    break;
            }


            index++;
        }


        log.info(
            "Parameter registration completed"
        );
    }

    // =========================
    // SET INPUT PARAMETERS
    // =========================
    private void setInputParameters(
            CallableStatement cs,
            List<ProcedureParameter> params,
            ProcedureRequest request) throws SQLException {

        log.info("Setting input parameters");

        Map<String, Object> input = request != null
                ? request.getParameters()
                : Collections.emptyMap();

        int index = 1;

        for (ProcedureParameter p : params) {

            if (p.getParameterMode() == ParameterMode.IN
                    || p.getParameterMode() == ParameterMode.INOUT) {

                Object value = getValue(input, p.getParameterName());

                if (value == null && p.getParameterMode() == ParameterMode.IN) {
                    throw new ValidationException(
                            "Missing required parameter: " + p.getParameterName());
                }

                setOracleParameter(
                        cs,
                        index,
                        p,
                        value
                );
            }
            index++;
        }

        log.info("Input parameters set successfully");
    }

    private void setOracleParameter(
            CallableStatement cs,
            int index,
            ProcedureParameter parameter,
            Object value
    ) throws SQLException {


        if(value == null){

            cs.setNull(
                    index,
                    oracleTypeMapper.getSqlType(
                            parameter.getDataType()
                                    .toString()
                    )
            );

            return;
        }


        DataType type =
                parameter.getDataType();


        switch(type){


            case VARCHAR2:
            case CHAR:

                cs.setString(
                        index,
                        value.toString()
                );

                break;



            case NUMBER:

                cs.setBigDecimal(
                        index,
                        new BigDecimal(
                                value.toString()
                        )
                );

                break;



            case DATE:

                if(value instanceof java.sql.Date){

                    cs.setDate(
                            index,
                            (java.sql.Date)value
                    );

                }
                else {

                    cs.setDate(
                            index,
                            java.sql.Date.valueOf(
                                    value.toString()
                            )
                    );
                }

                break;



            case TIMESTAMP:

                if(value instanceof Timestamp){

                    cs.setTimestamp(
                            index,
                            (Timestamp)value
                    );

                }
                else {

                    cs.setTimestamp(
                            index,
                            Timestamp.valueOf(
                                    value.toString()
                            )
                    );
                }

                break;



            case CLOB:
                cs.setCharacterStream(
                        index,
                        new StringReader(
                                value.toString()
                        )
                );

                break;



            case BLOB:


            case RAW:
            case LONG_RAW:


                if(value instanceof byte[]){

                    cs.setBytes(
                            index,
                            (byte[])value
                    );

                }
                else {

                    cs.setBinaryStream(
                            index,
                            new ByteArrayInputStream(
                                    value.toString()
                                            .getBytes()
                            )
                    );
                }

                break;



            case LONG:


                cs.setCharacterStream(
                        index,
                        new StringReader(
                                value.toString()
                        )
                );

                break;



            case XMLTYPE:


                cs.setString(
                        index,
                        value.toString()
                );

                break;



            default:


                cs.setObject(
                        index,
                        value
                );
        }

    }
    
    private Object getValue(Map<String, Object> map, String key) {
        return map.entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    // =========================
    // EXTRACT RESPONSE
    // =========================
    private Object extractResponse(
            CallableStatement cs,
            ProcedureMaster procedure) throws SQLException {

    	Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> outParams = readOutParameters(cs, procedure.getParameters());

        if (!outParams.isEmpty()) {
            response.put("outParameters", outParams);
        }

        int index = 1;

        for (ProcedureParameter p : procedure.getParameters()) {

            if (p.getParameterMode() == ParameterMode.REF_CURSOR) {

                try (ResultSet rs = (ResultSet) cs.getObject(index)) {

                    response.put(
                            p.getParameterName(),
                            readCursor(rs)
                    );
                }
            }

            index++;
        }

        return response.isEmpty() ? null : response;
    }

    // =========================
    // READ CURSOR
    // =========================
    private List<Map<String, Object>> readCursor(ResultSet rs) throws SQLException {

        List<Map<String, Object>> list = new ArrayList<>();

        if (rs == null) return list;

        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        while (rs.next()) {

            Map<String, Object> row = new LinkedHashMap<>();

            for (int i = 1; i <= cols; i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }

            list.add(row);
        }

        return list;
    }

    // =========================
    // READ OUT PARAMS
    // =========================
    private Map<String, Object> readOutParameters(
            CallableStatement cs,
            List<ProcedureParameter> params) throws SQLException {

        Map<String, Object> map = new HashMap<>();

        int index = 1;

        for (ProcedureParameter p : params) {

            if (p.getParameterMode() == ParameterMode.OUT
                    || p.getParameterMode() == ParameterMode.INOUT) {

                map.put(p.getParameterName(), cs.getObject(index));
            }

            index++;
        }

        return map;
    }
    
    private void executeWithTimeout(
            CallableStatement cs,
            Connection connection,
            Integer timeout
    ) throws Exception {


        ExecutorService executor =
                Executors.newSingleThreadExecutor();


        Future<?> future =
                executor.submit(() -> {

                    try {

                        cs.execute();

                    }
                    catch(SQLException e){

                        throw new RuntimeException(e);
                    }

                });



        try {


            if(timeout != null && timeout > 0){

                future.get(
                        timeout,
                        TimeUnit.SECONDS
                );

            }
            else {

                future.get();
            }


        }
        catch(TimeoutException e){


            log.error(
                "Procedure timeout. Cancelling Oracle statement"
            );


            try {

                cs.cancel();

                log.info(
                    "Oracle statement cancelled"
                );

            }
            catch(SQLException cancelException){

                log.error(
                    "Statement cancellation failed",
                    cancelException
                );
            }


            rollback(connection);


            throw new ProcedureExecutionException(
                    "Procedure execution timed out after "
                    + timeout
                    + " seconds."
            );


        }
        catch(ExecutionException e){


            Throwable cause =
                    e.getCause();


            if(cause instanceof RuntimeException
                    && cause.getCause()
                    instanceof SQLException){


                throw (SQLException)
                        cause.getCause();
            }


            throw e;

        }
        finally {


            executor.shutdownNow();

        }

    }
}
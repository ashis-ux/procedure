package com.bsp.procedure_gateway.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.bsp.procedure_gateway.dto.ProcedureRequest;
import com.bsp.procedure_gateway.dto.ProcedureResponse;
import com.bsp.procedure_gateway.entity.ProcedureMaster;
import com.bsp.procedure_gateway.entity.ProcedureParameter;
import com.bsp.procedure_gateway.enums.ParameterMode;
import com.bsp.procedure_gateway.exception.ProcedureExecutionException;
import com.bsp.procedure_gateway.exception.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

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

        try (Connection connection = dataSource.getConnection();
        	     CallableStatement callableStatement = connection.prepareCall(sql)) {

        	    log.info("Executing procedure : {}", procedure.getProcedureName());

        	    registerParameters(callableStatement, procedure.getParameters());

        	    setInputParameters(
        	            callableStatement,
        	            procedure.getParameters(),
        	            request);

        	    callableStatement.execute();

        	    Object response = extractResponse(callableStatement, procedure);

        	    return ProcedureResponse.builder()
        	            .success(true)
        	            .message("Procedure executed successfully.")
        	            .data(response)
        	            .build();

        	} catch (ValidationException ex) {

        	    log.warn("Validation failed : {}", ex.getMessage());

        	    throw ex;

        	} catch (SQLIntegrityConstraintViolationException ex) {

        	    log.error("Integrity constraint violation while executing procedure {}",
        	            procedure.getProcedureName(), ex);

        	    throw new ProcedureExecutionException(
        	            "Invalid request data.");

        	} catch (SQLTimeoutException ex) {

        	    log.error("Procedure execution timed out : {}",
        	            procedure.getProcedureName(), ex);

        	    throw new ProcedureExecutionException(
        	            "Procedure execution timed out. Please try again.");

        	} catch (SQLRecoverableException ex) {

        	    log.error("Database connection error : {}",
        	            procedure.getProcedureName(), ex);

        	    throw new ProcedureExecutionException(
        	            "Unable to connect to the database. Please try again.");

        	} catch (SQLException ex) {

        	    log.error("Database error while executing procedure {}",
        	            procedure.getProcedureName(), ex);

        	    throw new ProcedureExecutionException(
        	            "Failed to execute the procedure.");

        	} catch (Exception ex) {

        	    log.error("Unexpected error while executing procedure {}",
        	            procedure.getProcedureName(), ex);

        	    throw new ProcedureExecutionException(
        	            "An unexpected error occurred while processing the request.");
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
            List<ProcedureParameter> params) throws SQLException {

        log.info("Registering procedure parameters");

        int index = 1;

        for (ProcedureParameter p : params) {

            switch (p.getParameterMode()) {

                case OUT, INOUT, REF_CURSOR -> cs.registerOutParameter(
                        index,
                        oracleTypeMapper.getSqlType(p.getDataType())
                );

                case IN -> {
                    // nothing
                }
            }

            index++;
        }

        log.info("Parameter registration completed");
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

                cs.setObject(index, value);
            }
            index++;
        }

        log.info("Input parameters set successfully");
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

        Map<String, Object> response = new HashMap<>();

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
}
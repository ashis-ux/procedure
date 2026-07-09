package com.bsp.procedure_gateway.entity;
 
import com.bsp.procedure_gateway.enums.ExecutionStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "API_REQUEST_LOG", indexes = { @Index(name = "IDX_REQUEST_ID", columnList = "REQUEST_ID"),
		@Index(name = "IDX_UUID", columnList = "PROCEDURE_UUID"), @Index(name = "IDX_STATUS", columnList = "STATUS") })
public class ApiRequestLog extends AuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "api_log_gen")
	@TableGenerator(
	    name = "api_log_gen",
	    table = "ID_GENERATOR",
	    pkColumnName = "GEN_NAME",
	    valueColumnName = "GEN_VALUE",
	    pkColumnValue = "API_REQUEST_LOG_ID",
	    allocationSize = 1
	)
	private Long logId;

	@Column(name = "REQUEST_ID")
	private String requestId;

	@Column(name = "PROCEDURE_UUID")
	private String procedureUuid;

	@Column(name = "DATABASE_NAME")
	private String databaseName;

	@Column(name = "PROCEDURE_NAME")
	private String procedureName;

	@Column(name = "APPLICATION_NAME")
	private String applicationName;

	@Column(name = "CLIENT_IP")
	private String clientIp;

	@Column(name = "REQUEST_METHOD")
	private String requestMethod;

	@Lob
	@Column(name = "REQUEST_HEADERS")
	private String requestHeaders;

	@Lob
	@Column(name = "REQUEST_JSON")
	private String requestJson;

	@Lob
	@Column(name = "RESPONSE_JSON")
	private String responseJson;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false, length = 20)
	private ExecutionStatus status;

	@Column(name = "HTTP_STATUS")
	private Integer httpStatus;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Lob
	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;

	@Column(name = "EXECUTION_TIME_MS")
	private Long executionTimeMs;

	@Column(name = "REQUEST_TIME")
	private java.time.LocalDateTime requestTime;

	@Column(name = "RESPONSE_TIME")
	private java.time.LocalDateTime responseTime;
}

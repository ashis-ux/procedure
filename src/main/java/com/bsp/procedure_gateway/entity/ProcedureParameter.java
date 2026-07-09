package com.bsp.procedure_gateway.entity;

 
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.ParameterMode;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "PROCEDURE_PARAMETER",
        indexes = {
                @Index(name = "IDX_PARAMETER_PROC", columnList = "PROCEDURE_ID"),
                @Index(name = "IDX_PARAMETER_ORDER", columnList = "PARAMETER_ORDER")
        }
)
public class ProcedureParameter extends AuditEntity {

    @Id
    @Column(name = "PARAMETER_ID")
    private Long parameterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCEDURE_ID")
    private ProcedureMaster procedureMaster;

    @Column(name = "PARAMETER_NAME", nullable = false)
    private String parameterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "PARAMETER_MODE")
    private ParameterMode parameterMode;

    @Column(name = "DATA_TYPE", nullable = false)
    private String dataType;

    @Column(name = "PARAMETER_ORDER")
    private Integer parameterOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE")
    private ActiveStatus active;

    @Column(name = "REQUIRED", length = 1)
    private String required;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
}
package com.bsp.procedure_gateway.entity;

 
import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.DataType;
import com.bsp.procedure_gateway.enums.ParameterMode;
import com.bsp.procedure_gateway.sevice.impl.AuditListener;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditListener.class)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_TYPE")
    private DataType dataType;

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
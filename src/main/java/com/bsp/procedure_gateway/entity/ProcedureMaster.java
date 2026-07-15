package com.bsp.procedure_gateway.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.ProcedureType;

@Getter
@Setter
@Entity
@Table(
	    name = "PROCEDURE_MASTER",
	    indexes = {
	        @Index(name = "IDX_PROC_DB", columnList = "DATABASE_ID"),
	        @Index(name = "IDX_PROC_NAME", columnList = "PROCEDURE_NAME")
	    },
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "UK_SCHEMA_PROCEDURE",
	            columnNames = {"SCHEMA_NAME", "PROCEDURE_NAME"}
	        )
	    }
	)
public class ProcedureMaster extends AuditEntity {

    @Id
    @Column(name = "PROCEDURE_ID")
    private Long procedureId;

    @Column(name = "PROCEDURE_UUID", nullable = false, unique = true)
    private String procedureUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DATABASE_ID")
    private DatabaseMaster databaseMaster;

    @Column(name = "SCHEMA_NAME")
    private String schemaName;

    @Column(name = "PACKAGE_NAME")
    private String packageName;

    @Column(name = "PROCEDURE_NAME", nullable = false)
    private String procedureName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROCEDURE_TYPE")
    private ProcedureType procedureType;

    @Column(name = "HTTP_METHOD")
    private String httpMethod;

    @Column(name = "TIMEOUT_SECONDS" , nullable = false)
    private Integer timeoutSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE")
    private ActiveStatus active;

    @OneToMany(mappedBy = "procedureMaster",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ProcedureParameter> parameters;
    
    
}

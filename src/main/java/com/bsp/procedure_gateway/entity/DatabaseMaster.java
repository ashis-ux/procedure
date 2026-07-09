package com.bsp.procedure_gateway.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.DatabaseType;

@Getter
@Setter
@Entity
@Table(
        name = "DATABASE_MASTER",
        indexes = {
                @Index(name = "IDX_DATABASE_NAME", columnList = "DATABASE_NAME"),
                @Index(name = "IDX_DATABASE_ACTIVE", columnList = "ACTIVE")
        }
)
public class DatabaseMaster extends AuditEntity {

    @Id
    @Column(name = "DATABASE_ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "database_master_gen")
    @TableGenerator(
    	    name = "database_master_gen",
    	    table = "ID_GENERATOR",
    	    pkColumnName = "GEN_NAME",
    	    valueColumnName = "GEN_VALUE",
    	    pkColumnValue = "PROCEDURE_DATABASE_MASTER_ID",
    	    allocationSize = 1
    	)
    private Long databaseId;

    @Column(name = "DATABASE_NAME", nullable = false, length = 100)
    private String databaseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "DATABASE_TYPE", nullable = false)
    private DatabaseType databaseType;

    @Column(name = "HOST", nullable = false)
    private String host;

    @Column(name = "PORT", nullable = false)
    private Integer port;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "SID")
    private String sid;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE")
    private ActiveStatus active;

    @OneToMany(mappedBy = "databaseMaster", fetch = FetchType.LAZY)
    private List<ProcedureMaster> procedures;
}

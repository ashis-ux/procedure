package com.bsp.procedure_gateway.entity;

import com.bsp.procedure_gateway.enums.ActiveStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "CLIENT_PROCEDURE_MAPPING",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CLIENT_PROCEDURE",
                        columnNames = {
                                "CLIENT_ID",
                                "PROCEDURE_ID"
                        })
        }
)
public class ClientProcedureMapping extends AuditEntity {

    @Id
    @Column(name = "MAPPING_ID")
    private Integer mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLIENT_ID",
            nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "PROCEDURE_ID",
            nullable = false)
    private ProcedureMaster procedureMaster;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE")
    private ActiveStatus active;

}
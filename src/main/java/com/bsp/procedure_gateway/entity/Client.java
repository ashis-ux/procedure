package com.bsp.procedure_gateway.entity;

import java.time.LocalDateTime;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.sevice.impl.AuditListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CLIENT_MASTER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditListener.class)
public class Client extends AuditEntity{

    @Id
    @Column(name = "CLIENT_ID")
    private Integer clientId;

    @Column(name = "CLIENT_UUID", unique = true, length = 36)
    private String clientUuid;

    @Column(name = "CLIENT_NAME", unique = true)
    private String clientName;

    @Column(name = "CLIENT_DESCRIPTION")
    private String clientDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE")
    private ActiveStatus active;


}
package com.bsp.procedure_gateway.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditEntity {

	@Column(name = "CREATED_BY", nullable = false, updatable = false)
	private String createdBy;

	@CreationTimestamp
	@Column(name = "CREATED_DATE", nullable = false, updatable = false)
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@UpdateTimestamp
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

}

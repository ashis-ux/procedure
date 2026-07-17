package com.bsp.procedure_gateway.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.bsp.procedure_gateway.enums.ActiveStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientResponse {

	private Integer clientId;
    private String clientUuid;

    private String clientName;

    private String clientDescription;

    private ActiveStatus active;

    
}
package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.DatabaseType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DatabaseMasterResponse {

    private Long databaseId;

    private String databaseName;

    private DatabaseType databaseType;

    private String host;

    private Integer port;

    private String serviceName;

    private String sid;

    private String username;
    
    private String password;

    private ActiveStatus active;

}

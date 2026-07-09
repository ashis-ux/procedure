package com.bsp.procedure_gateway.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DatabaseDropdownResponse {

    private Long databaseId;

    private String databaseName;

}
package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;
import com.bsp.procedure_gateway.enums.DataType;
import com.bsp.procedure_gateway.enums.ParameterMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureParameterRequest {

    @NotNull(message = "Sequence Number is mandatory.")
    private Integer parameterOrder;

    @NotBlank(message = "Parameter Name is mandatory.")
    @Size(max = 100,
          message = "Parameter Name cannot exceed 100 characters.")
    private String parameterName;

    @NotNull(message = "Data Type is mandatory.")
    private DataType dataType;

    @NotNull(message = "Parameter Mode is mandatory.")
    private ParameterMode parameterMode;

    @NotNull(message = "Required flag is mandatory.")
    private String required;

    @Size(max = 200,
          message = "Default Value cannot exceed 200 characters.")
    private String defaultValue;

    @Size(max = 500,
          message = "Description cannot exceed 500 characters.")
    private String description;

    @NotNull(message = "Status is mandatory.")
    private ActiveStatus active;

}
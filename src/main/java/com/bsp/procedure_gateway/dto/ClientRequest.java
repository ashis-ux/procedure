package com.bsp.procedure_gateway.dto;

import com.bsp.procedure_gateway.enums.ActiveStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {

    @NotBlank(message = "Client name is required.")
    @Size(max = 100, message = "Client name cannot exceed 100 characters.")
    private String clientName;

    @Size(max = 255, message = "Client description cannot exceed 255 characters.")
    @Pattern(
            regexp = "^\\S*$",
            message = "Client description cannot contain spaces."
    )
    private String clientDescription;

    private ActiveStatus active;
}
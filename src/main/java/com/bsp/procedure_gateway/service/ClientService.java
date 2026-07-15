package com.bsp.procedure_gateway.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.procedure_gateway.dto.ClientRequest;
import com.bsp.procedure_gateway.dto.ClientResponse;
import com.bsp.procedure_gateway.enums.ActiveStatus;

public interface ClientService {

     
    ClientResponse createClient(ClientRequest request);

    
    ClientResponse updateClient(Integer clientId, ClientRequest request);
 
    public ClientResponse getClient(
            Integer clientId);

     
    public Page<ClientResponse> getClients(

            String clientName,
            
            ActiveStatus status,


            int page,

            int size);

    
    public ClientResponse regenerateClientUuid(
            Integer clientId);
}
package com.airtel.iq.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendDTMFRequestDO {

    private String vmSessionId;
    
    private String clientCorrelationId;
    
    private String dtmf;
    
    private String participantAddress;
	
    private String customerId;

}

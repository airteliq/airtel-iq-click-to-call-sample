package com.airtel.iq.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class StopAudioRequestDO {

    private String vmSessionId;
    private String clientCorrelationId;
    private int audioId;
    private String customerId;
	
}

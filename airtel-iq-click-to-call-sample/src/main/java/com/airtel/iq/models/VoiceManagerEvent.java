package com.airtel.iq.models;

import java.util.Map;

import com.airtel.iq.contants.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceManagerEvent {

    
    private EventType eventType;

    private String vmSessionId;

    private String clientCorrelationId;

    private String participantAddress;

    private String participantName;

    
    private String event;

    private String eventId;

    private String waitTime;

    private Map metaData;

    private String requestName;

  
}
package com.airtel.iq.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VoiceManagerResponseDO {

    private String clientCorrelationId;
    private String vmSessionId;
    private Integer audioId;
    private List<Participant> participants;


}

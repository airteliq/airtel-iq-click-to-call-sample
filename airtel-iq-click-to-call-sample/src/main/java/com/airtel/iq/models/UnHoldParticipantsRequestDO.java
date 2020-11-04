package com.airtel.iq.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class UnHoldParticipantsRequestDO {
    private List<Participant> participants;
    private String vmSessionId;
    private String customerId;
    private String clientCorrelationId;
}

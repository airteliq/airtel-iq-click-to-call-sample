package com.airtel.iq.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class RecordRequestDO {

    private String clientCorrelationId;
    private String vmSessionId;
    private Participant participant;
    private RecordConfig callRecordConfig;
    private String customerId;
    private List<ParticipantRecordingRequest> participantRequests;
    private boolean legwiseRecordingEnabled;

}

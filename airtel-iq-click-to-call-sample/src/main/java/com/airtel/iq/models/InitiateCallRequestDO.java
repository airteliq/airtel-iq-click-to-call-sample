package com.airtel.iq.models;

import java.util.List;
import java.util.Map;

import com.airtel.iq.contants.MergingStrategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class InitiateCallRequestDO {

    private String clientCorrelationId;
    private String vmSessionId;
    private String callerId;
    private List<Participant> participants;
    private MergingStrategy mergingStrategy;
    private Integer minParticipantToTerminate;
    private Integer maxParticipant;
    private List<CallbackURL> callBackURLs;
    private String customerId;
    private Integer maxTimeoutTime;
    private String agentPoolId;
    private Map<String , Object> metaData;
    private Map<String , Object> systemMetaData;
    private String requestName;



}

package com.airtel.iq.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class IncomingCallParameterRequestDO {
	
	private String vmSessionId;
	private String clientCorrelationId;
	private List<CallbackURL> callBackURLs;

	private Map<String,Object> metaData;
	private Integer minParticipantToTerminate;
	private Integer maxParticipant; 

}


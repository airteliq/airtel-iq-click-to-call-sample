package com.airtel.iq.models;

import java.util.List;
import java.util.Map;

import com.airtel.iq.contants.IncomingCallAction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class IncomingCallActionRequestDO {
	
	private String vmSessionId;
	private IncomingCallAction action;
	private String clientCorrelationId;
	private List<CallbackURL> callBackURLs;

	private String redirectAddress;
	private Map<String,Object> metaData;

}


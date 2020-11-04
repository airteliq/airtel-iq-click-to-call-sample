package com.airtel.iq.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class HangupRequestDO {

	private String clientCorrelationId;
	private String vmSessionId;
	private String customerId;

}

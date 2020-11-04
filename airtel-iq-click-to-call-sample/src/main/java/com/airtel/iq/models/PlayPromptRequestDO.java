package com.airtel.iq.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class PlayPromptRequestDO {

	private String clientCorrelationId;
	private String vmSessionId;
	private List<Participant> participants;
	private String audioURL;
	private List<CallbackURL> callBackURLs;
	private String customerId;
	private boolean beforeAnswer;

	
}

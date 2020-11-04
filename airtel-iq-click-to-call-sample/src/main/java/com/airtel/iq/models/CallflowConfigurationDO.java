package com.airtel.iq.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class CallflowConfigurationDO {

	@Id
	private String callFlowType;
	private String callerId;
	private String customerId;
	private List<CallbackURL> callBackURLS;
	private String toParticipantAddress;
}

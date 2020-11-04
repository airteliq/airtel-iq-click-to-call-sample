package com.airtel.iq.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Participant {
    private String participantName;

    @JsonAlias({"participantNumber", "participantAddress"})
    private String participantAddress;

	
    private String callerId;

	
    @JsonAlias({"audioURL", "audioFile"})
    private String audioURL;

	
    private List<CallbackURL> callBackURLs;

	
    private Integer maxRetries = 1;

	
    private DTMFConfig dtmfConfig;
	
    private Integer audioId = 0;
	
	
    private Integer maxTime = 0;

	
    private boolean enableEarlyMedia = false;

    private String enableRingingEvents;

    
}

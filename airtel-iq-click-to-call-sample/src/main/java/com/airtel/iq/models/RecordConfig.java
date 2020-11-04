package com.airtel.iq.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordConfig {
	
    private boolean beep = false;
    private int maxDurationSeconds = 0;
    private int maxSilenceSeconds = 0;
    private String terminateOn;
}

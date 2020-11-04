package com.airtel.iq.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RecordingRequest {

    private String bridgeId;
    private String name;
    private String format = "wav";
    private int maxDurationSeconds = 0;
    private int maxSilenceSeconds = 0;
    private String ifExists = "overwrite";
    private boolean beep;
    private String terminateOn;
    private String channelId;
}
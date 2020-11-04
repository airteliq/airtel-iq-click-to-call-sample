package com.airtel.iq.models;


import lombok.Data;

@Data
public class AddedAudio {
    private Participant participant;
    private String audioURL;
    private String audioId;
    private String requestId;
}

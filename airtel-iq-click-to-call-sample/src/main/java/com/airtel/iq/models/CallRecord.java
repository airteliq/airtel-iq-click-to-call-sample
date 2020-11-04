package com.airtel.iq.models;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airtel.iq.contants.RecordingStatus;

import lombok.Data;

@Data
public class CallRecord {
  
    private String participantAddress;
    private String name;
    private Date startedAt;
    private Date updatedAt;
    private RecordingStatus status;
    
}

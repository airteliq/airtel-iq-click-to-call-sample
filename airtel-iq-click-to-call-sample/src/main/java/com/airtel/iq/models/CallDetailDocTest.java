package com.airtel.iq.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.airtel.iq.contants.SessionStatus;

import lombok.Data;

@Document
@Data
public class CallDetailDocTest {

    @Id
    private String vmSessionid;
    @Indexed
    private String clientCorrelationId;
    private String customerId;
    private Date startTime;
    private Date endTime;
    private SessionStatus sessionStatus;
    private String callerId;
    private List<Participant> fromParticipants = new ArrayList<>();
    private List<Participant> toParticipants = new ArrayList<>();

    private int maxParticipants = 10;
    private int minParticipantToTerminate;
    private String audioFileURL;
    private int maxRetries;
    private List<CallRecord> callRecordList = new ArrayList<>();
    private List<AddedAudio> addedAudioList = new ArrayList<>();
    private List<Object> notifications = new ArrayList<>();
   
    private Map<String , Object> metaData = new HashMap<>();
    private Map<String , Object> systemMetaData = new HashMap<>();
    private Boolean isFirstAnswered = Boolean.FALSE;
    
}

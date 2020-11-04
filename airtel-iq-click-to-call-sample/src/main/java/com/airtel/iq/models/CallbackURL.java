package com.airtel.iq.models;

import java.util.Map;

import com.airtel.iq.contants.CallBackEvent;
import com.airtel.iq.contants.HttpMethodType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallbackURL {

	
    private CallBackEvent eventType;
    private String notifyURL;
    private HttpMethodType method;
    private Map<String, String> headers;

   

}

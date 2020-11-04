package com.airtel.iq.models;

import java.util.Map;

import com.airtel.iq.contants.RequestMessageType;
import com.airtel.iq.contants.ResponseType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDO {

    private String requestId;

    private String vmSessionId;

    private RequestMessageType requestMessageType;

    private ResponseType responseType;

    private Map<String , Object> responseObject;

    private String errorMessage;

    private String clientCorrelationId;
}

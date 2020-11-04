package com.airtel.iq.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DTMFConfig {

	
    private String dtmfPattern;

	
    private Integer maxDigits = 100;
    
    private Integer minDigits = 1;

	
    private Integer firstDigitTimer = 5000;

	
    private Integer interDigitTimer = 2000;

    private List<String> allowedInputs;

    private String dtmfInput;
}

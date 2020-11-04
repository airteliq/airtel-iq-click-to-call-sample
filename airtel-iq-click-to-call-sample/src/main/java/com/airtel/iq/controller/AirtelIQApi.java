package com.airtel.iq.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.airtel.iq.models.RequestDO;
import com.airtel.iq.models.ResponseDO;
import com.airtel.iq.service.AirtelIQService;
import com.airtel.iq.utils.AirtelIQUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AirtelIQApi {

    private final Logger log = LoggerFactory.getLogger(AirtelIQApi.class);

	@Autowired
	AirtelIQUtil airtelIQUtil ;
	@Autowired
	private AirtelIQService airtelIQService;
	@Autowired
	ObjectMapper mapper;

	/**
	 * Api to receive real time events
	 * @param notification
	 * @throws Exception
	 */
	@RequestMapping(value = "/realTimeEvent", method = RequestMethod.POST)
	public void sampleRealTimeEvent(@RequestBody Map<String,Object> notification) throws Exception {
		log.info("notification received = "+mapper.writeValueAsString(notification));
		airtelIQService.processNotification(notification);
	}
	
	/**
	 * Api to start a call
	 * @param startCallflowDO
	 * @return
	 * @throws Exception
	 * 
	 * 
	 * flow - initiate call -> play audio to the first participant -> 
	 * 		  add participant to the call session -> stop the audio -> 
	 * 		  record the call -> hangup if any of the participant disconnects
	 */
    @RequestMapping(value = "/clickToCall", method = RequestMethod.POST)
    public ResponseDO clickToCall(@RequestBody RequestDO request) throws Exception{
    	log.info("initiate call for request = "+mapper.writeValueAsString(request));
    	return airtelIQService.clickToCall(request);
    }

}

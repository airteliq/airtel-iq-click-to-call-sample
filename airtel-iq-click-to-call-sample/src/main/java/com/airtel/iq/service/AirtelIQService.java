package com.airtel.iq.service;

import java.util.Map;

import com.airtel.iq.models.RequestDO;
import com.airtel.iq.models.ResponseDO;

public interface AirtelIQService {

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	ResponseDO clickToCall(RequestDO request) throws Exception;

	/**
	 * service method to process a notification
	 * @param notification
	 * @throws Exception
	 */
	void processNotification(Map<String,Object> notification) throws Exception;

}

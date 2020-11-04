package com.airtel.iq.utils;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class AirtelIQUtil {

	@Value("${auth.name}")
	private String authName;

	@Value("${auth.password}")
	private String authPassword;

	public HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Auth logic
		String authString = authName + ":" + authPassword;
		String authEnc = Base64.getEncoder().encodeToString(authString.getBytes());
		httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + authEnc);
		return httpHeaders;
	}
}

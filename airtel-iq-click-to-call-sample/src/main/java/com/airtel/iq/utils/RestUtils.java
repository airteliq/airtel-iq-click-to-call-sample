package com.airtel.iq.utils;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestUtils {

	private static final ObjectMapper mapper = new ObjectMapper()
			.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES).setSerializationInclusion(Include.NON_NULL);

	private static final Logger logger = LogManager.getLogger(RestUtils.class);

	public static Object getRequest(String url, RestTemplate restTemplate, TypeReference mappingTypeReference,
			Map<String, String> queryParams, HttpHeaders httpHeaders) throws Exception {

		
		
		ResponseEntity<String> responseEntity = null;
		Object response = null;
		long startTime = new Date().getTime();
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
			// Add query parameter
			if (null != queryParams) {
				queryParams.entrySet().forEach(entry -> builder.queryParam(entry.getKey(), entry.getValue()));
			}
			HttpHeaders apiHeaders = getCommonHeaders();

			if (null != httpHeaders && !httpHeaders.isEmpty()) {
				for (Map.Entry entry : httpHeaders.entrySet()) {
					String headerKey = entry.getKey().toString();
					if (apiHeaders.get(headerKey) != null) {
						apiHeaders.remove(headerKey);
					}
				}
				apiHeaders.addAll(httpHeaders);
			}

			HttpEntity requestEntity = new HttpEntity(apiHeaders);

			logger.info("GET Resquest Url :{} ", builder.build().toUriString());
			startTime = new Date().getTime();

			responseEntity = restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET, requestEntity,
					String.class);

			if (mappingTypeReference != null) {
				if ("java.lang.String".equals(mappingTypeReference.getType().getTypeName())) {
					response = responseEntity.getBody();
				} else {
					response = mapper.readValue(responseEntity.getBody(), mappingTypeReference);
				}
			}
			logger.debug("Calling Service {} and get response {}", builder.build().toUriString(), response);
			return response;

		} catch (Exception ex) {
			logger.error("Exception Occured while making get rest call to url :: {} ", url, ex);
			throw ex;
		}
	}

	public static Object postRequest(String url, RestTemplate restTemplate, Object request,
			TypeReference mappingTypeReference, Map<String, String> queryParams, HttpHeaders httpHeaders)
			throws Exception {
		ResponseEntity<String> responseEntity = null;
		Object response = null;
		long startTime = new Date().getTime();

		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
			// Add query parameter
			if (null != queryParams) {
				queryParams.entrySet().forEach(entry -> builder.queryParam(entry.getKey(), entry.getValue()));
			}
			HttpHeaders apiHeaders = getCommonHeaders();
			if (null != httpHeaders && !httpHeaders.isEmpty()) {
				for (Map.Entry entry : httpHeaders.entrySet()) {
					String headerKey = entry.getKey().toString();
					if (apiHeaders.get(headerKey) != null) {
						apiHeaders.remove(headerKey);
					}
				}
				apiHeaders.addAll(httpHeaders);
			}
			HttpEntity<Object> requestEntity = new HttpEntity<>(request, apiHeaders);
			logger.info("POST Resquest Url :{} ", builder.build().toUriString());
			logger.debug("Request Entity :{} ", requestEntity);
			startTime = new Date().getTime();

			responseEntity = restTemplate.exchange(builder.build().toUriString(), HttpMethod.POST, requestEntity,
					String.class);

			logger.debug("Response Entity :{} ", responseEntity);

			if (responseEntity.getHeaders().get(HttpHeaders.CONTENT_TYPE) != null && responseEntity.getHeaders()
					.get(HttpHeaders.CONTENT_TYPE).get(0).contains(MediaType.APPLICATION_XML_VALUE)) {
				JSONObject responseObj = null;
				try {
					// converting xml to json
					responseObj = XML.toJSONObject(responseEntity.getBody());
				} catch (JSONException je) {
					logger.error(" Failed to convert body response from xml to json {}", je);
				}

				if (responseObj != null) {
					if (mappingTypeReference == null
							|| "java.lang.String".equals(mappingTypeReference.getType().getTypeName())) {
						response = responseEntity.getBody();
					} else {
						response = mapper.readValue(responseObj.toString(), mappingTypeReference);
					}
				}
			} else {
				if (mappingTypeReference == null
						|| "java.lang.String".equals(mappingTypeReference.getType().getTypeName())) {
					response = responseEntity.getBody();
				} else {
					response = mapper.readValue(responseEntity.getBody(), mappingTypeReference);
				}
			}
			return response;

		} catch (Exception ex) {
			logger.error("Exception Occured while making get rest call to url :: {} ", url, ex);
			throw ex;
		}
	}
	private static HttpHeaders getCommonHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		return httpHeaders;
	}
}
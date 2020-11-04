package com.airtel.iq.adapter.impl;


import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.airtel.iq.adapter.VoiceManagerAdapter;
import com.airtel.iq.models.AddParticipantRequestDO;
import com.airtel.iq.models.ApiResponseDO;
import com.airtel.iq.models.HangupRequestDO;
import com.airtel.iq.models.IncomingCallActionRequestDO;
import com.airtel.iq.models.IncomingCallParameterRequestDO;
import com.airtel.iq.models.InitiateCallRequestDO;
import com.airtel.iq.models.MuteParticipantsRequestDO;
import com.airtel.iq.models.PlayDTMFRequestDO;
import com.airtel.iq.models.PlayPromptRequestDO;
import com.airtel.iq.models.PutParticipantsOnHoldRequestDO;
import com.airtel.iq.models.RecordRequestDO;
import com.airtel.iq.models.SendDTMFRequestDO;
import com.airtel.iq.models.StopAudioRequestDO;
import com.airtel.iq.models.UnHoldParticipantsRequestDO;
import com.airtel.iq.models.UnMuteParticipantsRequestDO;
import com.airtel.iq.models.VoiceManagerResponseDO;
import com.airtel.iq.utils.AirtelIQUtil;
import com.airtel.iq.utils.AppUtil;
import com.airtel.iq.utils.RestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class VoiceManagerAdapterImpl implements VoiceManagerAdapter {

    private final Logger log = LoggerFactory.getLogger(VoiceManagerAdapterImpl.class);

    @Autowired
    private ObjectMapper objectMapper;


    @Value("${initiate.call.URL}")
    private String initiateCallURL;

    @Value("${add.participant.URL}")
    private String addParticipantURL;

    @Value("${play.prompt.URL}")
    private String playPromptURL;

    @Value("${play.DTMF.URL}")
    private String playDTMFURL;
    
    @Value("${collect.DTMF.URL}")
    private String collectDTMFURL;

    @Value("${hangup.URL}")
    private String hangupURL;

    @Value("${incoming.call.action.URL}")
    private String incomingCallActionUrl;
    
    @Value("${incoming.call.action.V2.URL}")
    private String incomingCallActionV2Url;
    
    @Value("${incoming.call.parameters.URL}")
    private String incomingCallParametersUrl;

    @Value("${record.call.URL}")
    private String recordURL;

    @Value("${stop.audio.URL}")
    private String stopAudioURL;
   
    @Value("${mute.participants.URL}")
    private String muteParticipantsURL;

    @Value("${un-mute.participants.URL}")
    private String unMuteParticipantsURL;

    @Value("${hold.participants.URL}")
    private String putParticipantsOnHoldURL;

    @Value("${un-hold.participants.URL}")
    private String unHoldParticipantsURL;
    
    @Value("${send.dtmf.URL}")
	private String sendDtmfUrl;



    private RestTemplate restTemplate = null;
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
    private CloseableHttpClient closeableHttpClient = null;

   
    @Value("${voice.manager.read.timeout}")
    private Integer readTimeout;

    @Value("${voice.manager.connection.timeout}")
    private Integer connectTimeout;
    @Value("${voice.manager.connect.request.timeout}")
    private Integer connectRequestTimeout;

    @Autowired
    @Qualifier("voiceManagerConnectionKeepAliveStrategy")
    private ConnectionKeepAliveStrategy voiceManagerConnectionKeepAliveStrategy;

    @Value("${voice.manager.max.idle.timeout}")
    private Integer voiceManagerMaxIdleTime;

    @Value("${voice.manager.rest.template.connection.pool.size:100}")
    private Integer voiceManagerRestTemplateConnectionPoolSize;

    @Value("${voice.manager.rest.template.maxPerRoute.pool.size:10}")
    private Integer voiceManagerRestTemplateMaxPerRouteSize;

    @Value("${voice.manager.validate.after.inactivity:15000}")
    private Integer voiceManagerValidateAfterInactivity;
    
  
    @Autowired
    private AirtelIQUtil airtelIQUtil;

    @PostConstruct
    public void intializeRestTemplate() {
        RequestConfig requestConfig =  RequestConfig.DEFAULT;
       
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(voiceManagerRestTemplateConnectionPoolSize);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(voiceManagerRestTemplateMaxPerRouteSize);
        poolingHttpClientConnectionManager.closeExpiredConnections();
        poolingHttpClientConnectionManager.closeIdleConnections(voiceManagerMaxIdleTime, TimeUnit.MILLISECONDS);
        poolingHttpClientConnectionManager.setValidateAfterInactivity(voiceManagerValidateAfterInactivity);

 
        closeableHttpClient = HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(voiceManagerConnectionKeepAliveStrategy)
                .build();

        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(closeableHttpClient);
        httpComponentsClientHttpRequestFactory.setReadTimeout(readTimeout);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(connectTimeout);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(connectRequestTimeout);
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    @PreDestroy
    public void closedConnectionManager() {
        if (!AppUtil.isEmpty(poolingHttpClientConnectionManager))
            poolingHttpClientConnectionManager.close();

        if (!AppUtil.isEmpty(closeableHttpClient))
            try {
                closeableHttpClient.close();
            } catch (Exception e) {
                log.error("Error closing closeableHttpClient:", e);
            }
    }

    @Override
    public VoiceManagerResponseDO initaiteCall(InitiateCallRequestDO initiateCallRequest) throws Exception {

        try {
            log.info("initiate call with correlation id = " + initiateCallRequest.getClientCorrelationId());
            log.debug("initiate call request for correlationId = " + initiateCallRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(initiateCallRequest));

            TypeReference<VoiceManagerResponseDO> typeReference = new TypeReference<VoiceManagerResponseDO>() {
            };

            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            VoiceManagerResponseDO response = (VoiceManagerResponseDO) RestUtils.postRequest(initiateCallURL,
                    restTemplate, initiateCallRequest, typeReference, null, httpHeaders);

            log.info("initiate call successful for correlation id =" + initiateCallRequest.getClientCorrelationId());
            log.debug("initiate call response for correlationId = " + initiateCallRequest.getClientCorrelationId()
                    + " = " + objectMapper.writeValueAsString(response));


            return response;


        } catch (Exception e) {
            log.error("exception occured while initiating call for correlationId = "
                    + initiateCallRequest.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO addParticipant(AddParticipantRequestDO addParticipantRequest) throws Exception {
        try {
            log.info("add participant with correlation id = " + addParticipantRequest.getClientCorrelationId());
            log.debug("add participant request for correlationId = " + addParticipantRequest.getClientCorrelationId()
                    + " = " + objectMapper.writeValueAsString(addParticipantRequest));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };

            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(addParticipantURL,
                    restTemplate, addParticipantRequest, typeReference, null, httpHeaders);

            log.info("add participant successful for correlation id =" + addParticipantRequest.getClientCorrelationId());
            log.debug("add participant response for correlationId = " + addParticipantRequest.getClientCorrelationId()
                    + " = " + objectMapper.writeValueAsString(response));

            return response;


        } catch (Exception e) {
            log.error("exception occured while add participant for correlationId = "
                    + addParticipantRequest.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO playPrompt(PlayPromptRequestDO playPromptRequest) throws Exception {

        try {
            log.info("play prompt with correlation id = " + playPromptRequest.getClientCorrelationId());
            log.debug("play prompt request for correlationId = " + playPromptRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(playPromptRequest));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };

            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(playPromptURL,
                    restTemplate, playPromptRequest, typeReference, null, httpHeaders);

            log.info("play prompt successful for correlation id =" + playPromptRequest.getClientCorrelationId());
            log.debug("play prompt response for correlationId = " + playPromptRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;

        } catch (Exception e) {
            log.error("exception occured while play audio for correlationId = "
                    + playPromptRequest.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
	public ApiResponseDO playDTMF(PlayDTMFRequestDO playDTMFRequest) throws Exception {

        try {
            log.info("play dtmf with correlation id = " + playDTMFRequest.getClientCorrelationId());
            log.debug("play DTMF request for correlationId = " + playDTMFRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(playDTMFRequest));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(playDTMFURL, restTemplate,
                    playDTMFRequest, typeReference, null, httpHeaders);

            log.info("play dtmf successful for correlation id =" + playDTMFRequest.getClientCorrelationId());
            log.debug("play DTMF response for correlationId = " + playDTMFRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;

        } catch (Exception e) {
            log.error("exception occured while play DTMF for correlationId = "
                    + playDTMFRequest.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO hangup(HangupRequestDO hangupRequest) throws Exception {

        try {
            log.info("hangup with correlation id = " + hangupRequest.getClientCorrelationId());
            log.debug("hangup request for correlationId = " + hangupRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(hangupRequest));
            //
            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(hangupURL, restTemplate,
                    hangupRequest, typeReference, null, httpHeaders);

            log.info("hangup successful for correlation id =" + hangupRequest.getClientCorrelationId());
            log.debug("hangup response for correlationId = " + hangupRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;


        } catch (Exception e) {
            log.error("exception occured while hangup call for correlationId = "
                    + hangupRequest.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO incomingCallAction(IncomingCallActionRequestDO incomingCallActionRequestDO) throws  Exception {
        try {
            log.debug("incoming call action request for correlationId = "
                    + incomingCallActionRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(incomingCallActionRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(incomingCallActionUrl,
                    restTemplate, incomingCallActionRequestDO, typeReference, null, httpHeaders);

            log.info("incoming call action response for correlationId = "
                    + incomingCallActionRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;


        } catch (Exception e) {
            log.error("exception occured while incoming call action call for correlationId = "
                    + incomingCallActionRequestDO.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO recordCall(RecordRequestDO recordRequestDO) throws Exception {

        try {
            log.info("record call with correlation id = " + recordRequestDO.getClientCorrelationId());
            log.debug("record request for correlationId = " + recordRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(recordRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(recordURL, restTemplate,
                    recordRequestDO, typeReference, null, httpHeaders);

            log.info("record call successful for correlation id =" + recordRequestDO.getClientCorrelationId());
            log.debug("record response for correlationId = " + recordRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;

        } catch (Exception e) {
            log.error("exception occured while record call for correlationId = "
                    + recordRequestDO.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO stopAudio(StopAudioRequestDO stopAudioRequestDO) throws Exception {
        try {
            log.info("stop audio with correlation id = " + stopAudioRequestDO.getClientCorrelationId());
            log.debug("stop audio for correlationId = " + stopAudioRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(stopAudioRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(stopAudioURL, restTemplate,
                    stopAudioRequestDO, typeReference, null, httpHeaders);

            log.info("stop audio successful for correlation id =" + stopAudioRequestDO.getClientCorrelationId());
            log.debug("stop audio response for correlationId = " + stopAudioRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;

        } catch (Exception e) {
            log.error("exception occured while stopping audio for correlationId = "
                    + stopAudioRequestDO.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

	@Override
	public ApiResponseDO collectDTMF(PlayDTMFRequestDO playDTMFRequest) throws Exception {
		try {
            log.info("collect dtmf with correlation id = " + playDTMFRequest.getClientCorrelationId());
            log.debug("collect DTMF request for correlationId = " + playDTMFRequest.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(playDTMFRequest));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(collectDTMFURL, restTemplate,
                    playDTMFRequest, typeReference, null, httpHeaders);

            log.info("collect dtmf successful for correlation id =" + playDTMFRequest.getClientCorrelationId());
            log.debug("collect DTMF response for correlationId = " + playDTMFRequest.getClientCorrelationId() + " = "
            		+ objectMapper.writeValueAsString(response));

            return response;

        } catch (Exception e) {	
        	   log.error("exception occured while collect DTMF for correlationId = "
                       + playDTMFRequest.getClientCorrelationId() + ", exception = ", e);
               throw e;
           }
   	}

    @Override
	public ApiResponseDO incomingCallParameters(
			IncomingCallParameterRequestDO incomingCallParameterRequestDO) throws Exception {
		try {
            log.debug("incoming call parameters request for correlationId = "
                    + incomingCallParameterRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(incomingCallParameterRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(incomingCallParametersUrl,
                    restTemplate, incomingCallParameterRequestDO, typeReference, null, httpHeaders);

            log.info("incoming call parameters response for correlationId = "
                    + incomingCallParameterRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;


        } catch (Exception e) {
            log.error("exception occured while incoming call parameters call for correlationId = "
                    + incomingCallParameterRequestDO.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
	}
	@Override
    public ApiResponseDO incomingCallActionV2(IncomingCallActionRequestDO incomingCallActionRequestDO) throws  Exception{
        try {
            log.debug("incoming call action v2 request for correlationId = "
                    + incomingCallActionRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(incomingCallActionRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
            };
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(incomingCallActionV2Url,
                    restTemplate, incomingCallActionRequestDO, typeReference, null, httpHeaders);

            log.info("incoming call action v2 response for correlationId = "
                    + incomingCallActionRequestDO.getClientCorrelationId() + " = "
                    + objectMapper.writeValueAsString(response));

            return response;


        } catch (Exception e) {
            log.error("exception occured while incoming call action v2 call for correlationId = "
                    + incomingCallActionRequestDO.getClientCorrelationId() + ", exception = ", e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO muteParticipants(MuteParticipantsRequestDO muteParticipantsRequestDO) throws Exception {
        try {
            log.debug("mute participants action request for correlationId = {} = {}",
                    muteParticipantsRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(muteParticipantsRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>(){};
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(muteParticipantsURL,
                    restTemplate, muteParticipantsRequestDO, typeReference, null, httpHeaders);

            log.info("mute participants action request for correlationId = {} = {}",
                    muteParticipantsRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(muteParticipantsRequestDO));
            return response;
        } catch (Exception e) {
            log.error("exception occurred while mute participants action request for correlationId = {}, exception = {}",
                    muteParticipantsRequestDO.getClientCorrelationId(), e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO unMuteParticipants(UnMuteParticipantsRequestDO unMuteParticipantsRequestDO) throws Exception {
        try {
            log.debug("un-mute participants action request for correlationId = {} = {}",
                    unMuteParticipantsRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(unMuteParticipantsRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>(){};
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(unMuteParticipantsURL,
                    restTemplate, unMuteParticipantsRequestDO, typeReference, null, httpHeaders);

            log.info("un-mute participants action request for correlationId = {} = {}",
                    unMuteParticipantsRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(unMuteParticipantsRequestDO));
            return response;
        } catch (Exception e) {
            log.error("exception occurred while un-mute participants action request for correlationId = {}, exception = {}",
                    unMuteParticipantsRequestDO.getClientCorrelationId(), e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO putParticipantsOnHold(PutParticipantsOnHoldRequestDO putParticipantsOnHoldRequestDO) throws Exception {
        try {
            log.debug("put participants on hold action request for correlationId = {} = {}",
                    putParticipantsOnHoldRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(putParticipantsOnHoldRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>(){};
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(putParticipantsOnHoldURL,
                    restTemplate, putParticipantsOnHoldRequestDO, typeReference, null, httpHeaders);

            log.info("put participants on hold action request for correlationId = {} = {}",
                    putParticipantsOnHoldRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(putParticipantsOnHoldRequestDO));
            return response;
        } catch (Exception e) {
            log.error("exception occurred while put participants on hold action request for correlationId = {}, exception = {}",
                    putParticipantsOnHoldRequestDO.getClientCorrelationId(), e);
            throw e;
        }
    }

    @Override
    public ApiResponseDO unHoldParticipants(UnHoldParticipantsRequestDO unHoldParticipantsRequestDO) throws Exception {
        try {
            log.debug("un-hold participants action request for correlationId = {} = {}",
                    unHoldParticipantsRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(unHoldParticipantsRequestDO));

            TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>(){};
            HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

            ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(unHoldParticipantsURL,
                    restTemplate, unHoldParticipantsRequestDO, typeReference, null, httpHeaders);

            log.info("un-hold participants action request for correlationId = {} = {}",
                    unHoldParticipantsRequestDO.getClientCorrelationId(),
                    objectMapper.writeValueAsString(unHoldParticipantsRequestDO));
            return response;
        } catch (Exception e) {
            log.error("exception occurred while un-hold participants action request for correlationId = {}, exception = {}",
                    unHoldParticipantsRequestDO.getClientCorrelationId(), e);
            throw e;
        }
    }
    
    @Override
	public ApiResponseDO sendDtmf(SendDTMFRequestDO sendDTMFRequestDO) throws  Exception{
		try {
			log.info("send dtmf with correlation id = " + sendDTMFRequestDO.getClientCorrelationId());
			log.debug("send DTMF request for correlationId = " + sendDTMFRequestDO.getClientCorrelationId() + " = "
					+ objectMapper.writeValueAsString(sendDTMFRequestDO));

			TypeReference<ApiResponseDO> typeReference = new TypeReference<ApiResponseDO>() {
			};
			HttpHeaders httpHeaders = airtelIQUtil.getHeaders();

			ApiResponseDO response = (ApiResponseDO) RestUtils.postRequest(sendDtmfUrl, restTemplate,
					sendDTMFRequestDO, typeReference, null, httpHeaders);

			log.info("send dtmf successful for correlation id =" + sendDTMFRequestDO.getClientCorrelationId());
			log.debug("send DTMF response for correlationId = " + sendDTMFRequestDO.getClientCorrelationId() + " = "
					+ objectMapper.writeValueAsString(response));

			return response;

		} catch (Exception e) {
			log.error("exception occured while plasendy DTMF for correlationId = "
					+ sendDTMFRequestDO.getClientCorrelationId() + ", exception = ", e);
			throw e;
		}
	}

}

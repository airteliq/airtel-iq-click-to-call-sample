package com.airtel.iq.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.airtel.iq.adapter.VoiceManagerAdapter;
import com.airtel.iq.contants.AirtelIQConstants;
import com.airtel.iq.contants.EventType;
import com.airtel.iq.contants.RequestMessageType;
import com.airtel.iq.contants.SessionStatus;
import com.airtel.iq.models.AddParticipantRequestDO;
import com.airtel.iq.models.AddedAudio;
import com.airtel.iq.models.ApiResponseDO;
import com.airtel.iq.models.CallDetailDocTest;
import com.airtel.iq.models.CallflowConfigurationDO;
import com.airtel.iq.models.HangupRequestDO;
import com.airtel.iq.models.InitiateCallRequestDO;
import com.airtel.iq.models.Participant;
import com.airtel.iq.models.PlayPromptRequestDO;
import com.airtel.iq.models.RecordConfig;
import com.airtel.iq.models.RecordRequestDO;
import com.airtel.iq.models.RequestDO;
import com.airtel.iq.models.ResponseDO;
import com.airtel.iq.models.StopAudioRequestDO;
import com.airtel.iq.models.VoiceManagerEvent;
import com.airtel.iq.models.VoiceManagerResponseDO;
import com.airtel.iq.repo.CallDetailRepo;
import com.airtel.iq.repo.CallflowConfigurationRepo;
import com.airtel.iq.service.AirtelIQService;
import com.airtel.iq.utils.AirtelIQUtil;
import com.airtel.iq.utils.AppUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AirtelIQServiceImpl implements AirtelIQService {
	private final Logger log = LoggerFactory.getLogger(AirtelIQServiceImpl.class);

	@Autowired
	VoiceManagerAdapter voiceManagerAdapter;
	@Autowired
	ObjectMapper mapper;

	@Autowired
	AirtelIQUtil airtelIQUtil;

	@Autowired
	CallDetailRepo callDetailRepo;

	@Autowired
	CallflowConfigurationRepo callflowConfigurationRepo;

	@Override
	public ResponseDO clickToCall(RequestDO request) throws Exception {
		// initiate a call session using the from participants

		CallflowConfigurationDO callflowConfiguration = callflowConfigurationRepo.findByCallFlowType("CLICK_TO_CALL");

		InitiateCallRequestDO initiateCallRequestDO = new InitiateCallRequestDO();
		initiateCallRequestDO.setCallBackURLs(callflowConfiguration.getCallBackURLS());
		initiateCallRequestDO.setCallerId(callflowConfiguration.getCallerId());
		initiateCallRequestDO.setClientCorrelationId(UUID.randomUUID().toString());
		initiateCallRequestDO.setCustomerId(callflowConfiguration.getCustomerId());

		Participant participant = new Participant();
		participant.setParticipantAddress(request.getParticipantAddress());

		List<Participant> participants = new ArrayList<>();
		participants.add(participant);
		initiateCallRequestDO.setParticipants(participants);
		initiateCallRequestDO.setRequestName(AirtelIQConstants.INITIATE_CALL_1);
		log.info("initiate call request =" + mapper.writeValueAsString(initiateCallRequestDO));
		VoiceManagerResponseDO response = voiceManagerAdapter.initaiteCall(initiateCallRequestDO);
		log.info("initiate call response = " + mapper.writeValueAsString(response));

		// create a call detail doc for the ongoing call
		createCallDetailDoc(initiateCallRequestDO, response, callflowConfiguration);

		ResponseDO responseDO = new ResponseDO();
		responseDO.setStatus(AirtelIQConstants.SUCCESS);
		responseDO.setVmSessionId(response.getVmSessionId());
		return responseDO;
	}

	public void createCallDetailDoc(InitiateCallRequestDO initiateCallRequestDO, VoiceManagerResponseDO response,
			CallflowConfigurationDO callflowConfiguration) throws Exception {
		CallDetailDocTest callDetailDoc = new CallDetailDocTest();
		callDetailDoc.setCallerId(initiateCallRequestDO.getCallerId());
		callDetailDoc.setClientCorrelationId(initiateCallRequestDO.getClientCorrelationId());
		callDetailDoc.setCustomerId(initiateCallRequestDO.getCustomerId());
		callDetailDoc.setFromParticipants(initiateCallRequestDO.getParticipants());

		Participant participant = new Participant();
		participant.setParticipantAddress(callflowConfiguration.getToParticipantAddress());

		List<Participant> participants = new ArrayList<>();
		participants.add(participant);
		callDetailDoc.setToParticipants(participants);
		callDetailDoc.setSessionStatus(SessionStatus.ON_GOING);
		callDetailDoc.setStartTime(new Date());
		callDetailDoc.setVmSessionid(response.getVmSessionId());
		log.info("call detail doc created = " + mapper.writeValueAsString(callDetailDoc));
		callDetailRepo.save(callDetailDoc);
	}

	@Override
	public void processNotification(Map<String, Object> notification) throws Exception {

		// there are 2 type of events which will be received on the call back url
		// provided in initiate call api
		// 1. events for the api i.e - success/error of the api along with a response
		// payload

		if (notification.containsKey(AirtelIQConstants.REQUEST_MESSAGE_TYPE)) {
			ApiResponseDO apiResponseDO = mapper.convertValue(notification, ApiResponseDO.class);
			log.info("api response notification = " + mapper.writeValueAsString(apiResponseDO));
			processApiResponse(apiResponseDO);
		}
		// 2. events relating to the call i.e - call/media/dtmf/recording events
		else if (notification.containsKey(AirtelIQConstants.EVENT_TYPE)) {
			VoiceManagerEvent voiceManagerEvent = mapper.convertValue(notification, VoiceManagerEvent.class);
			log.info("event notification = " + mapper.writeValueAsString(voiceManagerEvent));
			processEvent(voiceManagerEvent);
		} else {
			log.info("invalid event =" + mapper.writeValueAsString(notification));
		}
	}

	public void processApiResponse(ApiResponseDO apiResponseDO) throws Exception {
		// if the session is not disconnected take action according to the event
		CallDetailDocTest callDetailDocTest = callDetailRepo.findByVmSessionid(apiResponseDO.getVmSessionId());
		if (!AppUtil.isEmpty(callDetailDocTest) && !AppUtil.isEmpty(callDetailDocTest.getSessionStatus())
				&& callDetailDocTest.getSessionStatus().equals(SessionStatus.ON_GOING)) {
			if (apiResponseDO.getRequestMessageType().equals(RequestMessageType.PLAY_AUDIO_REQ)) {
				switch (apiResponseDO.getResponseType()) {
				case SUCCESS:
					// 1.update audio id in db so that it can be used to stop the audio
					VoiceManagerResponseDO response = mapper.convertValue(apiResponseDO.getResponseObject(),
							VoiceManagerResponseDO.class);
					log.info("response object = " + mapper.writeValueAsString(response));
					updateAudioId(response, apiResponseDO);
					break;
				case ERROR:
					// hangup the session if play audio fails
					hangup(apiResponseDO.getVmSessionId());
					break;
				}

			} else if (apiResponseDO.getRequestMessageType().equals(RequestMessageType.ADD_PARTICIPANT_REQ)) {
				switch (apiResponseDO.getResponseType()) {
				case SUCCESS:
					// play audio to the first participant
					playAudio(apiResponseDO.getVmSessionId(),
							callDetailDocTest.getFromParticipants().get(0).getParticipantAddress());
					break;
				case ERROR:
					// hangup the session if add participant fails
					hangup(apiResponseDO.getVmSessionId());
					break;
				default:
					break;
				}
			} else if (apiResponseDO.getRequestMessageType().equals(RequestMessageType.STOP_AUDIO_REQ)) {
				switch (apiResponseDO.getResponseType()) {
				// hangup the session if stop audio fails

				case ERROR:
					hangup(apiResponseDO.getVmSessionId());
					break;
				default:
					break;
				}
			} else if (apiResponseDO.getRequestMessageType().equals(RequestMessageType.CALL_RECORD_REQ)) {
				switch (apiResponseDO.getResponseType()) {
				// hangup the session if record call fails

				case ERROR:
					hangup(apiResponseDO.getVmSessionId());
					break;
				default:
					break;
				}
			} else if (apiResponseDO.getRequestMessageType().equals(RequestMessageType.SESSION_REQ)) {
				switch (apiResponseDO.getResponseType()) {
				// update the status of the session to Disconnected when hangup succeeds
				case SUCCESS:
					updateSessionStatus(apiResponseDO.getVmSessionId());
					break;
				default:
					break;
				}
			}
			// save the notification
			saveNotification(apiResponseDO.getVmSessionId(), apiResponseDO);
		} else {
			log.info("session with vm session id  = " + apiResponseDO.getVmSessionId() + "already disconnected");
		}
	}

	public void updateAudioId(VoiceManagerResponseDO response, ApiResponseDO apiResponseDO) throws Exception {
		// according the request id received in the notification update the audio id
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(apiResponseDO.getVmSessionId());
		List<AddedAudio> audioList = callDetailDoc.getAddedAudioList();
		int index = -1;
		if (!AppUtil.isEmpty(audioList)) {
			for (int i = 0; i < audioList.size(); i++) {
				if (audioList.get(i).getRequestId().equals(apiResponseDO.getRequestId())) {
					index = i;
					break;
				}
			}
			if (index != -1) {
				if (!AppUtil.isEmpty(response.getParticipants())) {
					callDetailDoc.getAddedAudioList().get(index)
							.setAudioId(response.getParticipants().get(0).getAudioId().toString());
					log.info("updated call detail doc =" + mapper.writeValueAsString(callDetailDoc));
					callDetailRepo.save(callDetailDoc);
				} else {
					callDetailDoc.getAddedAudioList().get(index).setAudioId(response.getAudioId().toString());
					log.info("updated call detail doc =" + mapper.writeValueAsString(callDetailDoc));
					callDetailRepo.save(callDetailDoc);
				}
			}
		}
	}

	public void updateSessionStatus(String vmSessionId) {
		// update session status = DISCONNECTED
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		callDetailDoc.setSessionStatus(SessionStatus.DISCONNECTED);
		callDetailDoc.setEndTime(new Date());
		callDetailRepo.save(callDetailDoc);
	}

	public void processEvent(VoiceManagerEvent voiceManagerEvent) throws Exception {
		// if the session is not disconnected take action according to the event

		CallDetailDocTest callDetailDocTest = callDetailRepo.findByVmSessionid(voiceManagerEvent.getVmSessionId());
		if (!AppUtil.isEmpty(callDetailDocTest) && !AppUtil.isEmpty(callDetailDocTest.getSessionStatus())
				&& callDetailDocTest.getSessionStatus().equals(SessionStatus.ON_GOING)) {
			if (voiceManagerEvent.getEventType().equals(EventType.CALL)) {
					if (voiceManagerEvent.getEvent().equals(AirtelIQConstants.ANSWER)) {
						
						if(!callDetailDocTest.getIsFirstAnswered()) {
							callDetailDocTest.setIsFirstAnswered(true);
							callDetailRepo.save(callDetailDocTest);
							//add participant

							addParticipant(voiceManagerEvent.getVmSessionId());
						}else {
							//stop audio
							stopAudio(voiceManagerEvent.getVmSessionId());
						}
						
					} else if (voiceManagerEvent.getEvent().equals(AirtelIQConstants.DISCONNECTED)) {
						// hangup the session if any of the participant disconnects the call
						hangup(voiceManagerEvent.getVmSessionId());
					
				} 
				
			} else if (voiceManagerEvent.getEventType().equals(EventType.MEDIA)) {
				if (voiceManagerEvent.getEvent().equals(AirtelIQConstants.PLAYED)) {
					// play the audio again if the audio completes
					playAudio(voiceManagerEvent.getVmSessionId(),
							callDetailDocTest.getFromParticipants().get(0).getParticipantAddress());
				} else if (voiceManagerEvent.getEvent().equals(AirtelIQConstants.STOPPED)) {
					// record the call when the audio stops and both the participants are patched
					record(voiceManagerEvent.getVmSessionId());
				}
			}
			// save the notification
			saveNotification(voiceManagerEvent.getVmSessionId(), voiceManagerEvent);
		} else {
			log.info("session with vm session id  = " + voiceManagerEvent.getVmSessionId() + "already disconnected");
		}
	}

	public void saveNotification(String vmSessionId, Object notification) {
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		callDetailDoc.getNotifications().add(notification);
		callDetailRepo.save(callDetailDoc);
	}

	public void stopAudio(String vmSessionId) throws Exception {
		StopAudioRequestDO stopAudioRequestDO = new StopAudioRequestDO();
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		List<AddedAudio> addedAudioList = callDetailDoc.getAddedAudioList();

		stopAudioRequestDO.setClientCorrelationId(callDetailDoc.getClientCorrelationId());
		stopAudioRequestDO.setCustomerId(callDetailDoc.getCustomerId());
		stopAudioRequestDO.setVmSessionId(vmSessionId);
		stopAudioRequestDO.setAudioId(Integer.valueOf(addedAudioList.get(addedAudioList.size() - 1).getAudioId()));
		log.info("stop audio request = " + mapper.writeValueAsString(stopAudioRequestDO));

		ApiResponseDO response = voiceManagerAdapter.stopAudio(stopAudioRequestDO);
		log.info("stop audio response = " + mapper.writeValueAsString(response));

	}

	public void record(String vmSessionId) throws Exception {
		RecordRequestDO recordRequestDO = new RecordRequestDO();
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		RecordConfig callRecordConfig = new RecordConfig();
		callRecordConfig.setBeep(true);
		recordRequestDO.setCallRecordConfig(callRecordConfig);
		recordRequestDO.setVmSessionId(vmSessionId);
		recordRequestDO.setClientCorrelationId(callDetailDoc.getClientCorrelationId());
		recordRequestDO.setCustomerId(callDetailDoc.getCustomerId());
		log.info("record call request = " + mapper.writeValueAsString(recordRequestDO));

		ApiResponseDO response = voiceManagerAdapter.recordCall(recordRequestDO);
		log.info("record call response = " + mapper.writeValueAsString(response));

	}

	public void playAudio(String vmSessionId, String participantAddress) throws Exception {
		PlayPromptRequestDO playPromptRequestDO = new PlayPromptRequestDO();
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		playPromptRequestDO.setVmSessionId(vmSessionId);
		Participant participant = new Participant();
		participant.setParticipantAddress(participantAddress);
		participant.setAudioURL(AirtelIQConstants.AUDIO_URL);
		List<Participant> participants = new ArrayList<>();
		participants.add(participant);

		playPromptRequestDO.setParticipants(participants);
		playPromptRequestDO.setClientCorrelationId(callDetailDoc.getClientCorrelationId());
		playPromptRequestDO.setCustomerId(callDetailDoc.getCustomerId());
		log.info("play audio request = " + mapper.writeValueAsString(playPromptRequestDO));

		ApiResponseDO response = voiceManagerAdapter.playPrompt(playPromptRequestDO);
		log.info("play audio response = " + mapper.writeValueAsString(response));

		List<AddedAudio> addedAudioList = callDetailDoc.getAddedAudioList();
		AddedAudio addedAudio = new AddedAudio();
		addedAudio.setParticipant(participant);
		addedAudio.setRequestId(response.getRequestId());
		addedAudioList.add(addedAudio);
		callDetailDoc.setAddedAudioList(addedAudioList);
		log.info("updated call detail doc = " + mapper.writeValueAsString(callDetailDoc));
		callDetailRepo.save(callDetailDoc);

	}

	public void hangup(String vmSessionId) throws Exception {
		HangupRequestDO hangupRequestDO = new HangupRequestDO();
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		hangupRequestDO.setClientCorrelationId(callDetailDoc.getClientCorrelationId());
		hangupRequestDO.setCustomerId(callDetailDoc.getCustomerId());
		hangupRequestDO.setVmSessionId(vmSessionId);
		log.info("hangup request = " + mapper.writeValueAsString(hangupRequestDO));
		ApiResponseDO apiResponseDO = voiceManagerAdapter.hangup(hangupRequestDO);
		log.info("hangup response = " + mapper.writeValueAsString(apiResponseDO));

	}

	public void addParticipant(String vmSessionId) throws Exception {
		AddParticipantRequestDO addParticipantRequest = new AddParticipantRequestDO();
		CallDetailDocTest callDetailDoc = callDetailRepo.findByVmSessionid(vmSessionId);
		addParticipantRequest.setClientCorrelationId(callDetailDoc.getClientCorrelationId());
		addParticipantRequest.setRequestName(AirtelIQConstants.ADD_PARTICIPANT_1);
		addParticipantRequest.setVmSessionId(vmSessionId);
		addParticipantRequest.setParticipants(callDetailDoc.getToParticipants());
		addParticipantRequest.setCustomerId(callDetailDoc.getCustomerId());
		log.info("add participant request = " + mapper.writeValueAsString(addParticipantRequest));
		ApiResponseDO apiResponseDO = voiceManagerAdapter.addParticipant(addParticipantRequest);
		log.info("add participant response = " + mapper.writeValueAsString(apiResponseDO));

	}

}

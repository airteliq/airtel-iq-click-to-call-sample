package com.airtel.iq.adapter;

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

public interface VoiceManagerAdapter {

	/**
	 * adapter method to initiate call session
	 * @param initiateCallRequest
	 * @return
	 * @throws Exception
	 */
	public VoiceManagerResponseDO initaiteCall(InitiateCallRequestDO initiateCallRequest) throws Exception;
	
	/**
	 * adapter method to add participant to an existing session
	 * @param addParticipantRequest
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO addParticipant(AddParticipantRequestDO addParticipantRequest) throws Exception;
	
	/**
	 * adapter method to play audio to a participant or on session
	 * @param playPromptRequest
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO playPrompt(PlayPromptRequestDO playPromptRequest) throws Exception;
	
	/**
	 * adapter method to play dtmf on a participant
	 * @param playDTMFRequest
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO playDTMF(PlayDTMFRequestDO playDTMFRequest) throws Exception;
	
	/**
	 * adapter method to destroy a session
	 * @param hangupRequest
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO hangup(HangupRequestDO hangupRequest) throws Exception;
	
	/**
	 * adapter method to accept/reject an incoming call
	 * @param incomingCallActionRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO incomingCallAction(IncomingCallActionRequestDO incomingCallActionRequestDO) throws Exception;
	
	/**
	 * adapter method to record a call at session or participant level
	 * @param recordRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO recordCall(RecordRequestDO recordRequestDO) throws Exception;
	
	/**
	 * adapter method to stop an audio
	 * @param stopAudioRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO stopAudio(StopAudioRequestDO stopAudioRequestDO) throws Exception;
	
	/**
	 * adapter method to set parameters of an incoming call
	 * @param incomingCallParameterRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO incomingCallParameters(
			IncomingCallParameterRequestDO incomingCallParameterRequestDO) throws Exception;
	
	/**
	 * adapter method to accept/reject an incoming call
	 * @param incomingCallActionRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO incomingCallActionV2(IncomingCallActionRequestDO incomingCallActionRequestDO) throws Exception;
	
	/**
	 * adapter method to collect dtmf from a participant
	 * @param playDTMFRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO collectDTMF(PlayDTMFRequestDO playDTMFRequestDO) throws Exception;
	
	/**
	 * adapter method to mute a participant
	 * @param muteParticipantsRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO muteParticipants(MuteParticipantsRequestDO muteParticipantsRequestDO) throws Exception;
	
	/**
	 * adapter method to un mute a participant
	 * @param unMuteParticipantsRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO unMuteParticipants(UnMuteParticipantsRequestDO unMuteParticipantsRequestDO) throws Exception;
	
	/**
	 * adapter method to put a participant on hold
	 * @param putParticipantsOnHoldRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO putParticipantsOnHold(PutParticipantsOnHoldRequestDO putParticipantsOnHoldRequestDO) throws Exception;
	
	/**
	 * adapter method to un hold a participant
	 * @param unHoldParticipantsRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO unHoldParticipants(UnHoldParticipantsRequestDO unHoldParticipantsRequestDO) throws Exception;
	
	/**
	 * adapter method to sent dtmf to a participant
	 * @param sendDTMFRequestDO
	 * @return
	 * @throws Exception
	 */
	public ApiResponseDO sendDtmf(SendDTMFRequestDO sendDTMFRequestDO) throws Exception;
	
}
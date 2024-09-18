package com.example.emotech.demoEmoTechHS;

import java.util.HashMap;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.*;
import java.io.File;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

//import com.sun.media.sound.WaveFileReader;
//import com.sun.media.sound.WaveFileWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.emotech.demoEmoTechHS.data.Audio;
//import com.example.emotech.demoEmoTechHS.data.UploadFileResponse;
import com.example.emotech.demoEmoTechHS.repository.AudioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Establishes WebSocket connections to handle audio streaming and file creation upon
 * receiving specific messages from clients. It utilizes Spring framework components
 * for WebSocket management and repository interactions. The class also includes error
 * handling mechanisms for transport errors.
 */
@RequiredArgsConstructor
public class AudioWebSocketHandler extends TextWebSocketHandler{

	@Autowired
    private AudioRepository audioRepository;

	List<WebSocketSession>sessions = new LinkedList<WebSocketSession>();
	ConcurrentHashMap<String,WebSocketSession>sessionMap = new ConcurrentHashMap<String,WebSocketSession>();
	final ObjectMapper map1=new ObjectMapper();
	Logger log1=LoggerFactory.getLogger(AudioWebSocketHandler.class);

	@Value("${ws.url}")
	private static String wsUrl;

	private byte[]  partialMessageResponseBuilder = null;

	/**
	 * Processes incoming WebSocket messages, checks if they contain audio data and if
	 * it's a "STOP" command to create an audio file from accumulated partial responses.
	 * It then saves the resulting WAV file to the database.
	 *
	 * @param session WebSocket session of the client making the request, used for sending
	 * messages back to the client.
	 *
	 * Set: WebSocketSession
	 * Properties:
	 * - Local address
	 * - Remote address
	 * - Principal
	 * - Attributes
	 *
	 * @param message TextMessage being received from the WebSocket session, containing
	 * audio data that is processed and handled by the function.
	 *
	 * Extract - message.getPayload(): Returns the payload (the actual data) contained
	 * within the message.
	 * Data type - String.
	 * The main property is: Payload contains an object of Audio class which holds name
	 * and other details of audio file.
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		//log1.info("message.isLast() >>>" + message.isLast());
		//log1.info(message.getPayload());
		Audio audioDataTest = map1.readValue(message.getPayload(), Audio.class);
		//log1.info(audioDataTest.getName());
		if (!audioDataTest.getName().equals("STOP")) {
            if (this.partialMessageResponseBuilder == null) {
                this.partialMessageResponseBuilder = new byte[0];
            }
            this.partialMessageResponseBuilder = joinByteArray(this.partialMessageResponseBuilder, map1.readValue(message.getPayload(), Audio.class).getData());
            return;
        }
		
		try {
            if(audioDataTest.getName().equals("STOP") && this.partialMessageResponseBuilder != null) {
            	//log1.info("Web Service Response : " + this.partialMessageResponseBuilder.append(message.getPayload()).toString());
				log1.info("Audio File now being created");
				//Audio audioData=map1.readValue(this.partialMessageResponseBuilder.toString(), Audio.class);
				//Audio audioResp = new Audio(audioData.getId().toString(), audioData.getData());
				String uuid = UUID.randomUUID().toString();
				float[] sampleRate = {8000,11025,16000,22050,44100,48000};
  				int[] sampleSizeInBits = {8,16,24};
  				int[] channels = {1,2};
  				boolean[] signed = {true, false}; // true,false
  				boolean[] bigEndian = {true, false};
				//for(int i = 0; i < sampleRate.length; i++) {
				//	for(int j = 0; j < sampleSizeInBits.length; j++) {
				//		for(int k = 0; k < channels.length; k++) {
				//			for(int l = 0; l < signed.length; l++) {
				//				for(int m = 0; m < bigEndian.length; m++) {
									
									//Path path = Paths.get("src/main/resources/"+uuid+".mp3");
									//File audioFile = new File("src/main/resources/"+uuid+".mp3");
									
				
				//				}
				//			}
				//		}
				//	}
				//}
				AudioFormat af = new AudioFormat(16000, 16, 1, true, false);
				InputStream targetStream = new ByteArrayInputStream(this.partialMessageResponseBuilder);
				AudioInputStream audioStream = new AudioInputStream(targetStream, af, this.partialMessageResponseBuilder.length);
				AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File(uuid+".wav"));
				File file = new File(uuid+".wav");
				FileInputStream input = new FileInputStream(file);
				MultipartFile multipartFile = new MockMultipartFile("file",
            		file.getName(), "text/plain", IOUtils.toByteArray(input));
				Audio newAud = new Audio(uuid, multipartFile.getBytes());
				AudioFileFormat audioIn = AudioSystem.getAudioFileFormat(file);
				newAud.setAudioDetails(audioIn.toString());
				audioRepository.save(newAud);
				//WaveFileReader reader = new WaveFileReader();
        		
				log1.info(audioIn.toString());
				this.partialMessageResponseBuilder = null;
			} else {
            	log1.info("Web Service Response : " + message.getPayload());
				session.sendMessage(new TextMessage("No File Created"));
            }
            
        } catch (Exception e1) {
        	log1.error("Could not parse Web Service Response payload: "+ message.getPayload(), e1);
        }

	}

	/**
	 * Adds a WebSocket session to a collection, then calls its superclass method with
	 * the established session as an argument. This establishes the session and enables
	 * further actions on it. The added session is tracked for future use.
	 *
	 * @param session WebSocket connection established with a client, which is then stored
	 * and managed by the class instance.
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		sessions.add(session);
		super.afterConnectionEstablished(session);
	}

	/**
	 * Removes a closed WebSocket session from a collection and logs the closure reason.
	 * It then calls the superclass method to complete any necessary cleanup. The session
	 * is effectively ended and cleared after its connection has been closed.
	 *
	 * @param session WebSocket session that has been closed, providing access to its
	 * state and allowing for removal from the sessions collection.
	 *
	 * @param status reason for closing the WebSocket connection, which can be retrieved
	 * using its `getReason()` method.
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		sessions.remove(session);
		log1.info("Connection Closed [" + status.getReason() + "]");
		super.afterConnectionClosed(session, status);
		
	}

	/**
	 * Logs a transport error with an associated exception for a WebSocket session. It
	 * utilizes a logging utility to record the error at level "error" and provides the
	 * exception as context. The error is logged via the `log1.error` method.
	 *
	 * @param session WebSocket connection that has experienced an error and is passed
	 * to facilitate logging and potential further actions related to it.
	 *
	 * @param exception throwable that caused the transport error in the WebSocket connection.
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log1.error("Transport Error", exception);
	}

	/**
	 * Returns a boolean value indicating whether partial messages are supported. In this
	 * implementation, partial messages are not supported, as indicated by the return
	 * value of `false`. This suggests that the component or class implementing this
	 * method only accepts complete or full messages.
	 *
	 * @returns a boolean value indicating it does not support partial messages, always
	 * returning false.
	 */
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * Concatenates two byte arrays into a single array by allocating a new ByteBuffer
	 * with sufficient capacity, writing the first byte array followed by the second, and
	 * then converting the ByteBuffer to an array. The result is a new array containing
	 * all elements from both input arrays.
	 *
	 * @param byte1 first array of bytes that will be concatenated with another array of
	 * bytes, represented by `byte2`.
	 *
	 * @param byte2 2nd byte array to be concatenated with the first one, `byte1`, into
	 * a single byte array.
	 *
	 * @returns a new combined byte array.
	 */
	public static byte[] joinByteArray(byte[] byte1, byte[] byte2) {

        return ByteBuffer.allocate(byte1.length + byte2.length)
                .put(byte1)
                .put(byte2)
                .array();

    }
}

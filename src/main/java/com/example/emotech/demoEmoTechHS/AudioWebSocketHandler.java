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

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		sessions.add(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		sessions.remove(session);
		log1.info("Connection Closed [" + status.getReason() + "]");
		super.afterConnectionClosed(session, status);
		
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log1.error("Transport Error", exception);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	public static byte[] joinByteArray(byte[] byte1, byte[] byte2) {

        return ByteBuffer.allocate(byte1.length + byte2.length)
                .put(byte1)
                .put(byte2)
                .array();

    }
}

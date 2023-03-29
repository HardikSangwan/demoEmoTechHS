package com.example.emotech.demoEmoTechHS;

import com.example.emotech.demoEmoTechHS.service.AudioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class AudioWebSocketConfiguration implements WebSocketConfigurer{

	@Autowired
	private AudioService audioService;

	@Value( "${allowed.origin:*}" )
	private String allowedOrigin;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(audioWebSocketHandler(), "/socket1").setAllowedOrigins(allowedOrigin);
	}

	@Bean
  	public WebSocketHandler audioWebSocketHandler() {
    	return new AudioWebSocketHandler();
  	}
	
}
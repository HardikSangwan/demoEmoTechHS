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

/**
 * Is configured to enable WebSocket functionality in a Spring-based application.
 * It registers a WebSocket handler at the "/socket1" endpoint with origins allowed
 * from a specified configuration value.
 * AudioService is auto-injected into this class.
 */
@Configuration
@EnableWebSocket
public class AudioWebSocketConfiguration implements WebSocketConfigurer{

	@Autowired
	private AudioService audioService;

	@Value( "${allowed.origin:*}" )
	private String allowedOrigin;
	
	/**
	 * Registers a WebSocket handler for audio communication. It adds a handler to the
	 * registry with the path `/socket1`, specifying an allowed origin, and maps it to
	 * the `audioWebSocketHandler`. The handler is enabled for WebSockets originating
	 * from specified domains.
	 *
	 * @param registry WebSocket handler registry, which is used to manage and register
	 * WebSocket handlers with specific URLs.
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(audioWebSocketHandler(), "/socket1").setAllowedOrigins(allowedOrigin);
	}

	/**
	 * Returns an instance of `AudioWebSocketHandler`, which is annotated with `@Bean`.
	 * This annotation indicates that a bean, or a managed object, is being defined. The
	 * returned handler appears to be related to WebSocket communication and audio handling.
	 *
	 * @returns an instance of `AudioWebSocketHandler`.
	 */
	@Bean
  	public WebSocketHandler audioWebSocketHandler() {
    	return new AudioWebSocketHandler();
  	}
	
}
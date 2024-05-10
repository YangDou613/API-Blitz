package org.example.apiblitz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Profile("Producer")
@Service
@Slf4j
public class MessageService {

	private final SimpMessagingTemplate messagingTemplate;

	public MessageService(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@Autowired
	ObjectMapper objectMapper;

	public void sendMessage(String stringMessage) throws JsonProcessingException {

		Message message = objectMapper.readValue(stringMessage, Message.class);

		String category = message.getCategory();

		switch(category) {
			case "APITest":
				messagingTemplate.convertAndSend("/topic/APITest", "API Test Completed!");
				break;
			case "TestCase":
				messagingTemplate.convertAndSend("/topic/TestCase", "Test Case Set Successfully!");
				break;
			case "Collections":
				messagingTemplate.convertAndSend("/topic/Collections", "Collection Test All Successfully!");
				break;
		}
		log.info("Sending message to browser through websocket...");
	}
}

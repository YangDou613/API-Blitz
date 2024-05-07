package org.example.apiblitz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.apiblitz.model.*;
import org.example.apiblitz.service.CollectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

	private final SimpMessagingTemplate messagingTemplate;

	public MessageController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@Autowired
	CollectionsService collectionsService;

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
	}

//	@GetMapping("/send-message")
//	public void sendMessage() {
//		messagingTemplate.convertAndSend("/topic/public", "Hello from the server!");
//	}
}

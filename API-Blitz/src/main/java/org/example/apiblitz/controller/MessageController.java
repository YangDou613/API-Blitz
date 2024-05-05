package org.example.apiblitz.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

	private final SimpMessagingTemplate messagingTemplate;

	public MessageController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@GetMapping("/send-message")
	public void sendMessage() {
		messagingTemplate.convertAndSend("/topic/public", "Hello from the server!");
	}
}

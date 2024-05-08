package org.example.apiblitz.messageBroker;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.controller.MessageService;
import org.springframework.context.annotation.Profile;

@Profile("Producer")
@Getter
@Slf4j
public class Receiver {

	private String receivedMessage;

	private final MessageService messageService;

	public Receiver(MessageService messageService) {
		this.messageService = messageService;
	}

	public void receiveMessage(String message) throws JsonProcessingException {

		this.receivedMessage = message;
		log.info("Received <" + message + ">");
		messageService.sendMessage(message);

	}

	//	public void receiveMessage(String message) {
//		LOGGER.info("Received <" + message + ">");
//		counter.incrementAndGet();
//	}
}

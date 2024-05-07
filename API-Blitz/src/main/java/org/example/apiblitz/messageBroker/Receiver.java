package org.example.apiblitz.messageBroker;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.controller.MessageController;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Slf4j
public class Receiver {

	private String receivedMessage;

	@Autowired
	MessageController messageController;

	public void receiveMessage(String message) throws JsonProcessingException {

		this.receivedMessage = message;
		log.info("Received <" + message + ">");
		messageController.sendMessage(message);

	}

	//	public void receiveMessage(String message) {
//		LOGGER.info("Received <" + message + ">");
//		counter.incrementAndGet();
//	}
}

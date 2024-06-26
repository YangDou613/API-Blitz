package org.example.apiblitz.messageBroker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Profile("Consumer")
@Component
@Slf4j
public class Sender {

	private final StringRedisTemplate stringRedisTemplate;

	@Autowired
	public Sender(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public void sendMessage(String message) {
		log.info("Finish task, and sending message to producer...");
		stringRedisTemplate.convertAndSend("chat", message);
	}
}

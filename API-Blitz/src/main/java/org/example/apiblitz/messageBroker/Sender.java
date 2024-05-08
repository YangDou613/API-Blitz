package org.example.apiblitz.messageBroker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
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

//	@Bean
//	RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
//	                                        MessageListenerAdapter listenerAdapter) {
//
//		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);
//		container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
//
//		return container;
//	}

//	@Bean
//	MessageListenerAdapter messageListenerAdapter(Receiver receiver) {
//		return new MessageListenerAdapter(receiver, "receiveMessage");
//	}

//	@Bean
//	Receiver messageBrokerReceiver() {
//		return new Receiver();
//	}

//	@Bean
//	StringRedisTemplate messageBrokerTemplate(RedisConnectionFactory connectionFactory) {
//		return new StringRedisTemplate(connectionFactory);
//	}

	public void sendMessage(String message) {

//		ApplicationContext ctx = SpringApplication.run(MessagingRedisApplication.class);

//		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
//		Receiver receiver = ctx.getBean(Receiver.class);

//		log.info("Sending message...");
		log.info("Finish task, and sending message to producer...");
		stringRedisTemplate.convertAndSend("chat", message);
//		Thread.sleep(500L);

//		System.exit(0);
	}
}

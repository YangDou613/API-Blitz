package org.example.apiblitz;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.service.ResetTestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ApiBlitzApplication {

	@Autowired
	private ResetTestCaseService resetTestCaseService;

	public static void main(String[] args) {
		SpringApplication.run(ApiBlitzApplication.class, args);
	}

	@PostConstruct
	public void init() {
		try {
			resetTestCaseService.resetTestCase();
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Bean
	public Publisher publisher(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
		return new Publisher(amazonSQSClient, objectMapper);
	}

	@Bean
	StringRedisTemplate messageBrokerTemplate(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

}

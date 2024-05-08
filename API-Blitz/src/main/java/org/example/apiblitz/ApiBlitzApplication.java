package org.example.apiblitz;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.repository.ResetTestCaseRepository;
import org.example.apiblitz.repository.TestCaseRepository;
import org.example.apiblitz.service.AutoTestService;
import org.example.apiblitz.service.ResetTestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ApiBlitzApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
		SpringApplication.run(ApiBlitzApplication.class, args);
	}

//	@Bean
//	StringRedisTemplate messageBrokerTemplate(RedisConnectionFactory connectionFactory) {
//		return new StringRedisTemplate(connectionFactory);
//	}

}

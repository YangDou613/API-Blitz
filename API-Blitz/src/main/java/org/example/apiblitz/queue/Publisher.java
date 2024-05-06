package org.example.apiblitz.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class Publisher {

	@Value("${aws.queueName}")
	private String queueName;

	private final AmazonSQS amazonSQSClient;
	private final ObjectMapper objectMapper;

	public Publisher(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
		this.amazonSQSClient = amazonSQSClient;
		this.objectMapper = objectMapper;
	}

	public void publishMessage(String requestEntity) {
		try {
			GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(queueName);
			var message = Message.builder()
//					.id(id)
//					.content("message")
					.content(requestEntity)
					.createdAt(new Date()).build();
			var result = amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(message));
		} catch (Exception e) {
			log.error("Queue Exception Message: {}", e.getMessage());
		}
	}
}

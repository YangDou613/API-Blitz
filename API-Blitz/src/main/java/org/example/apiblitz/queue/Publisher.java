package org.example.apiblitz.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
@Slf4j
public class Publisher {

	@Value("${aws.queueName}")
	private String queueName;

	private final AmazonSQS amazonSQSClient;

	private final ObjectMapper objectMapper;

	public Publisher(AmazonSQS amazonSQSClient) {
		this.amazonSQSClient = amazonSQSClient;
		this.objectMapper = new ObjectMapper();
	}

//	@Bean
//	public Publisher publisher() {
//		return new Publisher(amazonSQSClient, objectMapper);
//	}

	public void publishMessage(
			Integer userId,
			String category,
			Integer id,
			Timestamp testDateTime,
			Object content
			) {
		try {
			GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(queueName);

			var message = Message.builder()
					.userId(userId)
					.category(category)
					.id(id)
					.testDateTime(testDateTime)
					.content(content)
					.createdAt(new Date()).build();

			amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(message));
			log.info("Sending message to queue: { " + message + "}");

		} catch (Exception e) {
			log.error("Queue Exception Message: { " + e.getMessage() + "}");
		}
	}
}

package org.example.apiblitz.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Consumer {

	@Value("${aws.queueName}")
	private String queueName;

	private final AmazonSQS amazonSQSClient;

	public Consumer(AmazonSQS amazonSQSClient) {
		this.amazonSQSClient = amazonSQSClient;
	}

	@Scheduled(fixedDelay = 5000)
	public void consumeMessages() {
		try {
			String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();

			ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);

			if (!receiveMessageResult.getMessages().isEmpty()) {
				com.amazonaws.services.sqs.model.Message message = receiveMessageResult.getMessages().get(0);
				log.info("Read Message from queue: {}", message.getBody());
				amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
			}

		} catch (Exception e) {
			log.error("Queue Exception Message: {}", e.getMessage());
		}
	}
}

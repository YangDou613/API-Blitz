package org.example.apiblitz.queue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.messageBroker.Sender;
import org.example.apiblitz.model.APIData;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.AutoTestService;
import org.example.apiblitz.service.CollectionsService;
import org.example.apiblitz.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Profile("Consumer")
@Service
@Slf4j
public class Consumer {

	private final AmazonSQS amazonSQSClient;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	APIService apiService;
	@Autowired
	AutoTestService autoTestService;
	@Autowired
	TestCaseService testCaseService;
	@Autowired
	CollectionsService collectionsService;
	@Autowired
	Sender sender;
	@Value("${aws.queueName}")
	private String queueName;

	public Consumer(AmazonSQS amazonSQSClient) {
		this.amazonSQSClient = amazonSQSClient;
	}

	@Scheduled(fixedRate = 200)
	public void consumeMessages() {

		String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();

		try {

			ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);

			if (!receiveMessageResult.getMessages().isEmpty()) {

				com.amazonaws.services.sqs.model.Message message = receiveMessageResult.getMessages().get(0);
				org.example.apiblitz.model.Message messageBody =
						objectMapper.readValue(message.getBody(), org.example.apiblitz.model.Message.class);
				amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());

				log.info("Read Message from queue: { " + messageBody + "}");

				String category = messageBody.getCategory();

				org.example.apiblitz.model.Message messageToReceiver = new org.example.apiblitz.model.Message();
				messageToReceiver.setUserId(messageBody.getUserId());
				messageToReceiver.setCategory(category);
				messageToReceiver.setId(messageBody.getId());
				messageToReceiver.setTestDateTime(messageBody.getTestDateTime());
				messageToReceiver.setCreatedAt(new Date());

				switch (category) {
					case "APITest":
						apiService.APITest(
								messageBody.getUserId(),
								messageBody.getTestDateTime(),
								objectMapper.readValue(objectMapper.writeValueAsString(messageBody.getContent()), APIData.class));
						sender.sendMessage(objectMapper.writeValueAsString(messageToReceiver));
						break;
					case "TestCase":
						autoTestService.automatedTesting(messageBody.getId());
						sender.sendMessage(objectMapper.writeValueAsString(messageToReceiver));
						break;
					case "Collections":
						collectionsService.testCollectionAllApi(
								messageBody.getId(),
								messageBody.getTestDateTime(),
								objectMapper.convertValue(messageBody.getContent(), new TypeReference<>() {
								}));
						sender.sendMessage(objectMapper.writeValueAsString(messageToReceiver));
						break;
				}
			}
		} catch (Exception e) {
			log.error("Queue Exception Message: { " + e.getMessage() + " }");
			throw new RuntimeException(e);
		}
	}
}

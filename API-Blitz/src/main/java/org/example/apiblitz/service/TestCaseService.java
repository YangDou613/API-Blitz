package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.*;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.repository.TestCaseRepository;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TestCaseService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	APIService apiService;

	@Autowired
	TestCaseRepository testCaseRepository;

	@Autowired
	AutoTestService autoTestService;
	@Autowired
	Publisher publisher;
	@Autowired
	private JwtUtil jwtUtil;

	@Profile("Producer")
	public Integer createTestCase(Integer userId, TestCase testCase) throws JsonProcessingException {

		// Set testCase data in APIData
		APIData apiData = APIData.setAPIData(testCase);

		// Package API data into http request
		Request request = apiService.httpRequest(apiData);

		Object expectedResponseBody;

		if (!testCase.getExpectedResponseBody().isEmpty()) {
			if (!isValidJson(testCase.getExpectedResponseBody())) {
				expectedResponseBody = objectMapper.writeValueAsString(testCase.getExpectedResponseBody());
			} else {
				expectedResponseBody = testCase.getExpectedResponseBody();
			}
		} else {
			expectedResponseBody = null;
		}

		// Store API data into APIHistory table and return testCaseId
		return testCaseRepository.insertToTestCaseTable(userId, request, testCase, expectedResponseBody);
	}

	@Profile("Producer")
	public void setTestCaseTestSchedule(Integer userId, Integer testCaseId, TestCase testCase) {

		LocalDate testDate = LocalDate.now(); // Test Date
		LocalTime testTime = LocalTime.now(); // Test time

		// Confirm the test case id exist or not
		boolean testCaseIdExist = testCaseRepository.isTestCaseIdExistInNextTestSchedule(testCaseId);

		// Store to next test schedule table
		if (!testCaseIdExist) {
			testCaseRepository.insertToNextTestSchedule(testCaseId, testDate, testTime);
		}

		// Set test schedule
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Runnable test = () -> {
			try {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				// Confirm the reset status
				Integer resetStatus = testCaseRepository.getResetStatusByTestCaseId(testCaseId);

				if (resetStatus == 0) {

					// Category
					String category = "TestCase";

					// ID
					Integer id = testCaseId;

					// Test dateTime
					LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
					Timestamp testDateTime = Timestamp.valueOf(currentDateTime);

					// Content
					Object content = testCase;

					publisher.publishMessage(userId, category, id, testDateTime, content);

					log.info(timestamp + " : testCaseId <" + testCaseId + "> Set Schedule Successfully!");
				} else {
					executor.shutdown();
					log.info(timestamp + " : testCaseId <" + testCaseId + "> Schedule Shutdown!");
					if (resetStatus == 1) {
						executor.shutdown();
						setTestCaseTestSchedule(userId, testCaseId, testCase);
						testCaseRepository.updateResetStatusByTestCaseId(testCaseId);
						log.info(timestamp + " : testCaseId <" + testCaseId + "> Reset Schedule Successfully!");
					}
				}

				LocalDate nextTestDate = timestamp.toLocalDateTime().toLocalDate();
				LocalTime nextTestTime = timestamp.toLocalDateTime().toLocalTime();

				LocalTime originalTime = nextTestTime;

				switch (testCase.getIntervalsTimeUnit()) {
					case "Hour":
						nextTestTime = nextTestTime.plusHours(testCase.getIntervalsTimeValue());
						if (nextTestTime.isBefore(originalTime)) {
							nextTestDate = nextTestDate.plusDays(1);
						}
						break;
					case "Day":
						nextTestTime = nextTestTime.plusHours(testCase.getIntervalsTimeValue() * 24);
						nextTestDate = nextTestDate.plusDays(testCase.getIntervalsTimeValue());
						break;
					case "Sec":
						nextTestTime = nextTestTime.plusSeconds(testCase.getIntervalsTimeValue());
						if (nextTestTime.isBefore(originalTime)) {
							nextTestDate = nextTestDate.plusDays(1);
						}
						break;
				}

				testCaseRepository.updateNextTestTime(testCaseId, nextTestDate, nextTestTime);

			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		};

		// Get intervals time unit
		String intervalsTimeUnit = testCase.getIntervalsTimeUnit();

		TimeUnit timeUnit = null;

		switch (intervalsTimeUnit) {
			case "Hour":
				timeUnit = TimeUnit.HOURS;
				break;
			case "Day":
				timeUnit = TimeUnit.DAYS;
				break;
			case "Sec":
				timeUnit = TimeUnit.SECONDS;
				break;
		}

		// Get intervals time value
		Integer intervalsTimeValue = testCase.getIntervalsTimeValue();

		executor.scheduleAtFixedRate(test, 0, intervalsTimeValue, timeUnit);
	}

	@Profile("Producer")
	public List<NextSchedule> getTestCase(String accessToken) throws TokenParsingException {

		Integer userId = parseToken(accessToken);

		try {
			return testCaseRepository.getTestCaseList(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Profile("Producer")
	public void updateTestCase(String accessToken, ResetTestCase resetTestCase) throws TokenParsingException {

		Integer userId = parseToken(accessToken);

		try {
			// Set testCase data in APIData
			APIData apiData = APIData.setAPIData(resetTestCase);

			// Package API data into http request
			Request request = apiService.httpRequest(apiData);

			// Update API data in APIHistory table
			testCaseRepository.updateTestCase(userId, request, resetTestCase);
		} catch (SQLException | JsonProcessingException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Producer")
	public void deleteTestCase(Integer testCaseId) {

		// Delete API data in APIHistory table
		try {
			testCaseRepository.deleteTestCase(testCaseId);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Consumer")
	private boolean isValidJson(String responseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.readTree(responseBody);
			return true;
		} catch (JsonProcessingException e) {
			return false;
		}
	}

	private Integer parseToken(String accessToken) throws TokenParsingException {
		Claims claims = jwtUtil.parseToken(accessToken);
		return claims.get("userId", Integer.class);
	}
}

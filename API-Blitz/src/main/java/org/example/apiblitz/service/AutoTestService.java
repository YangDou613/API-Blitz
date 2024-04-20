package org.example.apiblitz.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.repository.AutoTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AutoTestService {

	@Autowired
	AutoTestRepository autoTestRepository;

	@Autowired
	APIService apiService;

	@Autowired
	SendEmailService sendEmailService;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public void autoTest(Integer testCaseId) throws IOException, UnirestException {

		// Test Date
		LocalDate testDate = LocalDate.now();

		// Test time
		LocalTime testTime = LocalTime.now();
		long startTime = System.currentTimeMillis(); // Start time (To calculate the executionDuration)

		// Get API data
		Request request = autoTestRepository.getAPIData(testCaseId);

		// Send Request
		ResponseEntity<?> response = apiService.sendRequest(request);

		// Get response status code
		Integer statusCode = response.getStatusCode().value();

		// Execution duration
		long endTime = System.currentTimeMillis(); // End time (To calculate the execution duration)
		long executionDuration = endTime - startTime;

		// Content length
		long contentLength = response.getHeaders().getContentLength();

		// Response headers
		Object responseHeaders = objectMapper.writeValueAsString(response.getHeaders());

		// Response body
		Object responseBody;
		if (!request.getMethod().equals("HEAD")) {
			responseBody = response.getBody();
		} else {
			responseBody = null;
		}

		// Convert data type to compare
		Map<String, Object> responseBodyMap = objectMapper.readValue(responseBody.toString(), new TypeReference<>() {});

		// Compare response
		String result = getCompareResult(testCaseId, statusCode, responseBodyMap);

		if (result.equals("failed")) {

			// Get recipient email list
			String emailListAsString = autoTestRepository.getRecipientEmailList(testCaseId);
			List<String> recipientEmailList = objectMapper.readValue(emailListAsString, new TypeReference<>() {
			});

			for (Object recipientEmail : recipientEmailList) {
				sendEmailService.sendEmail(recipientEmail, request.getAPIUrl());
			}
		}

		// Insert to testResult
		autoTestRepository.insertToTestResult(
				testCaseId,
				testDate,
				testTime,
				statusCode,
				executionDuration,
				contentLength,
				responseHeaders,
				responseBody,
				result);
	}

	public String getCompareResult(Integer testCaseId,
	                               Integer statusCode,
	                               Map<String, Object> responseBody) throws IOException {

		// Get expected status code
		Integer expectedStatusCode = autoTestRepository.getExpectedStatusCode(testCaseId);

		if (!statusCode.equals(expectedStatusCode)) return "failed";

		// Get expected response body
		String expectedResponseBodyString = autoTestRepository.getExpectedResponseBody(testCaseId);
		Map<String, Object> expectedResponseBody = objectMapper.readValue(expectedResponseBodyString, new TypeReference<>() {});

		if (expectedResponseBody.size() != responseBody.size()) return "failed";

		// Get key list
		Set<String> keys = expectedResponseBody.keySet();

		for (String key : keys) {
			if (!expectedResponseBody.get(key).equals(responseBody.get(key))) {
				return "failed";
			}
		}

		return "pass";
	}
}

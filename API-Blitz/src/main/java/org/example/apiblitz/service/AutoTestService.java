package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.jsonwebtoken.Claims;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.CollectionTestResult;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.TestResult;
import org.example.apiblitz.repository.AutoTestRepository;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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

	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	AutoTestRepository autoTestRepository;
	@Autowired
	APIService apiService;
	@Autowired
	SendEmailService sendEmailService;
	@Autowired
	private JwtUtil jwtUtil;

	public void automatedTesting(Integer testCaseId) throws IOException, UnirestException {

		// Test Date
		LocalDate testDate = LocalDate.now();

		// Test time
		LocalTime testTime = LocalTime.now();

		// Get API data
		Request request = autoTestRepository.getAPIData(testCaseId);

		// Send Request
		ResponseEntity<?> response = apiService.sendRequest(request);

		// Get response status code
		Integer statusCode = response.getStatusCode().value();

		// Response headers
		Object responseHeaders = objectMapper.writeValueAsString(response.getHeaders());

		// Response body
		String result = null;

		String contentType = response.getHeaders().get("Content-type").get(0);

		Object responseBody;
		if (!request.getMethod().equals("HEAD")) {

			if (response.getBody() != null) {
				if (!isValidJson(response.getBody().toString()) || contentType.equals("image/jpeg")) {

					responseBody = objectMapper.writeValueAsString(response.getBody());
					result = getCompareResultForText(testCaseId, statusCode, responseBody);

				} else {
					responseBody = response.getBody();

					// Convert data type to compare
					Map<String, Object> responseBodyMap = objectMapper.readValue(responseBody.toString(), new TypeReference<>() {
					});

					// Compare response
					result = getCompareResultForJson(testCaseId, statusCode, responseBodyMap);
				}
			} else {
				responseBody = null;
			}
		} else {
			responseBody = null;
		}

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
				response.getHeaders().getFirst("Execution-Duration"),
				response.getHeaders().getContentLength(),
				responseHeaders,
				responseBody,
				result);
	}

	public String getCompareResultForText(Integer testCaseId,
	                                      Integer statusCode,
	                                      Object responseBody) {

		// Get expected status code
		Integer expectedStatusCode = autoTestRepository.getExpectedStatusCode(testCaseId);

		if (!statusCode.equals(expectedStatusCode)) return "failed";

		// Get expected response body
		String expectedResponseBodyString = autoTestRepository.getExpectedResponseBody(testCaseId);

		if (expectedResponseBodyString != null) {
			if (!responseBody.equals(expectedResponseBodyString)) return "failed";
		} else {
			if (responseBody != null) return "failed";
		}
		return "pass";
	}

	public String getCompareResultForJson(Integer testCaseId,
	                                      Integer statusCode,
	                                      Map<String, Object> responseBody) throws IOException {

		// Get expected status code
		Integer expectedStatusCode = autoTestRepository.getExpectedStatusCode(testCaseId);

		if (!statusCode.equals(expectedStatusCode)) return "failed";

		// Get expected response body
		String expectedResponseBodyString = autoTestRepository.getExpectedResponseBody(testCaseId);

		Map<String, Object> expectedResponseBody;

		if (expectedResponseBodyString != null) {
			expectedResponseBody = objectMapper.readValue(expectedResponseBodyString, new TypeReference<>() {
			});
			if (expectedResponseBody.size() != responseBody.size()) return "failed";
			Set<String> keys = expectedResponseBody.keySet();

			for (String key : keys) {
				if (!expectedResponseBody.get(key).equals(responseBody.get(key))) {
					return "failed";
				}
			}
		} else {
			if (responseBody != null) return "failed";
		}
		return "pass";
	}

	@Profile("Producer")
	public List<Integer> getTestCaseIdList(String accessToken) throws TokenParsingException {

		Claims claims = jwtUtil.parseToken(accessToken);
		Integer userId = claims.get("userId", Integer.class);

		return autoTestRepository.getTestCaseIdListByUserId(userId);
	}

	@Profile("Producer")
	public Map<String, Object> getTestCaseStartTime(Integer testCaseId) {
		return autoTestRepository.getTestCaseStartTimeByTestCaseId(testCaseId);
	}

	@Profile("Producer")
	public List<TestResult> getTestCaseTestResult(Integer testCaseId) {
		return autoTestRepository.getTestCaseResultByTestCaseId(testCaseId);
	}

	@Profile("Producer")
	public List<TestResult> getTenTestCaseTestResult(Integer testCaseId) {
		return autoTestRepository.getTenTestCaseTestResultListByTestCaseId(testCaseId);
	}

	@Profile("Producer")
	public List<Map<String, Object>> getCollectionTestTime(Integer collectionId) {
		return autoTestRepository.getCollectionTestTimeByCollectionId(collectionId);
	}

	@Profile("Producer")
	public List<CollectionTestResult> getCollectionTestResult(Integer collectionId, LocalDate testDate, LocalTime testTime) {
		return autoTestRepository.getCollectionTestResultByCollectionId(collectionId, testDate, testTime);
	}

	@Profile("Producer")
	public List<CollectionTestResult> getCollectionRetestResult(Integer collectionTestResultId) {
		return autoTestRepository.getCollectionRetestResultByCollectionTestResultId(collectionTestResultId);
	}

	private boolean isValidJson(String responseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.readTree(responseBody);
			return true;
		} catch (JsonProcessingException e) {
			return false;
		}
	}
}

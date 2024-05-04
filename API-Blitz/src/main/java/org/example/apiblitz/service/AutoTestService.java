package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.example.apiblitz.model.CollectionTestResult;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.TestResult;
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

		// Get API data
		Request request = autoTestRepository.getAPIData(testCaseId);

		// Set API url
//		String APIUrl;
//		if (request.getQueryParams() != null) {
//			APIUrl = addParamsToAPIUrl(request.getAPIUrl(), request.getQueryParams());
//		} else {
//			APIUrl = request.getAPIUrl();
//		}
//		request.setAPIUrl(APIUrl);

		// Send Request
		ResponseEntity<?> response = apiService.sendRequest(request);

		// Get response status code
		Integer statusCode = response.getStatusCode().value();

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
				response.getHeaders().getFirst("Execution-Duration"),
				response.getHeaders().getContentLength(),
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

		Map<String, Object> expectedResponseBody;

		if (expectedResponseBodyString != null) {
			expectedResponseBody = objectMapper.readValue(expectedResponseBodyString, new TypeReference<>() {});
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

//		if (expectedResponseBody.size() != responseBody.size()) return "failed";

		// Get key list
//		Set<String> keys = expectedResponseBody.keySet();
//
//		for (String key : keys) {
//			if (!expectedResponseBody.get(key).equals(responseBody.get(key))) {
//				return "failed";
//			}
//		}

		return "pass";
	}

//	public String addParamsToAPIUrl(String APIUrl, Object getQueryParams) throws JsonProcessingException {
//
//		boolean isFirstIteration = true;
//
//		Map<String, Object> queryParams = objectMapper.readValue(getQueryParams.toString(), new TypeReference<>() {});
//
//		for (Map.Entry<String, Object> queryParam : queryParams.entrySet()) {
//			String key = queryParam.getKey();
//			Object value = queryParam.getValue();
//			String param = key + "=" + value;
//			if (isFirstIteration) {
//				APIUrl += "?";
//				isFirstIteration = false;
//			} else {
//				APIUrl += "&";
//			}
//			APIUrl += param;
//		}
//		return APIUrl;
//	}

	public List<Integer> getAllTestCaseId(Integer userId) {
		return autoTestRepository.getAllTestCaseIdByUserId(userId);
	}

	public List<TestResult> getAllTestResult(Integer testCaseId) {
		return autoTestRepository.getAllTestResultByTestCaseId(testCaseId);
	}

	public List<Map<String, Object>> getTestTime(Integer collectionId) {
		return autoTestRepository.getAllTestTime(collectionId);
	}

	public List<CollectionTestResult> collectionOnceTestResult(Integer collectionId, LocalDate testDate, LocalTime testTime) {
		return autoTestRepository.getOnceTestResultByCollectionId(collectionId, testDate, testTime);
	}

	public List<List<TestResult>> collectionAllTestResult(Integer collectionId) {
		return autoTestRepository.getAllTestResultByCollectionId(collectionId);
	}
}

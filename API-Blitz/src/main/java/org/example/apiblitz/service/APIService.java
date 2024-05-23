package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.APIData;
import org.example.apiblitz.model.APITestResult;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.repository.APIRepository;
import org.example.apiblitz.repository.CollectionsRepository;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class APIService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	APIRepository apiRepository;

	@Autowired
	CollectionsRepository collectionsRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Profile("Consumer")
	public void APITest(Integer userId,
	                    Timestamp testDateTime,
	                    APIData apiData) throws JsonProcessingException, InterruptedException {

		ResponseEntity<?> result;

		// Package API data into http request
		Request request = httpRequest(apiData);

		if (request == null) {
			result = ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Failed to parse JSON data. Please check the JSON format and try again.");
		} else {
			// Send Request
			result = sendRequest(request);
		}

		Object responseHeaders = objectMapper.writeValueAsString(result.getHeaders());

		String contentType = result.getHeaders().get("Content-type").get(0);

		Object responseBody;

		if (result.getBody() != null) {

			if (!isValidJson(result.getBody().toString()) || contentType.equals("image/jpeg")) {
				responseBody = objectMapper.writeValueAsString(result.getBody());
			} else {
				responseBody = result.getBody();
			}
		} else {
			responseBody = null;
		}

		Integer statusCode = result.getStatusCode().value();

		// Store API data and response into APIHistory table
		apiRepository.insertToAPIHistory(userId, apiData.getUrl(), request, testDateTime, responseHeaders, responseBody, statusCode);
	}

	public Request httpRequest(APIData apiData) {

		Request request = new Request();

		try {
			// API url
			request.setAPIUrl(apiData.getUrl());

			// Method
			String method = apiData.getMethod();
			request.setMethod(method);

			// Query params
			Object queryParams;
			if (apiData.getParamsKey() == null || apiData.getParamsKey().isEmpty()) {
				queryParams = null;
			} else {
				queryParams = getQueryParams(apiData.getParamsKey(), apiData.getParamsValue());
			}
			request.setQueryParams(queryParams);

			// Request headers
			HttpHeaders headers = setHeaders(apiData);
			Object requestHeaders = objectMapper.writeValueAsString(headers);
			request.setRequestHeaders(requestHeaders);

			// Request Body
			Object requestBody;
			if (apiData.getBody().isEmpty()) {
				requestBody = null;
			} else {
				requestBody = apiData.getBody();
			}
			request.setRequestBody(requestBody);

			return request;
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Profile("Consumer")
	public ResponseEntity<?> sendRequest(Request request) {

		// Test time
		long startTime = System.currentTimeMillis(); // Start time (To calculate the executionDuration)

		try {
			// Method
			String method = request.getMethod();

			// API url
			String url = request.getAPIUrl();

			// Http entity
			HttpEntity<?> requestEntity = getHttpEntity(request);

			// Send Request
			ResponseEntity<?> response = switch (method) {
				case "GET", "HEAD" -> restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
				case "POST" -> restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
				case "PUT" -> restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
				case "DELETE" -> restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
				case "PATCH" -> restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
				case "OPTIONS" -> restTemplate.exchange(url, HttpMethod.OPTIONS, requestEntity, String.class);
				default -> null;
			};

			// Execution duration
			long endTime = System.currentTimeMillis(); // End time (To calculate the execution duration)
			long executionDuration = endTime - startTime;

			HttpHeaders headers = response.getHeaders();

			// Create a new HttpHeaders object
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.putAll(headers);

			if (headers.getContentLength() == -1) {

				// Calculate response size
				Object body = response.getBody();
				int headerSize = headers.toString().getBytes().length;
				int bodySize = body != null ? body.toString().length() : 0;
				int totalSize = headerSize + bodySize;
				newHeaders.set("Content-Length", String.valueOf(totalSize));
			}

			newHeaders.set("Execution-Duration", String.valueOf(executionDuration));

			return new ResponseEntity<>(response.getBody(), newHeaders, response.getStatusCode());

		} catch (HttpClientErrorException | HttpServerErrorException e) {

			// Execution duration
			long endTime = System.currentTimeMillis(); // End time (To calculate the execution duration)
			long executionDuration = endTime - startTime;

			// Create a new HttpHeaders object
			HttpHeaders newHeaders = new HttpHeaders();

			// Calculate response size
			int body = e.getMessage().length();
			newHeaders.set("Content-Length", String.valueOf(body));

			newHeaders.set("Execution-Duration", String.valueOf(executionDuration));

			return new ResponseEntity<>(e.getResponseBodyAsString(), newHeaders, e.getStatusCode());
		}
	}

	@Profile("Consumer")
	public String getQueryParams(ArrayList<Object> paramsKey, ArrayList<Object> paramsValue)
			throws JsonProcessingException {

		Map<Object, Object> queryParams = new HashMap<>();
		for (int i = 0; i < paramsKey.size(); i++) {
			if (!paramsKey.get(i).equals("") && !paramsValue.get(i).equals("")) {
				queryParams.put(paramsKey.get(i), paramsValue.get(i));
			}
		}
		return objectMapper.writeValueAsString(queryParams);
	}

	@Profile("Consumer")
	public HttpHeaders setHeaders(APIData apiData) {

		// Header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (apiData.getHeadersKey() != null) {
			for (int i = 0; i < apiData.getHeadersKey().size(); i++) {
				headers.set(apiData.getHeadersKey().get(i), apiData.getHeadersValue().get(i));
			}
		}

		// Authorization
		if (!apiData.getAuthorizationKey().equals("No Auth")) {
			headers.set("Authorization", apiData.getAuthorizationKey() + " " + apiData.getAuthorizationValue());
		}
		return headers;
	}

	@Profile("Consumer")
	public HttpEntity<?> getHttpEntity(Request request) {
		MultiValueMap<String, String> requestHeaders = convertJsonToMultiValueMap((String) request.getRequestHeaders());
		return new HttpEntity<>(request.getRequestBody(), requestHeaders);
	}

	@Profile("Consumer")
	public MultiValueMap<String, String> convertJsonToMultiValueMap(String headers) {

		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(headers);

			jsonNode.fields().forEachRemaining(entry -> {
				String key = entry.getKey();
				JsonNode valueNode = entry.getValue();
				if (valueNode.isArray()) {
					valueNode.elements().forEachRemaining(val -> requestHeaders.add(key, val.asText()));
				} else {
					requestHeaders.add(key, valueNode.asText());
				}
			});
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return requestHeaders;
	}

	public APITestResult getApiTestResult(String accessToken, String testDateTime) throws TokenParsingException {
		Integer userId = parseToken(accessToken);
		return apiRepository.getApiTestResultByUserIdAndTestDateTime(userId, testDateTime);
	}

	public List<Request> getApiTestHistory(String accessToken) throws TokenParsingException {
		Integer userId = parseToken(accessToken);
		return apiRepository.getHistoryList(userId);
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

	public Integer parseToken(String accessToken) throws TokenParsingException {
		Claims claims = jwtUtil.parseToken(accessToken);
		return claims.get("userId", Integer.class);
	}
}

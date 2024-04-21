package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.APIData;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.repository.APIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class APIService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	APIRepository apiRepository;

	public ResponseEntity<?> APITest(APIData apiData) {

		// Package API data into http request
		Request request = httpRequest(apiData);
		if (request == null) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Failed to parse JSON data. Please check the JSON format and try again.");
		}

		// Store API data into APIHistory table
		apiRepository.insertToAPIHistory(request);

		// Send Request
		ResponseEntity<?> result = sendRequest(request);

		return setResponse(apiData, result);
	}

	public ResponseEntity<?> setResponse(APIData apiData, ResponseEntity<?> result) {

		// Get status code
		HttpStatusCode statusCode = result.getStatusCode();

		if (apiData.getMethod().equals("HEAD")) {
			HttpHeaders headers = result.getHeaders();
			String headersString = headers.entrySet().stream()
					.map(entry -> entry.getKey() + ": " + entry.getValue())
					.collect(Collectors.joining("\n"));
			return ResponseEntity
					.status(statusCode)
					.body(headersString);
		}
		return ResponseEntity
				.status(statusCode)
				.body(result);
	}

	public Request httpRequest(APIData apiData) {

		Request request = new Request();

		try {
			// API url
			String APIUrl;
			if (apiData.getParamsKey() != null) {
				APIUrl = AddParamsToAPIUrl(apiData.getUrl(), apiData.getParamsKey(), apiData.getParamsValue());
			} else {
				APIUrl = apiData.getUrl();
			}
			request.setAPIUrl(APIUrl);

			// Method
			String method = apiData.getMethod();
			request.setMethod(method);

			// Query params
			Object queryParams;
			if (apiData.getParamsKey().isEmpty()) {
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
			ResponseEntity<?> response =  switch (method) {
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

	public String AddParamsToAPIUrl(String APIUrl, ArrayList<Object> paramsKey, ArrayList<Object> paramsValue) {

		for (int i = 0; i < paramsKey.size(); i++) {
			String param = paramsKey.get(i) + "=" + paramsValue.get(i);
			if (i == 0) {
				APIUrl += "?";
			} else {
				APIUrl += "&";
			}
			APIUrl += param;
		}
		return APIUrl;
	}

	public String getQueryParams(ArrayList<Object> paramsKey, ArrayList<Object> paramsValue)
			throws JsonProcessingException {

		Map<Object, Object> queryParams = new HashMap<>();
		for (int i = 0; i < paramsKey.size(); i++) {
			queryParams.put(paramsKey.get(i), paramsValue.get(i));
		}
		return objectMapper.writeValueAsString(queryParams);
	}

	public HttpHeaders setHeaders(APIData apiData) {

		// Header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (!apiData.getHeadersKey().isEmpty()) {
			for (int i = 0; i < apiData.getHeadersKey().size(); i++) {
				headers.set(apiData.getHeadersKey().get(i), apiData.getHeadersValue().get(i));
			}
		}

		// Authorization
		if (!apiData.getAuthorizationKey().isEmpty()) {
			headers.set("Authorization", apiData.getAuthorizationKey() + " " + apiData.getAuthorizationValue());
		}

		return headers;
	}

	public HttpEntity<?> getHttpEntity(Request request) {

		MultiValueMap<String, String> requestHeaders = convertJsonToMultiValueMap((String) request.getRequestHeaders());

		return new HttpEntity<>(request.getRequestBody(), requestHeaders);
	}

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
}

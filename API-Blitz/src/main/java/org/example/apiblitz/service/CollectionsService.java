package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.APIData;
import org.example.apiblitz.model.Collections;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.repository.CollectionsRepository;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class CollectionsService {

	private static final long INITIAL_DELAY_MS = 60000;
	private static final double BACKOFF_MULTIPLIER = 2.0;
	@Autowired
	APIService apiService;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	CollectionsRepository collectionsRepository;
	@Autowired
	Publisher publisher;
	@Autowired
	private JwtUtil jwtUtil;

	@Profile("Producer")
	public List<Map<String, Object>> getCollections(String accessToken) throws TokenParsingException {

		Integer userId = parseToken(accessToken);

		try {
			return collectionsRepository.getCollectionsList(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Profile("Producer")
	public void createCollection(String accessToken, Collections collection) throws TokenParsingException {

		Integer userId = parseToken(accessToken);

		try {
			collectionsRepository.insertToCollectionsTable(userId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Producer")
	public void addApiToCollection(Integer collectionId, Collections collection) {

		// Package API data into http request
		APIData apiData = setAPIData(collection);
		Request request = apiService.httpRequest(apiData);

		collection.setRequest(request);

		try {
			collectionsRepository.insertToCollectionDetailsTable(collectionId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Producer")
	public void updateCollection(Integer collectionId, Collections collection) {

		if (collection.getApiurl() != null) {
			// Package API data into http request
			APIData apiData = setAPIData(collection);
			Request request = apiService.httpRequest(apiData);
			collection.setRequest(request);
		}

		try {
			collectionsRepository.updateCollection(collectionId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Producer")
	public void deleteCollection(String accessToken, String collectionName, Integer requestId) throws TokenParsingException {

		Integer userId = parseToken(accessToken);

		try {
			collectionsRepository.deleteCollection(userId, collectionName, requestId);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Consumer")
	public void testCollectionAllApi(Integer collectionId, Timestamp testDateTime, List<Request> requests) {

		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		List<Callable<Map.Entry<Integer, ResponseEntity<?>>>> callables = new ArrayList<>();

		LocalDateTime parsedDateTime = testDateTime.toLocalDateTime();
		LocalDate testDate = parsedDateTime.toLocalDate();
		LocalTime testTime = parsedDateTime.toLocalTime();

		try {
			for (Request request : requests) {

				if (request.getBody() != null) {
					request.setRequestBody(objectMapper.readValue(request.getBody(), Object.class));
				} else {
					request.setRequestBody(null);
				}

				// Header
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				Object requestHeaders = objectMapper.writeValueAsString(headers);
				request.setRequestHeaders(requestHeaders);

				Integer collectionDetailsId = request.getId();

				callables.add(() -> {
					String threadName = Thread.currentThread().getName();
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					log.info(timestamp + "  Sending request in thread: " + threadName);
					ResponseEntity<?> responseEntity = apiService.sendRequest(request);
					return new AbstractMap.SimpleEntry<>(collectionDetailsId, responseEntity);
				});
			}

			List<Future<Map.Entry<Integer, ResponseEntity<?>>>> futures = cachedThreadPool.invokeAll(callables);

			List<ResponseEntity<?>> responseList = new ArrayList<>();
			for (Future<Map.Entry<Integer, ResponseEntity<?>>> future : futures) {
				Map.Entry<Integer, ResponseEntity<?>> entry = future.get();
				Integer collectionDetailsId = entry.getKey();
				ResponseEntity<?> responseEntity = entry.getValue();

				Object responseHeaders = objectMapper.writeValueAsString(responseEntity.getHeaders());

				Object responseBody;

				if (!isValidJson(objectMapper.writeValueAsString(responseEntity.getBody()))) {
					responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
				} else {
					responseBody = responseEntity.getBody();
				}

				Integer collectionTestResultId = collectionsRepository.insertToCollectionTestResult(
						collectionId, collectionDetailsId, testDate, testTime, responseHeaders, responseBody, responseEntity);

				if (!responseEntity.getStatusCode().is2xxSuccessful()) {
					CompletableFuture.runAsync(() -> {
						retest(collectionDetailsId, collectionTestResultId);
					});
				}
				responseList.add(responseEntity);
			}
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			log.error(e.getMessage());
		} finally {
			cachedThreadPool.shutdown();
		}
	}

	@Profile("Consumer")
	public void retest(Integer collectionDetailsId, Integer collectionTestResultId) {

		long delay = INITIAL_DELAY_MS;

		try {
			// Retest 3 times
			for (int i = 1; i <= 3; i++) {

				// Get API data from collectionTestResultId
				Request request = collectionsRepository.getAPIDataFromCollectionDetailsId(collectionDetailsId);

				if (request.getBody() != null) {
					request.setRequestBody(objectMapper.readValue(request.getBody(), Object.class));
				}

				// Header
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				Object requestHeaders = objectMapper.writeValueAsString(headers);
				request.setRequestHeaders(requestHeaders);

				Thread.sleep(delay);

				// Test Date
				LocalDate testDate = LocalDate.now();

				// Test time
				LocalTime testTime = LocalTime.now();

				ResponseEntity<?> responseEntity = apiService.sendRequest(request);
				collectionsRepository.insertToCollectionTestResultException(
						collectionTestResultId, testDate, testTime, responseEntity);

				delay = (long) (INITIAL_DELAY_MS * Math.pow(BACKOFF_MULTIPLIER, i));
			}
			log.info("Collection Test Result ID: " + collectionTestResultId + " Retest Finished!");
		} catch (JsonProcessingException | InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	@Profile("Producer")
	public List<Request> getCollectionAllApi(Integer collectionId) {
		try {
			return collectionsRepository.getCollectionAllApiList(collectionId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Profile("Consumer")
	public APIData setAPIData(Collections collection) {

		APIData apiData = new APIData();

		apiData.setMethod(collection.getMethod());
		if (collection.getApiurl() != null) {
			apiData.setUrl(collection.getApiurl());
		} else {
			apiData.setUrl(collection.getUrl());
		}
		apiData.setParamsKey(collection.getParamsKey());
		apiData.setParamsValue(collection.getParamsValue());
		if (collection.getAuthorizationKey() == null) {
			apiData.setAuthorizationKey("No Auth");
		} else {
			apiData.setAuthorizationKey(collection.getAuthorizationKey());
		}
		apiData.setAuthorizationValue(collection.getAuthorizationValue());
		apiData.setHeadersKey(collection.getHeadersKey());
		apiData.setHeadersValue(collection.getHeadersValue());
		if (collection.getBody() == null) {
			apiData.setBody("");
		} else {
			apiData.setBody(collection.getBody());
		}
		return apiData;
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

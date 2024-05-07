package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.model.Collections;
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
import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
public class CollectionsService {

	@Autowired
	APIService apiService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CollectionsRepository collectionsRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	Publisher publisher;

	private static final long INITIAL_DELAY_MS = 60000;
	private static final double BACKOFF_MULTIPLIER = 2.0;

//	@Profile("Producer")
	public List<Map<String, Object>> get(String accessToken) {

		Claims claims = jwtUtil.parseToken(accessToken);
		Integer userId = claims.get("userId", Integer.class);

		try {
			return collectionsRepository.getCollectionsList(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

//	@Profile("Consumer")
	public void create(String accessToken, Collections collection) {

		Claims claims = jwtUtil.parseToken(accessToken);
		Integer userId = claims.get("userId", Integer.class);

		try {
			collectionsRepository.insertToCollectionsTable(userId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

//	@Profile("Consumer")
	public void update(Integer collectionId, Collections collection) {

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

//	@Profile("Consumer")
	public void add(Integer collectionId, Collections collection) {

//		Request request = new Request();

		// Package API data into http request
//		if (collection.getParamsKey() != null) {
//			APIData apiData = setAPIData(collection);
//			request = apiService.httpRequest(apiData);
//		} else {
//			request.setAPIUrl(collection.getApiurl());
//			request.setMethod(collection.getMethod());
//			request.setQueryParams(collection.getQueryParams());
//			request.setHeaders(collection.getHeaders());
//			request.setBody(collection.getBody());
//		}

		// Package API data into http request
		APIData apiData = setAPIData(collection);
		Request request = apiService.httpRequest(apiData);

		collection.setRequest(request);

		try {
			collectionsRepository.addAPIToCollection(collectionId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

//	@Profile("Consumer")
	public void delete(String accessToken, String collectionName, Integer requestId) {

		Claims claims = jwtUtil.parseToken(accessToken);
		Integer userId = claims.get("userId", Integer.class);

		try {
			collectionsRepository.deleteCollection(userId, collectionName, requestId);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

//	@Profile("Consumer")
	public void sendRequestAtSameTime(Integer collectionId, Timestamp testDateTime, List<Request> requests) {

//		Map<String, Object> collectionTestTime = new HashMap<>();

		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		List<Callable<Map.Entry<Integer, ResponseEntity<?>>>> callables = new ArrayList<>();

		LocalDateTime parsedDateTime = testDateTime.toLocalDateTime();
		LocalDate testDate = parsedDateTime.toLocalDate();
		LocalTime testTime = parsedDateTime.toLocalTime();

//		// Test Date
//		LocalDate testDate = LocalDate.now();
//		collectionTestTime.put("testDate", testDate);
//
//		// Test time
//		LocalTime testTime = LocalTime.now();
//		collectionTestTime.put("testTime", testTime);

		try {
			for (Request request : requests) {

//				if (request.getQueryParams() != null) {
//					request.setAPIUrl(apiService.addParams(request.getAPIUrl(), request.getQueryParams()));
//				}

				if (request.getBody() != null) {
					request.setRequestBody(objectMapper.readValue(request.getBody(), Object.class));
//					request.setRequestBody(request.getBody());
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

				if (isValidJson(objectMapper.writeValueAsString(responseEntity.getBody()))) {
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
//			return collectionTestTime;
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			log.error(e.getMessage());
		} finally {
			cachedThreadPool.shutdown();
		}
	}

//	public List<ResponseEntity<?>> sendRequestAtSameTime(Integer collectionId, List<Request> requests) {
//
//		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//		List<Callable<Map.Entry<Integer, ResponseEntity<?>>>> callables = new ArrayList<>();
//
//		// Test Date
//		LocalDate testDate = LocalDate.now();
//
//		// Test time
//		LocalTime testTime = LocalTime.now();
//
//		try {
//			for (Request request : requests) {
//
////				if (request.getQueryParams() != null) {
////					request.setAPIUrl(apiService.addParams(request.getAPIUrl(), request.getQueryParams()));
////				}
//
//				if (request.getBody() != null) {
//					request.setRequestBody(objectMapper.readValue(request.getBody(), Object.class));
//				}
//
//				// Header
//				HttpHeaders headers = new HttpHeaders();
//				headers.setContentType(MediaType.APPLICATION_JSON);
//				Object requestHeaders = objectMapper.writeValueAsString(headers);
//				request.setRequestHeaders(requestHeaders);
//
//				Integer collectionDetailsId = request.getId();
//
//				callables.add(() -> {
//					String threadName = Thread.currentThread().getName();
//					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//					log.info(timestamp + "  Sending request in thread: " + threadName);
//					ResponseEntity<?> responseEntity = apiService.sendRequest(request);
//					return new AbstractMap.SimpleEntry<>(collectionDetailsId, responseEntity);
//				});
//			}
//
//			List<Future<Map.Entry<Integer, ResponseEntity<?>>>> futures = cachedThreadPool.invokeAll(callables);
//
//			List<ResponseEntity<?>> responseList = new ArrayList<>();
//			for (Future<Map.Entry<Integer, ResponseEntity<?>>> future : futures) {
//				Map.Entry<Integer, ResponseEntity<?>> entry = future.get();
//				Integer collectionDetailsId = entry.getKey();
//				ResponseEntity<?> responseEntity = entry.getValue();
//				Integer collectionTestResultId = collectionsRepository.insertToCollectionTestResult(
//						collectionId, collectionDetailsId, testDate, testTime, responseEntity);
//				if (!responseEntity.getStatusCode().is2xxSuccessful()) {
//					CompletableFuture.runAsync(() -> {
//						retest(collectionDetailsId, collectionTestResultId);
//					});
//				}
//				responseList.add(responseEntity);
//			}
//			return responseList;
//		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
//			log.error(e.getMessage());
//			return null;
//		} finally {
//			cachedThreadPool.shutdown();
//		}
//	}

//	@Profile("Consumer")
	public void retest(Integer collectionDetailsId, Integer collectionTestResultId) {

		long delay = INITIAL_DELAY_MS;

		try {
			// Retest 3 times
			for (int i = 1; i <= 3; i++) {

				// Get API data from collectionTestResultId
				Request request = collectionsRepository.getAPIDataFromCollectionDetailsId(collectionDetailsId);

//				if (request.getQueryParams() != null) {
//					request.setAPIUrl(apiService.addParams(request.getAPIUrl(), request.getQueryParams()));
//				}

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

//	@Profile("Producer")
	public List<Request> getAPIList(Integer collectionId) {

		try {
			return collectionsRepository.getAllAPIFromCollection(collectionId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

//	@Profile("Consumer")
	public APIData setAPIData(Collections collection) {

		APIData apiData = new APIData();

		apiData.setMethod(collection.getMethod());
		if (collection.getApiurl() != null) {
			apiData.setUrl(collection.getApiurl());
		} else {
			apiData.setUrl(collection.getUrl());
		}
//		apiData.setUrl(collection.getApiurl());
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

package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.repository.CollectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	private static final long INITIAL_DELAY_MS = 60000;
	private static final double BACKOFF_MULTIPLIER = 2.0;

	public List<Map<String, Object>> get(Integer userId) {

		try {
			return collectionsRepository.getCollectionsList(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public void create(Integer userId, Collections collection) {

		try {
			collectionsRepository.insertToCollectionsTable(userId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

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

	public void delete(Integer userId, String collectionName, Integer requestId) {

		try {
			collectionsRepository.deleteCollection(userId, collectionName, requestId);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public List<ResponseEntity<?>> sendRequestAtSameTime(Integer collectionId, List<Request> requests) {

		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		List<Callable<Map.Entry<Integer, ResponseEntity<?>>>> callables = new ArrayList<>();

		// Test Date
		LocalDate testDate = LocalDate.now();

		// Test time
		LocalTime testTime = LocalTime.now();

		try {
			for (Request request : requests) {

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
				Integer collectionTestResultId = collectionsRepository.insertToCollectionTestResult(
						collectionId, collectionDetailsId, testDate, testTime, responseEntity);
				if (!responseEntity.getStatusCode().is2xxSuccessful()) {
					CompletableFuture.runAsync(() -> {
						retest(collectionDetailsId, collectionTestResultId);
					});
				}
				responseList.add(responseEntity);
			}
			return responseList;
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			log.error(e.getMessage());
			return null;
		} finally {
			cachedThreadPool.shutdown();
		}
	}

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

	public List<Request> getAPIList(Integer collectionId) {

		try {
			return collectionsRepository.getAllAPIFromCollection(collectionId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public APIData setAPIData(Collections collection) {

		APIData apiData = new APIData();

		apiData.setMethod(collection.getMethod());
		apiData.setUrl(collection.getUrl());
		apiData.setParamsKey(collection.getParamsKey());
		apiData.setParamsValue(collection.getParamsValue());
		apiData.setAuthorizationKey(collection.getAuthorizationKey());
		apiData.setAuthorizationValue(collection.getAuthorizationValue());
		apiData.setHeadersKey(collection.getHeadersKey());
		apiData.setHeadersValue(collection.getHeadersValue());
		apiData.setBody(collection.getBody());

		return apiData;
	}
}

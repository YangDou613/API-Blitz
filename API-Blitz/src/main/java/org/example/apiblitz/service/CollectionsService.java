package org.example.apiblitz.service;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.repository.CollectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CollectionsService {

	@Autowired
	APIService apiService;

	@Autowired
	CollectionsRepository collectionRepository;

	public List<Map<String, Object>> get(Integer userId) {

		try {
			return collectionRepository.getCollectionsList(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public void create(Integer userId, Collections collection) {

		try {
			collectionRepository.insertToCollectionsTable(userId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public void update(Integer collectionId, Collections collection) {

		// Package API data into http request
		APIData apiData = setAPIData(collection);
		Request request = apiService.httpRequest(apiData);
		collection.setRequest(request);

		try {
			collectionRepository.updateCollection(collectionId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public void add(Integer collectionId, Collections collection) {

		Request request = new Request();

		// Package API data into http request
		if (collection.getParamsKey() != null) {
			APIData apiData = setAPIData(collection);
			request = apiService.httpRequest(apiData);
		} else {
			request.setAPIUrl(collection.getApiurl());
			request.setMethod(collection.getMethod());
			request.setQueryParams(collection.getQueryParams());
			request.setHeaders(collection.getHeaders());
			request.setBody(collection.getBody());
		}

		collection.setRequest(request);

		try {
			collectionRepository.addAPIToCollection(collectionId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public void delete(Integer userId, String collectionName, Integer requestId) {

		try {
			collectionRepository.deleteCollection(userId, collectionName, requestId);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public List<Request> getAPIList(Integer collectionId) {

		try {
			return collectionRepository.getAllAPIFromCollection(collectionId);
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

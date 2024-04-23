package org.example.apiblitz.service;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CollectionService {

	@Autowired
	APIService apiService;

	@Autowired
	CollectionRepository collectionRepository;

	public List<Map<String, Object>> get(Integer userId) {

		try {
			return collectionRepository.getCollectionsList(userId);
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public void create(Integer userId, Collection collection) {

		// Package API data into http request
		APIData apiData = setAPIData(collection);
		Request request = apiService.httpRequest(apiData);
		collection.setRequest(request);

		try {
			collectionRepository.insertToCollectionsTable(userId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public void update(Integer collectionId, Collection collection) {

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

	public void add(Integer collectionId, Collection collection) {

		// Package API data into http request
		APIData apiData = setAPIData(collection);
		Request request = apiService.httpRequest(apiData);
		collection.setRequest(request);

		try {
			collectionRepository.addAPIToCollection(collectionId, collection);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public void delete(Integer userId, String collectionName, String requestName) {

		try {
			collectionRepository.deleteCollection(userId, collectionName, requestName);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public APIData setAPIData(Collection collection) {

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

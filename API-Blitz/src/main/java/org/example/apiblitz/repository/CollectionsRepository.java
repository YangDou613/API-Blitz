package org.example.apiblitz.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Collections;
import org.example.apiblitz.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@Transactional
public class CollectionsRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ObjectMapper objectMapper;

	public List<Map<String, Object>> getCollectionsList(Integer userId) throws SQLException {

		String getCollectionSql = "SELECT *, (" +
				"SELECT JSON_ARRAYAGG(" +
				"JSON_OBJECT(" +
				"'id', id," +
				"'requestName', requestName," +
				"'apiurl', apiurl," +
				"'method', method," +
				"'queryParams', IFNULL(queryParams, 'null')," +
				"'headers', IFNULL(headers, 'null')," +
				"'body', IFNULL(body, 'null'))) FROM collectionDetails WHERE collectionId = collections.id) " +
				"AS collectionDetails FROM collections WHERE userId = ?";

		return jdbcTemplate.queryForList(getCollectionSql, userId);
	}

	public void insertToCollectionsTable(Integer userId, Collections collection) throws SQLException {

		String insertToCollectionTableSql = "INSERT INTO collections (collectionName, description, userId) " +
				"VALUES (?, ?, ?)";

		jdbcTemplate.update(insertToCollectionTableSql,
				collection.getCollectionName(),
				collection.getDescription(),
				userId);

		log.info("Successfully insert to collections table!");
	}

	public void addAPIToCollection(Integer collectionId, Collections collection) throws SQLException {

		String addAPISql = "INSERT INTO collectionDetails (collectionId, requestName, APIUrl, method, " +
				"queryParams, headers, body) VALUES (?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(addAPISql,
				collectionId,
				collection.getRequestName(),
				collection.getRequest().getAPIUrl(),
				collection.getRequest().getMethod(),
				collection.getRequest().getQueryParams(),
				collection.getRequest().getHeaders(),
				collection.getRequest().getBody());

		log.info("Successfully added API to the collection!");
	}

	public void updateCollection(Integer collectionId, Collections collection) throws SQLException {

		String updateCollectionSql = "UPDATE collections SET collectionName = ?, description = ? WHERE id = ?";
		jdbcTemplate.update(updateCollectionSql,
				collection.getCollectionName(),
				collection.getDescription());

		String updateCollectionDetailsSql = "UPDATE collectionDetails SET requestName = ?, APIUrl = ?, method = ?," +
				"queryParams = ?, headers = ?, body = ? WHERE collectionId = ?";
		jdbcTemplate.update(updateCollectionDetailsSql,
				collection.getRequestName(),
				collection.getRequest().getAPIUrl(),
				collection.getRequest().getMethod(),
				collection.getRequest().getQueryParams(),
				collection.getRequest().getHeaders(),
				collection.getRequest().getBody(),
				collectionId);

		log.info("Update successfully!");
	}

	public void deleteCollection(Integer userId, String collectionName, Integer requestId) throws SQLException {

		// Get collection id
		String getCollectionIdSql = "SELECT id FROM collections WHERE collectionName = ? AND userId = ?";
		Integer collectionId = jdbcTemplate.queryForObject(getCollectionIdSql, Integer.class, collectionName, userId);

		if (requestId == null) {
			String deleteCollectionSql = "DELETE FROM collections WHERE collectionName = ? AND userId = ?";
			jdbcTemplate.update(deleteCollectionSql, collectionName, userId);
		} else {
			String deleteCollectionDetailsSql = "DELETE FROM collectionDetails WHERE collectionId = ? AND id = ?";
			jdbcTemplate.update(deleteCollectionDetailsSql, collectionId, requestId);
		}

		log.info("Delete successfully!");
	}

	public List<Request> getAllAPIFromCollection(Integer collectionId) throws SQLException {

		String getCollectionSql = "SELECT apiurl, method, queryParams, headers, body " +
				"FROM collectionDetails WHERE collectionId = ?";

		return jdbcTemplate.query(getCollectionSql, new BeanPropertyRowMapper<>(Request.class), collectionId);
	}

	public void insertToCollectionTestResult(Integer collectionId,
	                                         Integer collectionDetailsId,
	                                         LocalDate testDate,
	                                         LocalTime testTime,
	                                         ResponseEntity<?> responseEntity) throws JsonProcessingException {

		String result = "";
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			result = "pass";
		} else {
			result = "failed";
		}

		String insertToCollectionTestResultSql = "INSERT INTO collectionTestResult (" +
				"collectionId, collectionDetailsId, testOptions, testDate, testTime, statusCode, " +
				"executionDuration, contentLength, responseHeaders, responseBody, result) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(insertToCollectionTestResultSql,
				collectionId,
				collectionDetailsId,
				"Run all",
				testDate,
				testTime,
				responseEntity.getStatusCode().value(),
				responseEntity.getHeaders().getFirst("Execution-Duration"),
				responseEntity.getHeaders().getContentLength(),
				objectMapper.writeValueAsString(responseEntity.getHeaders()),
				responseEntity.getBody(),
				result);

		log.info("Successfully insert to collectionTestResult table!");
	}
}

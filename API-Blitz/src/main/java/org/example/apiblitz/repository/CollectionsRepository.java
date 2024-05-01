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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
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
				collection.getDescription(),
				collection.getCollectionId());

		if (collection.getApiurl() != null) {

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
		}

		log.info("Update successfully!");
	}

	public void deleteCollection(Integer userId, String collectionName, Integer requestId) throws SQLException {

		if (requestId == null) {
			String deleteCollectionSql = "DELETE FROM collections WHERE collectionName = ? AND userId = ?";
			jdbcTemplate.update(deleteCollectionSql, collectionName, userId);
		} else {
			String deleteCollectionDetailsSql = "DELETE FROM collectionDetails WHERE id = ?";
			jdbcTemplate.update(deleteCollectionDetailsSql, requestId);
		}

		log.info("Delete successfully!");
	}

	public List<Request> getAllAPIFromCollection(Integer collectionId) throws SQLException {

		String getAllAPISql = "SELECT id, requestName, apiurl, method, queryParams, headers, body " +
				"FROM collectionDetails WHERE collectionId = ?";

		return jdbcTemplate.query(getAllAPISql, new BeanPropertyRowMapper<>(Request.class), collectionId);
	}

	public Integer insertToCollectionTestResult(Integer collectionId,
	                                         Integer collectionDetailsId,
	                                         LocalDate testDate,
	                                         LocalTime testTime,
	                                         ResponseEntity<?> responseEntity) {

		String result = responseEntity.getStatusCode().is2xxSuccessful() ? "pass" : "failed";

		String insertToCollectionTestResultSql = "INSERT INTO collectionTestResult (" +
				"collectionId, collectionDetailsId, testOptions, testDate, testTime, statusCode, " +
				"executionDuration, contentLength, responseHeaders, responseBody, result) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(
					insertToCollectionTestResultSql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, collectionId);
			ps.setInt(2, collectionDetailsId);
			ps.setString(3, "Run all");
			ps.setDate(4, Date.valueOf(testDate));
			ps.setTime(5, Time.valueOf(testTime));
			ps.setInt(6, responseEntity.getStatusCode().value());
			ps.setString(7, responseEntity.getHeaders().getFirst("Execution-Duration"));
			ps.setLong(8, responseEntity.getHeaders().getContentLength());
			try {
				ps.setObject(9, objectMapper.writeValueAsString(responseEntity.getHeaders()));
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
				throw new RuntimeException(e);
			}
			ps.setObject(10, responseEntity.getBody());
			ps.setString(11, result);
			return ps;
		}, keyHolder);

		log.info("Successfully insert to collectionTestResult table!");

		// Return collection test result ID
		Integer collectionTestResultId =  keyHolder.getKey().intValue();

		return collectionTestResultId;
	}

	public Request getAPIDataFromCollectionDetailsId(Integer collectionDetailsId) {

		String getAPIDataSql = "SELECT apiurl, method, queryParams, headers, body " +
				"FROM collectionDetails WHERE id = ?";

		return jdbcTemplate.queryForObject(getAPIDataSql, new BeanPropertyRowMapper<>(Request.class), collectionDetailsId);
	}

	public void insertToCollectionTestResultException(
			Integer collectionTestResultId,
			LocalDate testDate,
			LocalTime testTime,
			ResponseEntity<?> responseEntity) throws JsonProcessingException {

		String result = responseEntity.getStatusCode().is2xxSuccessful() ? "pass" : "failed";

		String insertToCollectionTestResultExceptionSql = "INSERT INTO collectionTestResultException (" +
				"collectionTestResultId, testDate, testTime, statusCode, executionDuration, contentLength, " +
				"responseHeaders, responseBody, result) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(insertToCollectionTestResultExceptionSql,
				collectionTestResultId,
				testDate,
				testTime,
				responseEntity.getStatusCode().value(),
				responseEntity.getHeaders().getFirst("Execution-Duration"),
				responseEntity.getHeaders().getContentLength(),
				objectMapper.writeValueAsString(responseEntity.getHeaders()),
				responseEntity.getBody(),
				result);

		log.info("Successfully insert to collectionTestResultException table!");
	}
}

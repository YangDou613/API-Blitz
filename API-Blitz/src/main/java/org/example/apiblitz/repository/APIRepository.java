package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.APITestResult;
import org.example.apiblitz.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Slf4j
@Transactional
public class APIRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Integer insertToAPIHistory(
			Integer userId,
			String APIUrl,
			Request request,
			Timestamp timestamp,
			Object responseHeaders,
			Object responseBody,
			Integer statusCode) {

		String insertToAPIHistorySql = "INSERT INTO APIHistory (userId, APIUrl, method, queryParams, headers, body, " +
				"testDateTime, responseHeaders, responseBody, statusCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(insertToAPIHistorySql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, userId);
			ps.setString(2, APIUrl);
			ps.setString(3, request.getMethod());
			ps.setObject(4, request.getQueryParams());
			ps.setObject(5, request.getRequestHeaders());
			ps.setObject(6, request.getRequestBody());
			ps.setTimestamp(7, timestamp);
			ps.setObject(8, responseHeaders);
			ps.setObject(9, responseBody);
			ps.setInt(10, statusCode);
			return ps;
		}, keyHolder);

		// Get the API auto ID
		Integer APIAutoId = keyHolder.getKey().intValue();

		log.info("Successfully insert to APIHistory table!");

		return APIAutoId;
	}

	public List<Request> getAllHistoryList(Integer userId) {
		String getHistoryListSql = "SELECT * FROM APIHistory WHERE userId = ? ORDER BY id DESC LIMIT 20";
		return jdbcTemplate.query(getHistoryListSql, new BeanPropertyRowMapper<>(Request.class), userId);
	}

	public APITestResult getApiTestResultByUserIdAndDateTime(Integer userId, String testDateTime) {
		String getApiTestResultSql = "SELECT * FROM APIHistory WHERE userId = ? AND testDateTime = ?";
		return jdbcTemplate.queryForObject(getApiTestResultSql, new BeanPropertyRowMapper<>(APITestResult.class), userId, testDateTime);
	}
}

package org.example.apiblitz.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@Slf4j
public class TestCaseRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ObjectMapper objectMapper;

	public Integer insertToTestCase(Request request, TestCase testCase) throws JsonProcessingException {

		Object email = objectMapper.writeValueAsString(testCase.getRecipientEmail());

		String insertToTestCaseSql = "INSERT INTO testCase (userId, APIUrl, method, queryParams, headers, body, " +
				"statusCode, expectedResponseBody, intervalsTimeUnit, intervalsTimeValue, notification, " +
				"recipientEmail) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(insertToTestCaseSql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, 1); // 記得改 user Id
			ps.setString(2, request.getAPIUrl());
			ps.setString(3, request.getMethod());
			ps.setObject(4, request.getQueryParams());
			ps.setObject(5, request.getRequestHeaders());
			ps.setObject(6, request.getRequestBody());
			ps.setObject(7, testCase.getStatusCode());
			ps.setObject(8, testCase.getExpectedResponseBody());
			ps.setObject(9, testCase.getIntervalsTimeUnit());
			ps.setObject(10, testCase.getIntervalsTimeValue());
			ps.setObject(11, 1);
			ps.setObject(12, email);
			return ps;
		}, keyHolder);

		// Get the API auto ID
		Integer APIAutoId = keyHolder.getKey().intValue();

		log.info("Successfully insert to testCase table!");

		return APIAutoId;
	}
}

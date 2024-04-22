package org.example.apiblitz.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@Slf4j
@Transactional
public class TestCaseRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ObjectMapper objectMapper;

	public Integer insertToTestCase(Request request, TestCase testCase) throws JsonProcessingException {

		Integer notification = testCase.getNotification().equals("Yes") ? 1 : 0;

		Object email = objectMapper.writeValueAsString(testCase.getRecipientEmail());

		String insertToTestCaseSql = "INSERT INTO testCase (userId, APIUrl, method, queryParams, headers, body, " +
				"statusCode, expectedResponseBody, intervalsTimeUnit, intervalsTimeValue, notification, " +
				"recipientEmail, resetStatus) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(insertToTestCaseSql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, 1);
			ps.setString(2, testCase.getUrl());
			ps.setString(3, request.getMethod());
			ps.setObject(4, request.getQueryParams());
			ps.setObject(5, request.getRequestHeaders());
			ps.setObject(6, request.getRequestBody());
			ps.setObject(7, testCase.getStatusCode());
			ps.setObject(8, testCase.getExpectedResponseBody());
			ps.setObject(9, testCase.getIntervalsTimeUnit());
			ps.setObject(10, testCase.getIntervalsTimeValue());
			ps.setObject(11, notification);
			ps.setObject(12, email);
			ps.setInt(13, 0);
			return ps;
		}, keyHolder);

		// Get the API auto ID
		Integer APIAutoId = keyHolder.getKey().intValue();

		log.info("Successfully insert to testCase table!");

		return APIAutoId;
	}

	public void updateTestCase(Request request, ResetTestCase resetTestCase)
			throws SQLException, JsonProcessingException {

		Integer notification = resetTestCase.getNotification().equals("Yes") ? 1 : 0;

		Object email = objectMapper.writeValueAsString(resetTestCase.getRecipientEmail());

		String updateTestCaseSql = "UPDATE testCase SET userId = ?, APIUrl = ?, method = ?, queryParams = ?, " +
				"headers = ?, body = ?, statusCode = ?, expectedResponseBody = ?, intervalsTimeUnit = ?, " +
				"intervalsTimeValue = ?, notification = ?, recipientEmail = ?, resetStatus = ? WHERE id = ?";

		jdbcTemplate.update(updateTestCaseSql,
				1,
				request.getAPIUrl(),
				request.getMethod(),
				request.getQueryParams(),
				request.getRequestHeaders(),
				request.getRequestBody(),
				resetTestCase.getStatusCode(),
				resetTestCase.getExpectedResponseBody(),
				resetTestCase.getIntervalsTimeUnit(),
				resetTestCase.getIntervalsTimeValue(),
				notification,
				email,
				1,
				resetTestCase.getId());
	}

	public List<NextSchedule> getTestCase(Integer userId) throws SQLException {
		String getTestCaseSql = "SELECT * FROM testCase WHERE userId = ?";
		return jdbcTemplate.query(getTestCaseSql, new BeanPropertyRowMapper<>(NextSchedule.class), userId);
	}

	public Integer getResetStatusByTestCaseId(Integer testCaseId) throws SQLException {
		String getResetStatusSql = "SELECT resetStatus FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getResetStatusSql, Integer.class, testCaseId);
	}

	public void updateResetStatusByTestCaseId(Integer testCaseId) throws SQLException {
		String updateTestCaseSql = "UPDATE testCase SET resetStatus = 0 WHERE id = ?";
		jdbcTemplate.update(updateTestCaseSql, testCaseId);
	}

	public void deleteTestCase(Integer testCaseId) throws SQLException {

		String deleteTestCaseSql = "UPDATE testCase SET resetStatus = null WHERE id = ?";
		jdbcTemplate.update(deleteTestCaseSql, testCaseId);
	}

	public boolean isTestCaseIdExistInNextTestSchedule(Integer testCaseId) {

		String confirmSql = "SELECT COUNT(*) FROM nextTestSchedules WHERE testCaseId = ?";
		Integer count = jdbcTemplate.queryForObject(confirmSql, Integer.class, testCaseId);
		return count != null && count > 0;
	}

	public void insertToNextTestSchedule(Integer testCaseId, LocalDate testDate, LocalTime nextTestTime) {

		String insertToNextTestScheduleSql = "INSERT INTO nextTestSchedules (testCaseId, nextTestDate, nextTestTime) " +
				"VALUES (?, ?, ?)";
		jdbcTemplate.update(insertToNextTestScheduleSql, testCaseId, testDate, nextTestTime);
	}

	public void updateNextTestTime(Integer testCaseId, LocalDate nextTestDate, LocalTime nextTestTime)
			throws SQLException {

		String updateNextTestScheduleSql = "UPDATE nextTestSchedules SET nextTestDate = ?, nextTestTime = ? " +
				"WHERE testCaseId = ?";
		jdbcTemplate.update(updateNextTestScheduleSql, nextTestDate, nextTestTime, testCaseId);
	}
}

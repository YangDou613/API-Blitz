package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@Slf4j
@Transactional
public class AutoTestRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Request getAPIData(Integer testCaseId) {
		String getTestCaseRequestSql = "SELECT APIUrl, method, queryParams, headers, body FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getTestCaseRequestSql, new BeanPropertyRowMapper<>(Request.class), testCaseId);
	}

	public Integer getExpectedStatusCode(Integer testCaseId) {
		String getTestCaseRequestSql = "SELECT statusCode FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getTestCaseRequestSql, Integer.class, testCaseId);
	}

	public String getExpectedResponseBody(Integer testCaseId) {
		String getTestCaseRequestSql = "SELECT expectedResponseBody FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getTestCaseRequestSql, String.class, testCaseId);
	}

	public void insertToTestResult(Integer testCaseId,
	                               LocalDate testDate,
	                               LocalTime testTime,
	                               Integer statusCode,
	                               String executionDuration,
	                               long contentLength,
	                               Object responseHeaders,
	                               Object responseBody,
	                               String result) {

		String insertToTestResultSql = "INSERT INTO testResult (testCaseId, testDate, testTime, statusCode, " +
				"executionDuration, contentLength, responseHeaders, responseBody, result) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(insertToTestResultSql, testCaseId, testDate, testTime, statusCode,
				executionDuration, contentLength, responseHeaders, responseBody, result);

		log.info("Successfully insert to testResult table!");
	}

	public String getRecipientEmailList(Integer testCaseId) {
		String getRecipientEmailListSql = "SELECT recipientEmail FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getRecipientEmailListSql, String.class, testCaseId);
	}

	public List<Integer> getAllTestCaseIdByUserId(Integer userId) {
		String getTestCaseIdListSql = "SELECT id FROM testCase WHERE resetStatus = 0 AND userId = ?";
		return jdbcTemplate.queryForList(getTestCaseIdListSql, Integer.class, userId);
	}

	public List<TestResult> getAllTestResultByTestCaseId(Integer testCaseId) {
		String getTestResultListSql = "SELECT * FROM ( SELECT * FROM testResult WHERE testCaseId = ? " +
				"ORDER BY testDate DESC, testTime DESC LIMIT 10 ) AS subquery ORDER BY testTime ASC";
		return jdbcTemplate.query(getTestResultListSql, new Object[]{testCaseId}, new BeanPropertyRowMapper<>(TestResult.class));
	}
}

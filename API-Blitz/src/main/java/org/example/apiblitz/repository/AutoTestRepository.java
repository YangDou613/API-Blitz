package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.CollectionTestResult;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	public Map<String, Object> getTestStartTime(Integer testCaseId) {

		String getMinTestDateSql = "SELECT MIN(testDate) FROM testResult WHERE testCaseId = ?";
		Date startTestDate = jdbcTemplate.queryForObject(getMinTestDateSql, Date.class, testCaseId);

		String getMinTestTimeSql = "SELECT testDate, MIN(testTime) FROM testResult WHERE testCaseId = ? AND testDate = ?";
		return jdbcTemplate.queryForMap(getMinTestTimeSql, testCaseId, startTestDate);
	}

	public List<TestResult> getAllTestResultByTestCaseId(Integer testCaseId) {
		String getTestResultListSql = "SELECT * FROM testResult WHERE testCaseId = ?";
		return jdbcTemplate.query(getTestResultListSql, new Object[]{testCaseId}, new BeanPropertyRowMapper<>(TestResult.class));
	}

	public List<TestResult> getTenTestResultByTestCaseId(Integer testCaseId) {
		String getTestResultListSql = "SELECT * FROM ( SELECT * FROM testResult WHERE testCaseId = ? " +
				"ORDER BY testDate DESC, testTime DESC LIMIT 10 ) AS subquery ORDER BY testTime ASC";
		return jdbcTemplate.query(getTestResultListSql, new Object[]{testCaseId}, new BeanPropertyRowMapper<>(TestResult.class));
	}

	public List<Map<String, Object>> getAllTestTime(Integer testCaseId) {
		String getTestTimeSql = "SELECT DISTINCT testDate, testTime FROM collectionTestResult WHERE collectionId = ?";
		return jdbcTemplate.queryForList(getTestTimeSql, testCaseId);
	}

	public List<CollectionTestResult> getTestResultByCollectionId(Integer collectionId, LocalDate testDate, LocalTime testTime) {

		String getTestTimeSql = "SELECT cd.*, ctr.* FROM collectionDetails cd JOIN collectionTestResult ctr " +
				"ON cd.id = ctr.collectionDetailsId WHERE testDate = ? AND testTime = ? AND cd.collectionId = ?";
		return jdbcTemplate.query(getTestTimeSql, new BeanPropertyRowMapper<>(CollectionTestResult.class), testDate, testTime, collectionId);
	}

	public List<CollectionTestResult> getRetestResultByCollectionTestResultId(Integer collectionTestResultId) {

		String getRetestResultSql =
				"SELECT * FROM collectionTestResultException WHERE collectionTestResultId = ?";
		return jdbcTemplate.query(getRetestResultSql, new BeanPropertyRowMapper<>(CollectionTestResult.class), collectionTestResultId);
	}

	public List<List<TestResult>> getAllTestResultByCollectionId(Integer collectionId) {

		// Get test time
		String getTestTimeSql = "SELECT DISTINCT testDate, testTime FROM collectionTestResult WHERE collectionId = ?";
		List<Map<String, Object>> testTimeList = jdbcTemplate.queryForList(getTestTimeSql, collectionId);

		// Get test result
		List<List<TestResult>> testResultList = new ArrayList<>();

		for (Map<String, Object> testTime : testTimeList) {
			String getTestResultSql =
					"SELECT * FROM collectionTestResult WHERE testDate = ? AND testTime = ? AND collectionId = ?";
			List<TestResult> testResult = jdbcTemplate.query(getTestResultSql, new BeanPropertyRowMapper<>(TestResult.class),
					testTime.get("testDate"),
					testTime.get("testTime"),
					collectionId);
			testResultList.add(testResult);
		}
		return testResultList;
	}
}

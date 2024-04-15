package org.example.apiblitz.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AutoTestRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Integer getExpectedStatusCode(Integer testCaseId) {
		String getTestCaseRequestSql = "SELECT statusCode FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getTestCaseRequestSql, Integer.class, testCaseId);
	}

	public String getExpectedResponseBody(Integer testCaseId) {
		String getTestCaseRequestSql = "SELECT expectedResponseBody FROM testCase WHERE id = ?";
		return jdbcTemplate.queryForObject(getTestCaseRequestSql, String.class, testCaseId);
	}
}

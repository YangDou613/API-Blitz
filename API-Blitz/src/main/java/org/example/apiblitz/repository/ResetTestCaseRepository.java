package org.example.apiblitz.repository;

import org.example.apiblitz.model.NextSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResetTestCaseRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<NextSchedule> getAllNextSchedule() {

		String getNextTestScheduleSql = "SELECT * FROM testCase AS tc INNER JOIN nextTestSchedules AS nts " +
				"ON tc.id = nts.testCaseId";
		return jdbcTemplate.query(getNextTestScheduleSql, new BeanPropertyRowMapper<>(NextSchedule.class));
	}
}

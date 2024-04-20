package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@Slf4j
@Transactional
public class APIRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Integer insertToAPIHistory(Request request) {

		String insertToAPIHistorySql = "INSERT INTO APIHistory (userId, APIUrl, method, queryParams, headers, body) " +
				"VALUES (?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(insertToAPIHistorySql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, 1);
			ps.setString(2, request.getAPIUrl());
			ps.setString(3, request.getMethod());
			ps.setObject(4, request.getQueryParams());
			ps.setObject(5, request.getRequestHeaders());
			ps.setObject(6, request.getRequestBody());
			return ps;
		}, keyHolder);

		// Get the API auto ID
		Integer APIAutoId = keyHolder.getKey().intValue();

		log.info("Successfully insert to APIHistory table!");

		return APIAutoId;
	}
}

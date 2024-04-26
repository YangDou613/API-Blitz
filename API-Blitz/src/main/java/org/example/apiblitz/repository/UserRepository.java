package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.UserSignIn;
import org.example.apiblitz.model.UserSignUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@Slf4j
public class UserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean findByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
		return count != null && count > 0;
	}

	public Integer addToUserTableWhenSignUp(UserSignUp user) {
		String sql = "INSERT INTO user (name, email, password) VALUES (?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPassword());
			return ps;
		}, keyHolder);

		return keyHolder.getKey().intValue();

	}

	public UserSignIn getUserInfo(String email) {
		String getUserInfoSql = "SELECT * FROM user WHERE email = ?";
		try {
			return jdbcTemplate.queryForObject(getUserInfoSql, new BeanPropertyRowMapper<>(UserSignIn.class), email);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}

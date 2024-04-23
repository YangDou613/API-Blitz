package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@Transactional
public class CollectionRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> getCollectionsList(Integer userId) throws SQLException {

		String getCollectionSql = "SELECT *, (" +
				"SELECT JSON_ARRAYAGG(" +
				"JSON_OBJECT(" +
				"'requestName', requestName," +
				"'APIUrl', APIUrl," +
				"'method', method," +
				"'queryParams', IFNULL(queryParams, 'null')," +
				"'headers', IFNULL(headers, 'null')," +
				"'body', IFNULL(body, 'null'))) FROM collectionDetails WHERE collectionId = collections.id) " +
				"AS collectionDetails FROM collections WHERE userId = ?";

		return jdbcTemplate.queryForList(getCollectionSql, userId);
	}

	public void insertToCollectionsTable(Integer userId, Collection collection) throws SQLException {

		String insertToCollectionTableSql = "INSERT INTO collections (collectionName, description, userId) " +
				"VALUES (?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(insertToCollectionTableSql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, collection.getCollectionName());
			ps.setString(2, collection.getDescription());
			ps.setInt(3, userId);
			return ps;
		}, keyHolder);

		// Get collection id
		Integer collectionId = keyHolder.getKey().intValue();
		insertToCollectionDetailsTable(collectionId, collection);

		log.info("Successfully insert to collections table!");
	}

	public void insertToCollectionDetailsTable(Integer collectionId, Collection collection) {

		String insertToCollectionDetailsTableSql = "INSERT INTO collectionDetails (collectionId, requestName, " +
				"APIUrl, method, queryParams, headers, body) VALUES (?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(insertToCollectionDetailsTableSql,
				collectionId,
				collection.getRequestName(),
				collection.getRequest().getAPIUrl(),
				collection.getRequest().getMethod(),
				collection.getRequest().getQueryParams(),
				collection.getRequest().getHeaders(),
				collection.getRequest().getBody());
	}

	public void updateCollection(Integer collectionId, Collection collection) throws SQLException {

		String updateCollectionSql = "UPDATE collections SET collectionName = ?, description = ? WHERE id = ?";
		jdbcTemplate.update(updateCollectionSql,
				collection.getCollectionName(),
				collection.getDescription());

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

	public void addAPIToCollection(Integer collectionId, Collection collection) throws SQLException {

		String addCollectionSql = "UPDATE collectionDetails SET requestName = ?, APIUrl = ?, method = ?, " +
				"queryParams = ?, headers = ?, body = ? WHERE collectionId = ?";
		jdbcTemplate.update(addCollectionSql,
				collection.getRequestName(),
				collection.getRequest().getAPIUrl(),
				collection.getRequest().getMethod(),
				collection.getRequest().getQueryParams(),
				collection.getRequest().getHeaders(),
				collection.getBody(),
				collectionId);
	}

	public void deleteCollection(Integer userId, String collectionName, String requestName) throws SQLException {

		// Get collection id
		String getCollectionIdSql = "SELECT id FROM collections WHERE collectionName = ? AND userId = ?";
		Integer collectionId = jdbcTemplate.queryForObject(getCollectionIdSql, Integer.class, collectionName, userId);

		if (requestName == null) {
			String deleteCollectionSql = "DELETE FROM collections WHERE collectionName = ? AND userId = ?";
			jdbcTemplate.update(deleteCollectionSql, collectionName, userId);
		} else {
			String deleteCollectionDetailsSql = "DELETE FROM collectionDetails WHERE collectionId = ? AND " +
					"requestName = ?";
			jdbcTemplate.update(deleteCollectionDetailsSql, collectionId, requestName);
		}
	}
}

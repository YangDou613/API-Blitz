package org.example.apiblitz.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@Transactional
public class CollectionsRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> getCollectionsList(Integer userId) throws SQLException {

		String getCollectionSql = "SELECT *, (" +
				"SELECT JSON_ARRAYAGG(" +
				"JSON_OBJECT(" +
				"'requestName', requestName," +
				"'apiurl', apiurl," +
				"'method', method," +
				"'queryParams', IFNULL(queryParams, 'null')," +
				"'headers', IFNULL(headers, 'null')," +
				"'body', IFNULL(body, 'null'))) FROM collectionDetails WHERE collectionId = collections.id) " +
				"AS collectionDetails FROM collections WHERE userId = ?";

		return jdbcTemplate.queryForList(getCollectionSql, userId);
	}

	public void insertToCollectionsTable(Integer userId, Collections collection) throws SQLException {

		String insertToCollectionTableSql = "INSERT INTO collections (collectionName, description, userId) " +
				"VALUES (?, ?, ?)";

		jdbcTemplate.update(insertToCollectionTableSql,
				collection.getCollectionName(),
				collection.getDescription(),
				userId);

		log.info("Successfully insert to collections table!");
	}

	public void addAPIToCollection(Integer collectionId, Collections collection) throws SQLException {

		String addAPISql = "INSERT INTO collectionDetails (collectionId, requestName, APIUrl, method, " +
				"queryParams, headers, body) VALUES (?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(addAPISql,
				collectionId,
				collection.getRequestName(),
				collection.getRequest().getAPIUrl(),
				collection.getRequest().getMethod(),
				collection.getRequest().getQueryParams(),
				collection.getRequest().getHeaders(),
				collection.getRequest().getBody());

		log.info("Successfully added API to the collection!");
	}

	public void updateCollection(Integer collectionId, Collections collection) throws SQLException {

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

		log.info("Update successfully!");
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

		log.info("Delete successfully!");
	}
}

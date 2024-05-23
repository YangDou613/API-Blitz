package org.example.apiblitz.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.Collections;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.CollectionsService;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("Producer")
@Controller
@Slf4j
@RequestMapping("/api/1.0/collections")
public class CollectionsController {

	@Autowired
	CollectionsService collectionService;

	@Autowired
	APIService apiService;

	@Autowired
	Publisher publisher;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping
	public ResponseEntity<?> getCollections(
			@RequestHeader("Authorization") String authorization) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		String accessToken = jwtUtil.extractAccessToken(authorization);

		try {
			List<Map<String, Object>> collectionList = collectionService.getCollections(accessToken);

			if (collectionList != null) {
				return ResponseEntity
						.ok()
						.body(collectionList);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You don't have any collections yet.");
			}
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
	}

	@PostMapping(path = "/create")
	public ResponseEntity<?> createCollection(
			@RequestHeader("Authorization") String authorization,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			String accessToken = jwtUtil.extractAccessToken(authorization);
			collectionService.createCollection(accessToken, collection);

			return ResponseEntity
					.ok()
					.build();
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@PostMapping(path = "/create/addAPI")
	public ResponseEntity<?> addAPIToCollection(
			@RequestHeader("Authorization") String authorization,
			Integer collectionId,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.addApiToCollection(collectionId, collection);

			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@PostMapping(path = "/create/addHistoryAPI")
	public ResponseEntity<?> addHistoryAPIToCollection(
			@RequestHeader("Authorization") String authorization,
			Integer collectionId,
			@RequestBody Collections collection,
			BindingResult bindingResult) throws BindException {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.addApiToCollection(collectionId, collection);

			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@PostMapping(path = "/update")
	public ResponseEntity<?> updateCollection(
			@RequestHeader("Authorization") String authorization,
			Integer collectionId,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.updateCollection(collectionId, collection);

			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@DeleteMapping(path = "/delete")
	public ResponseEntity<?> deleteCollection(
			@RequestHeader("Authorization") String authorization,
			String collectionName,
			@RequestParam(required = false) Integer requestId) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		try {
			String accessToken = jwtUtil.extractAccessToken(authorization);

			collectionService.deleteCollection(accessToken, collectionName, requestId);

			return ResponseEntity
					.ok()
					.build();
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@GetMapping(path = "/getAllAPI")
	public ResponseEntity<List<Request>> getCollectionAllApi(Integer collectionId) {

		List<Request> apiList = collectionService.getCollectionAllApi(collectionId);

		if (apiList != null) {
			return ResponseEntity.ok().body(apiList);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/testAll")
	public ResponseEntity<?> testCollectionAllApi(
			@RequestHeader("Authorization") String authorization,
			Integer collectionId,
			@RequestBody List<Request> requests) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		try {
			// User ID
			String accessToken = jwtUtil.extractAccessToken(authorization);
			Claims claims = jwtUtil.parseToken(accessToken);
			Integer userId = claims.get("userId", Integer.class);

			// Category
			String category = "Collections";

			// ID
			Integer id = collectionId;

			// Test dateTime
			Map<String, Object> collectionTestTime = new HashMap<>();

			// Test Date
			LocalDate testDate = LocalDate.now();
			collectionTestTime.put("testDate", testDate);

			// Test time
			LocalTime testTime = LocalTime.now();
			collectionTestTime.put("testTime", testTime);

			LocalDateTime localDateTime = LocalDateTime.of(testDate, testTime);
			Timestamp testDateTime = Timestamp.valueOf(localDateTime);

			// Content
			Object content = requests;

			publisher.publishMessage(userId, category, id, testDateTime, content);

			return ResponseEntity.ok().body(collectionTestTime);
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
}

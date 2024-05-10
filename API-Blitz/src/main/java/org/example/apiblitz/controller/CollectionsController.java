package org.example.apiblitz.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Collections;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.UserResponse;
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
	private JwtUtil jwtUtil;

	@Autowired
	Publisher publisher;

	@GetMapping
	public String collectionsPage() {
		return "collections";
	}

	@GetMapping(path = "/details")
	public String collectionDetailsPage(
			@RequestParam("collectionName") String collectionName,
			@RequestParam("collectionId") Integer collectionId) {
		return "collectionDetails";
	}

	@GetMapping(path = "/get")
	public ResponseEntity<?> getCollections(
			@RequestHeader("Authorization") String authorization) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

		List<Map<String, Object>> collectionList = collectionService.get(accessToken);

		if (collectionList != null) {
			return ResponseEntity
					.ok()
					.body(collectionList);
		} else {
			return ResponseEntity.badRequest().body("You don't have any collections yet.");
		}
	}

	@PostMapping(path = "/create")
	public ResponseEntity<?> createCollection(
			@RequestHeader("Authorization") String authorization,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.badRequest().body(userResponse);
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			String accessToken = extractAccessToken(authorization);

			collectionService.create(accessToken, collection);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ResponseEntity
				.ok()
				.build();
	}

	@PostMapping(path = "/create/addAPI")
	public ResponseEntity<?> addAPIToCollection(
			@RequestHeader("Authorization") String authorization,
			Integer collectionId,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {

			collectionService.add(collectionId, collection);

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

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {

			collectionService.add(collectionId, collection);

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

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {

			collectionService.update(collectionId, collection);

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

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		try {
			String accessToken = extractAccessToken(authorization);

			collectionService.delete(accessToken, collectionName, requestId);

			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@GetMapping(path = "/getAllAPI")
	public ResponseEntity<List<Request>> getAllAPI(Integer collectionId) {

		List<Request> apiList = collectionService.getAPIList(collectionId);

		if (apiList != null) {
			return ResponseEntity.ok().body(apiList);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/testAll")
	public ResponseEntity<?> getResponseAtSameTime(
			@RequestHeader("Authorization") String authorization,
			Integer collectionId,
			@RequestBody List<Request> requests) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		try {
			// User ID
			String accessToken = extractAccessToken(authorization);
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

		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	private String extractAccessToken(String authorization) {
		String[] parts = authorization.split(" ");
		if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
			return parts[1];
		} else {
			return null;
		}
	}
}

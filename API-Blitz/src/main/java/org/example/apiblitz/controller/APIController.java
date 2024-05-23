package org.example.apiblitz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.APIData;
import org.example.apiblitz.model.APITestResult;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.AutoTestService;
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
import java.time.LocalDateTime;
import java.util.List;

@Profile("Producer")
@Controller
@Slf4j
@RequestMapping("/api/1.0/APITest")
public class APIController {

	@Autowired
	APIService apiService;

	@Autowired
	AutoTestService autoTestService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	Publisher publisher;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping
	public ResponseEntity<?> receiveApiTestData(
			@RequestHeader("Authorization") String authorization,
			@Valid @ModelAttribute APIData apiData,
			BindingResult bindingResult) throws BindException {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			// User ID
			String accessToken = jwtUtil.extractAccessToken(authorization);
			Claims claims = jwtUtil.parseToken(accessToken);
			Integer userId = claims.get("userId", Integer.class);

			// Category
			String category = "APITest";

			// ID
			Integer id = null;

			// Test dateTime
			LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
			Timestamp testDateTime = Timestamp.valueOf(currentDateTime);

			// Content
			Object content = apiData;

			publisher.publishMessage(userId, category, id, testDateTime, content);

			return ResponseEntity.ok().body(currentDateTime);
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		} catch (Exception e) {
			log.error("Internal server error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@GetMapping("/testResult")
	public ResponseEntity<?> getApiTestResult(
			@RequestHeader("Authorization") String authorization,
			@RequestParam("testDateTime") String testDateTime) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		String accessToken = jwtUtil.extractAccessToken(authorization);

		try {
			APITestResult apiTestResult = apiService.getApiTestResult(accessToken, testDateTime);

			if (apiTestResult != null) {
				return ResponseEntity.ok(apiTestResult);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no API test result.");
			}
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
	}

	@GetMapping("/history")
	public ResponseEntity<?> getApiTestHistory(
			@RequestHeader("Authorization") String authorization) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		String accessToken = jwtUtil.extractAccessToken(authorization);

		try {
			List<Request> historyList = apiService.getApiTestHistory(accessToken);

			if (historyList != null) {
				return ResponseEntity.ok(historyList);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no API history.");
			}
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
	}
}

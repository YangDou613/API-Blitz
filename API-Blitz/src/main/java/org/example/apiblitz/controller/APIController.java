package org.example.apiblitz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.AutoTestService;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

//@Profile("Producer")
@Controller
@Slf4j
public class APIController {

	@Autowired
	APIService apiService;

	@Autowired
	AutoTestService autoTestService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	Publisher publisher;

	@GetMapping ("/APITest.html")
	public String APITestPage() {
		return "APITest";
	}

	@PostMapping("/APITest.html")
	public ResponseEntity<?> getResponse(
			@RequestHeader("Authorization") String authorization,
			@Valid @ModelAttribute APIData apiData,
			BindingResult bindingResult)
			throws BindException {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			// User ID
			String accessToken = extractAccessToken(authorization);
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

//			apiService.APITest(accessToken, timestamp, apiData);

			return ResponseEntity
					.ok()
					.body(currentDateTime);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@GetMapping("/APITest/testResult")
	public ResponseEntity<?> getTestResult(
			@RequestHeader("Authorization") String authorization,
			@RequestParam("testDateTime") String testDateTime) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

		APITestResult apiTestResult = apiService.getApiTestResult(accessToken, testDateTime);
		if (apiTestResult != null) {
			return ResponseEntity.ok(apiTestResult);
		} else {
			return ResponseEntity.badRequest().body("There is currently no API test result.");
		}
	}

	@GetMapping ("/history")
	public String historyPage() {
		return "history";
	}

	@GetMapping("/APITest/history")
	public ResponseEntity<?> getHistory(
			@RequestHeader("Authorization") String authorization) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

		List<Request> historyList = apiService.getAllHistory(accessToken);
		if (historyList != null) {
			return ResponseEntity.ok(historyList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no API history.");
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

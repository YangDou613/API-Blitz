package org.example.apiblitz.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.model.ResetTestCase;
import org.example.apiblitz.model.TestCase;
import org.example.apiblitz.model.UserResponse;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.service.TestCaseService;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Profile("Producer")
@Controller
@Slf4j
@RequestMapping("/api/1.0/testCase")
public class TestCaseController {

	@Autowired
	TestCaseService testCaseService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	Publisher publisher;

	@GetMapping
	public String testCasePage() {
		return "testCase";
	}

	@GetMapping(path = "/get")
	public ResponseEntity<?> getTestCase(
			@RequestHeader("Authorization") String authorization) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

//		List<NextSchedule> testCaseList = testCaseService.get(userId);
		List<NextSchedule> testCaseList = testCaseService.get(accessToken);

		if (testCaseList != null) {
			return ResponseEntity
					.ok()
					.body(testCaseList);
		} else {
			return ResponseEntity.badRequest().body("You don't have any Test cases yet.");
		}
	}

	@PostMapping(path = "/create")
	public String createTestCase(
			@RequestHeader("Authorization") String authorization,
			@Valid @ModelAttribute TestCase testCase,
			BindingResult bindingResult) throws BindException {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return "redirect:/api/1.0/testCase";
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			// User ID
			String accessToken = extractAccessToken(authorization);
			Claims claims = jwtUtil.parseToken(accessToken);
			Integer userId = claims.get("userId", Integer.class);
//
//			// Category
//			String category = "TestCase";
//
//			// Type
//			String type = "create/save";
//
//			// ID
//			Integer id = null;
//
//			// Test dateTime
//			LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
//			Timestamp testDateTime = Timestamp.valueOf(currentDateTime);
//
//			// Content
//			Object content = testCase;

//			publisher.publishMessage(userId, category, type, id, testDateTime, content);

			Integer testCaseId = testCaseService.save(userId, testCase);

//			testCaseService.setTestSchedule(userId, Integer.parseInt(message), testCase);
			testCaseService.setTestSchedule(userId, testCaseId, testCase);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "redirect:/api/1.0/testCase";
	}

	@GetMapping("/myTestCase")
	public String modifyTestCasePage() {
		return "myTestCase";
	}

	@PostMapping(path = "/update")
	public ResponseEntity<?> updateTestCase(
			@RequestHeader("Authorization") String authorization,
			@Valid @ModelAttribute ResetTestCase resetTestCase,
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
			String accessToken = extractAccessToken(authorization);
//			Claims claims = jwtUtil.parseToken(accessToken);
//			Integer userId = claims.get("userId", Integer.class);
//
//			// Category
//			String category = "TestCase";
//
//			// Type
//			String type = "update";
//
//			// ID
//			Integer id = null;
//
//			// Test dateTime
//			LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
//			Timestamp testDateTime = Timestamp.valueOf(currentDateTime);
//
//			// Content
//			Object content = resetTestCase;
//
//			publisher.publishMessage(userId, category, type, id, testDateTime, content);

//			testCaseService.update(resetTestCase);
			testCaseService.update(accessToken, resetTestCase);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@DeleteMapping(path = "/delete")
	public ResponseEntity<?> deleteTestCase(
			@RequestHeader("Authorization") String authorization,
			Integer testCaseId) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		try {
			// User ID
//			String accessToken = extractAccessToken(authorization);
//			Claims claims = jwtUtil.parseToken(accessToken);
//			Integer userId = claims.get("userId", Integer.class);
//
//			// Category
//			String category = "TestCase";
//
//			// Type
//			String type = "delete";
//
//			// ID
//			Integer id = testCaseId;
//
//			// Test dateTime
//			LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
//			Timestamp testDateTime = Timestamp.valueOf(currentDateTime);
//
//			// Content
//			Object content = null;
//
//			publisher.publishMessage(userId, category, type, id, testDateTime, content);

			testCaseService.delete(testCaseId);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
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

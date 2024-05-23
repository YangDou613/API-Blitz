package org.example.apiblitz.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.model.ResetTestCase;
import org.example.apiblitz.model.TestCase;
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

@Profile("Producer")
@Controller
@Slf4j
@RequestMapping("/api/1.0/testCase")
public class TestCaseController {

	@Autowired
	TestCaseService testCaseService;

	@Autowired
	Publisher publisher;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping(path = "/get")
	public ResponseEntity<?> getTestCase(
			@RequestHeader("Authorization") String authorization) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		String accessToken = jwtUtil.extractAccessToken(authorization);

		try {
			List<NextSchedule> testCaseList = testCaseService.getTestCase(accessToken);

			if (testCaseList != null) {
				return ResponseEntity
						.ok()
						.body(testCaseList);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You don't have any Test cases yet.");
			}
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
	}

	@PostMapping(path = "/create")
	public ResponseEntity<?> createTestCase(
			@RequestHeader("Authorization") String authorization,
			@Valid @ModelAttribute TestCase testCase,
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

			Integer testCaseId = testCaseService.createTestCase(userId, testCase);

			testCaseService.setTestCaseTestSchedule(userId, testCaseId, testCase);

		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return ResponseEntity
				.ok()
				.build();
	}

	@PostMapping(path = "/update")
	public ResponseEntity<?> updateTestCase(
			@RequestHeader("Authorization") String authorization,
			@Valid @ModelAttribute ResetTestCase resetTestCase,
			BindingResult bindingResult) throws BindException {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			String accessToken = jwtUtil.extractAccessToken(authorization);

			testCaseService.updateTestCase(accessToken, resetTestCase);

			return ResponseEntity.ok().build();
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@DeleteMapping(path = "/delete")
	public ResponseEntity<?> deleteTestCase(
			@RequestHeader("Authorization") String authorization,
			Integer testCaseId) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		try {
			testCaseService.deleteTestCase(testCaseId);

			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
}

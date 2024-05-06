package org.example.apiblitz.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.model.ResetTestCase;
import org.example.apiblitz.model.TestCase;
import org.example.apiblitz.model.UserResponse;
import org.example.apiblitz.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/1.0/testCase")
public class TestCaseController {

	@Autowired
	TestCaseService testCaseService;

	@GetMapping
	public String testCasePage() {
		return "testCase";
	}

	@GetMapping(path = "/get")
//	public ResponseEntity<?> getTestCase(Integer userId) {
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

		String accessToken = extractAccessToken(authorization);

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			Integer testCaseId = testCaseService.save(accessToken, testCase);
			testCaseService.setTestSchedule(testCaseId, testCase);
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

		String accessToken = extractAccessToken(authorization);

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
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
	public ResponseEntity<?> deleteTestCase(Integer testCaseId) {

		try {
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

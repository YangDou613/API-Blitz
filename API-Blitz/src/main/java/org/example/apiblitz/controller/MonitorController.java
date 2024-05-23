package org.example.apiblitz.controller;

import org.example.apiblitz.error.TokenParsingException;
import org.example.apiblitz.model.CollectionTestResult;
import org.example.apiblitz.model.TestResult;
import org.example.apiblitz.service.AutoTestService;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Profile("Producer")
@Controller
@RequestMapping("/api/1.0/monitor")
public class MonitorController {

	@Autowired
	AutoTestService autoTestService;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping("/testCase")
	public ResponseEntity<?> getTestCaseIdList(
			@RequestHeader("Authorization") String authorization) {

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing Bearer token");
		}

		String accessToken = jwtUtil.extractAccessToken(authorization);

		try {
			List<Integer> testCaseIdList = autoTestService.getTestCaseIdList(accessToken);

			if (testCaseIdList != null) {
				return ResponseEntity.ok(testCaseIdList);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You don't have any Test cases yet.");
			}
		} catch (TokenParsingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
		}
	}

	@GetMapping("/testResult/testStartTime/{testCaseId}")
	public ResponseEntity<?> getTestCaseStartTime(@PathVariable Integer testCaseId) {

		Map<String, Object> testResultList = autoTestService.getTestCaseStartTime(testCaseId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no test results.");
		}
	}

	@GetMapping("/testResult/{testCaseId}")
	public ResponseEntity<?> getTestCaseTestResult(@PathVariable Integer testCaseId) {

		List<TestResult> testResultList = autoTestService.getTestCaseTestResult(testCaseId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no test results.");
		}
	}

	// Get the latest 10 test results of the test case to display them on the dashboard
	@GetMapping("/testResult/dashboard/{testCaseId}")
	public ResponseEntity<?> getTenTestCaseTestResult(@PathVariable Integer testCaseId) {

		List<TestResult> testResultList = autoTestService.getTenTestCaseTestResult(testCaseId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no test results.");
		}
	}

	@GetMapping("/testResult/testTime/{collectionId}")
	public ResponseEntity<?> getCollectionTestTime(@PathVariable Integer collectionId) {

		List<Map<String, Object>> testResultList = autoTestService.getCollectionTestTime(collectionId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no test results.");
		}
	}

	@GetMapping("/testResult")
	public ResponseEntity<?> getCollectionTestResult(
			@RequestParam Integer collectionId,
			@RequestParam LocalDate testDate,
			@RequestParam LocalTime testTime) {

		List<CollectionTestResult> testResultList = autoTestService.getCollectionTestResult(collectionId, testDate, testTime);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no test results.");
		}
	}

	@GetMapping("/retestResult")
	public ResponseEntity<?> getCollectionRetestResult(@RequestParam Integer collectionTestResultId) {

		List<CollectionTestResult> retestResultList = autoTestService.getCollectionRetestResult(collectionTestResultId);

		if (retestResultList != null) {
			return ResponseEntity.ok(retestResultList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is currently no test results.");
		}
	}
}

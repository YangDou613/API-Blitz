package org.example.apiblitz.controller;

import org.example.apiblitz.model.CollectionTestResult;
import org.example.apiblitz.model.TestResult;
import org.example.apiblitz.model.UserResponse;
import org.example.apiblitz.service.AutoTestService;
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
@RequestMapping("/api/1.0/autoTest")
public class AutoTestController {

	@Autowired
	AutoTestService autoTestService;

	@GetMapping("/monitor")
	public String monitorPage() {
		return "monitor";
	}

	@GetMapping("/monitor/testCase")
	public ResponseEntity<?> getTestCaseId(
			@RequestHeader("Authorization") String authorization) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

		List<Integer> testCaseIdList = autoTestService.getAllTestCaseId(accessToken);

		if (testCaseIdList != null) {
			return ResponseEntity.ok(testCaseIdList);
		} else {
			return ResponseEntity.badRequest().body("You don't have any Test cases yet.");
		}
	}

	@GetMapping("/monitor/testResult/testStartTime/{testCaseId}")
	public ResponseEntity<?> getTestStartTime(@PathVariable Integer testCaseId) {

		Map<String, Object> testResultList = autoTestService.getTestStartTime(testCaseId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

	@GetMapping("/monitor/testResult/{testCaseId}")
	public ResponseEntity<?> getAllTestResult(@PathVariable Integer testCaseId) {

		List<TestResult> testResultList = autoTestService.getAllTestResult(testCaseId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

	@GetMapping("/monitor/testResult/dashboard/{testCaseId}")
	public ResponseEntity<?> getTenTestResult(@PathVariable Integer testCaseId) {

		List<TestResult> testResultList = autoTestService.getTenTestResult(testCaseId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

	@GetMapping("/monitor/testResult/testTime/{collectionId}")
	public ResponseEntity<?> getCollectionTestTime(@PathVariable Integer collectionId) {

		List<Map<String, Object>> testResultList = autoTestService.getTestTime(collectionId);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

	@GetMapping("/monitor/testResult")
	public ResponseEntity<?> getCollectionTestResult(
			@RequestParam Integer collectionId,
			@RequestParam LocalDate testDate,
			@RequestParam LocalTime testTime) {

		List<CollectionTestResult> testResultList = autoTestService.collectionTestResult(collectionId, testDate, testTime);

		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

	@GetMapping("/monitor/retestResult")
	public ResponseEntity<?> getCollectionRetestResult(@RequestParam Integer collectionTestResultId) {

		List<CollectionTestResult> retestResultList = autoTestService.collectionRetestResult(collectionTestResultId);

		if (retestResultList != null) {
			return ResponseEntity.ok(retestResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

//	@GetMapping("/monitor/testResult/retestResult")
//	public ResponseEntity<?> getCollectionAllTestResult(@RequestParam Integer collectionTestResultId) {
//
//		List<List<TestResult>> testResultList = autoTestService.collectionAllTestResult(collectionId);
//
//		if (testResultList != null) {
//			return ResponseEntity.ok(testResultList);
//		} else {
//			return ResponseEntity.badRequest().body("There is currently no test results.");
//		}
//	}

	private String extractAccessToken(String authorization) {
		String[] parts = authorization.split(" ");
		if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
			return parts[1];
		} else {
			return null;
		}
	}
}

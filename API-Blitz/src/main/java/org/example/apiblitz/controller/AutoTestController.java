package org.example.apiblitz.controller;

import org.example.apiblitz.model.CollectionTestResult;
import org.example.apiblitz.model.TestResult;
import org.example.apiblitz.service.AutoTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
	public ResponseEntity<?> getTestCaseId(@RequestParam Integer userId) {
		List<Integer> testCaseIdList = autoTestService.getAllTestCaseId(userId);
		if (testCaseIdList != null) {
			return ResponseEntity.ok(testCaseIdList);
		} else {
			return ResponseEntity.badRequest().body("You don't have any Test cases yet.");
		}
	}

	@GetMapping("/monitor/testResult/{testCaseId}")
	public ResponseEntity<?> getTestResult(@PathVariable Integer testCaseId) {
		List<TestResult> testResultList = autoTestService.getAllTestResult(testCaseId);
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
	public ResponseEntity<?> getCollectionOnceTestResult(
			@RequestParam Integer collectionId,
			@RequestParam LocalDate testDate,
			@RequestParam LocalTime testTime) {
		List<CollectionTestResult> testResultList = autoTestService.collectionOnceTestResult(collectionId, testDate, testTime);
		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}

	@GetMapping("/monitor/testResult/all")
	public ResponseEntity<?> getCollectionAllTestResult(@RequestParam Integer collectionId) {
		List<List<TestResult>> testResultList = autoTestService.collectionAllTestResult(collectionId);
		if (testResultList != null) {
			return ResponseEntity.ok(testResultList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no test results.");
		}
	}
}

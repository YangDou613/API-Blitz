package org.example.apiblitz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.model.ResetTestCase;
import org.example.apiblitz.model.TestCase;
import org.example.apiblitz.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/1.0/testCase")
public class TestCaseController {

	@Autowired
	TestCaseService testCaseService;

	@GetMapping
	public String testCasePage() {
		return "testCase";
	}

	@GetMapping(path = "/get")
	public ResponseEntity<?> getTestCase(Integer userId) {

		List<NextSchedule> testCaseList = testCaseService.get(userId);

		if (testCaseList != null) {
			return ResponseEntity
					.status(200)
					.body(testCaseList);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping(path = "/create")
	public String createTestCase(@ModelAttribute TestCase testCase) throws JsonProcessingException {
		Integer testCaseId = testCaseService.save(testCase);
		testCaseService.setTestSchedule(testCaseId, testCase);
		return "redirect:/api/1.0/testCase";
	}

	@GetMapping("/resetTestCase")
	public String resetTestCasePage() {
		return "resetTestCase";
	}

	@PostMapping(path = "/update")
	public String updateTestCase(@ModelAttribute ResetTestCase resetTestCase) throws JsonProcessingException {
		return testCaseService.update(resetTestCase);
	}

	@DeleteMapping(path = "delete")
	public String deleteTestCase(Integer testCaseId) {
		return testCaseService.delete(testCaseId);
	}
}

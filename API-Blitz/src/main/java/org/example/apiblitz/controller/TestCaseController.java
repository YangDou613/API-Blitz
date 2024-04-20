package org.example.apiblitz.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.model.ResetTestCase;
import org.example.apiblitz.model.TestCase;
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
	public ResponseEntity<?> getTestCase(Integer userId) {

		List<NextSchedule> testCaseList = testCaseService.get(userId);

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
			@Valid @ModelAttribute TestCase testCase,
			BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			Integer testCaseId = testCaseService.save(testCase);
			testCaseService.setTestSchedule(testCaseId, testCase);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "redirect:/api/1.0/testCase";
	}

	@GetMapping("/resetTestCase")
	public String resetTestCasePage() {
		return "resetTestCase";
	}

	@PostMapping(path = "/update")
	public ResponseEntity<?> updateTestCase(
			@Valid @ModelAttribute ResetTestCase resetTestCase,
			BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			testCaseService.update(resetTestCase);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@DeleteMapping(path = "delete")
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
}

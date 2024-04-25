package org.example.apiblitz.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.AutoTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class APIController {

	@Autowired
	APIService apiService;

	@Autowired
	AutoTestService autoTestService;

	@GetMapping ("/APITest.html")
	public String APITestPage() {
		return "APITest";
	}

	@PostMapping("/APITest.html")
	public ResponseEntity<?> getResponse(@Valid @ModelAttribute APIData apiData, BindingResult bindingResult)
			throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			return apiService.APITest(apiData);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@GetMapping ("/history")
	public String historyPage() {
		return "history";
	}

	@GetMapping("/APITest/history")
	public ResponseEntity<?> getHistory(@RequestParam Integer userId) {
		List<Request> historyList = apiService.getAllHistory(userId);
		if (historyList != null) {
			return ResponseEntity.ok(historyList);
		} else {
			return ResponseEntity.badRequest().body("There is currently no API history.");
		}
	}
}

package org.example.apiblitz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.apiblitz.model.TestCase;
import org.example.apiblitz.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TestCaseController {

	@Autowired
	TestCaseService testCaseService;

	@GetMapping("/testCase.html")
	public String testCasePage() {
		return "testCase";
	}

	@PostMapping("/testCase.html")
	public String saveTestCase(@ModelAttribute TestCase testCase) throws JsonProcessingException {

		testCaseService.setTestSchedule(testCase);

		return "redirect:/testCase.html";
	}
}

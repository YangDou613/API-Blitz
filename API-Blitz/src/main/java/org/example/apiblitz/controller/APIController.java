package org.example.apiblitz.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class APIController {

	@Autowired
	APIService apiService;

	@GetMapping ("/API.html")
	public String APITestPage() {
		return "API";
	}

	@PostMapping("/API.html")
	public ResponseEntity<?> GetResponse(@Valid @ModelAttribute APIData apiData, BindingResult bindingResult)
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
}

package org.example.apiblitz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.apiblitz.model.*;
import org.example.apiblitz.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class APIController {

	@Autowired
	APIService apiService;

	@GetMapping ("/API.html")
	public String APITestPage() {
		return "API";
	}

	@PostMapping("/API.html")
	public ResponseEntity<?> GetResponse(@ModelAttribute APIData apiData) throws JsonProcessingException {

		ResponseEntity<?> response = apiService.APITest(apiData);

		if (apiData.getMethod().equals("HEAD")) {
			return ResponseEntity
					.status(response.getStatusCode())
					.body(response.getHeaders());
		}
		return ResponseEntity
				.status(response.getStatusCode())
				.body(response);
	}
}

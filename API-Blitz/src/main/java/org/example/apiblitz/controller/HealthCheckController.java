package org.example.apiblitz.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("Consumer")
@RestController
public class HealthCheckController {

	@GetMapping("/healthCheck")
	public ResponseEntity<?> healthCheck() {
		return ResponseEntity.ok("ok");
	}
}

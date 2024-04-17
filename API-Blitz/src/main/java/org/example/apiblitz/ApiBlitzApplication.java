package org.example.apiblitz;

import jakarta.annotation.PostConstruct;
import org.example.apiblitz.service.ResetTestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiBlitzApplication {

	@Autowired
	private ResetTestCaseService resetTestCaseService;

	public static void main(String[] args) {
		SpringApplication.run(ApiBlitzApplication.class, args);
	}

	@PostConstruct
	public void init() {
		resetTestCaseService.resetTestCase();
	}

}

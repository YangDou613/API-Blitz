package org.example.apiblitz;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.service.ResetTestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
@Slf4j
public class ApiBlitzApplication {

	@Autowired
	private ResetTestCaseService resetTestCaseService;

	public static void main(String[] args) {
		SpringApplication.run(ApiBlitzApplication.class, args);
	}

	@PostConstruct
	public void init() {
		try {
			resetTestCaseService.resetTestCase();
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

}

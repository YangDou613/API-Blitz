package org.example.apiblitz.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	public ResponseEntity<?> handleBindException() {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body("Please confirm the information you entered is correct.");
	}
}

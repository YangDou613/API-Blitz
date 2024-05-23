package org.example.apiblitz.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.UserResponse;
import org.example.apiblitz.model.UserSignIn;
import org.example.apiblitz.model.UserSignUp;
import org.example.apiblitz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile("Producer")
@Controller
@Slf4j
@RequestMapping("/api/1.0/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/signup")
	@Validated
	public ResponseEntity<?> userSignUp(
			@Valid @RequestBody UserSignUp user,
			BindingResult bindingResult) {

		UserResponse userResponse = new UserResponse();

		if (bindingResult.hasErrors()) {
			userResponse.setError(
					"Please enter your name, email and password, and confirm whether the email or password format " +
					"you entered is correct. The email addresses need to follow the standard format, " +
					"and passwords need to contain at least one letter," +
					" one number, and be at least 8 characters long.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		try {
			userResponse = userService.userSignUp(user);

			if (userResponse.getError() == null) {
				return ResponseEntity.ok(userResponse);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
			}
		} catch (Exception e) {
			return errorMessage(userResponse);
		}
	}

	@PostMapping("/signin")
	@Validated
	public ResponseEntity<?> userSignIn(
			@RequestBody @Valid UserSignIn user,
			BindingResult bindingResult) {

		UserResponse userResponse = new UserResponse();

		if (bindingResult.hasErrors()) {
			userResponse.setError("Please enter your email and password, " +
			                      "and confirm the information you entered is correct.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		try {
			userResponse = userService.userSignIn(user);

			if (userResponse.getError() == null) {
				return ResponseEntity.ok(userResponse);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
			}
		} catch (Exception e) {
			return errorMessage(userResponse);
		}
	}

	public ResponseEntity<?> errorMessage(UserResponse userResponse) {
		userResponse.setError("Internal server error");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userResponse);
	}
}

package org.example.apiblitz.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Collections;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.UserResponse;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.CollectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api/1.0/collections")
public class CollectionsController {

	@Autowired
	CollectionsService collectionService;

	@Autowired
	APIService apiService;

	@GetMapping
	public String collectionsPage() {
		return "collections";
	}

	@GetMapping(path = "/details")
	public String collectionDetailsPage(
			@RequestParam("collectionName") String collectionName,
			@RequestParam("collectionId") Integer collectionId) {
		return "collectionDetails";
	}

	@GetMapping(path = "/get")
//	public ResponseEntity<?> getCollections(Integer userId) {
	public ResponseEntity<?> getCollections(
			@RequestHeader("Authorization") String authorization) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

//		List<Map<String, Object>> collectionList = collectionService.get(userId);
		List<Map<String, Object>> collectionList = collectionService.get(accessToken);

		if (collectionList != null) {
			return ResponseEntity
					.ok()
					.body(collectionList);
		} else {
			return ResponseEntity.badRequest().body("You don't have any collections yet.");
		}
	}

	@PostMapping(path = "/create")
	public String createCollection(
//			Integer userId,
			@RequestHeader("Authorization") String authorization,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return "redirect:/api/1.0/collections";
		}

		String accessToken = extractAccessToken(authorization);

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
//			collectionService.create(userId, collection);
			collectionService.create(accessToken, collection);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "redirect:/api/1.0/collections";
	}

	@PostMapping(path = "/create/addAPI")
	public ResponseEntity<?> addAPIToCollection(
			Integer collectionId,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.add(collectionId, collection);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@PostMapping(path = "/create/addHistoryAPI")
	public ResponseEntity<?> addHistoryAPIToCollection(
			Integer collectionId,
			@RequestBody Collections collection,
			BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.add(collectionId, collection);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@PostMapping(path = "/update")
	public ResponseEntity<?> updateCollection(
			Integer collectionId,
			@ModelAttribute Collections collection,
			BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.update(collectionId, collection);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@DeleteMapping(path = "/delete")
	public ResponseEntity<?> deleteCollection(
//			Integer userId,
			@RequestHeader("Authorization") String authorization,
			String collectionName,
			@RequestParam(required = false) Integer requestId) {

		UserResponse userResponse = new UserResponse();

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			userResponse.setError("Invalid or missing Bearer token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponse);
		}

		String accessToken = extractAccessToken(authorization);

		try {
//			collectionService.delete(userId, collectionName, requestId);
			collectionService.delete(accessToken, collectionName, requestId);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	@GetMapping(path = "/getAllAPI")
	public ResponseEntity<List<Request>> getAllAPI(Integer collectionId) {

		List<Request> apiList = collectionService.getAPIList(collectionId);

		if (apiList != null) {
			return ResponseEntity.ok().body(apiList);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/testAll")
	public ResponseEntity<?> getResponseAtSameTime(
			Integer collectionId,
			@RequestBody List<Request> requests) {

		System.out.println("hooooo");

		try {
			Map<String, Object> testTime = collectionService.sendRequestAtSameTime(collectionId, requests);

			if (testTime == null) {
				return ResponseEntity.badRequest().build();
			}

			return ResponseEntity.status(200).body(testTime);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

//	@PostMapping("/testAll")
//	public ResponseEntity<?> getResponseAtSameTime(
//			Integer collectionId,
//			@RequestBody List<Request> requests) {
//
//		try {
//			List<ResponseEntity<?>> responseList = collectionService.sendRequestAtSameTime(collectionId, requests);
//
//			if (responseList == null) {
//				return ResponseEntity.badRequest().build();
//			}
//
//			return ResponseEntity.ok(responseList);
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
//		}
//	}

	private String extractAccessToken(String authorization) {
		String[] parts = authorization.split(" ");
		if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
			return parts[1];
		} else {
			return null;
		}
	}
}

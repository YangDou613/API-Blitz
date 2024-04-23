package org.example.apiblitz.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.Collection;
import org.example.apiblitz.service.CollectionService;
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
@RequestMapping("/api/1.0/collection")
public class CollectionController {

	@Autowired
	CollectionService collectionService;

	@GetMapping(path = "/get")
	public ResponseEntity<?> getCollections(Integer userId) {

		List<Map<String, Object>> collectionList = collectionService.get(userId);

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
			Integer userId,
			@ModelAttribute Collection collection,
			BindingResult bindingResult) throws BindException {

		if (bindingResult.hasErrors()) {
			throw new BindException(bindingResult);
		}

		try {
			collectionService.create(userId, collection);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "redirect:/api/1.0/collection";
	}

	@PostMapping(path = "/update")
	public ResponseEntity<?> updateCollection(
			Integer collectionId,
			@ModelAttribute Collection collection,
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

	@PostMapping(path = "/update/addAPI")
	public ResponseEntity<?> addAPIToCollection(
			Integer collectionId,
			@ModelAttribute Collection collection,
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

	@DeleteMapping(path = "/delete")
	public ResponseEntity<?> deleteCollection(
			Integer userId,
			String collectionName,
			@RequestParam(required = false) String requestName) {

		try {
			collectionService.delete(userId, collectionName, requestName);
			return ResponseEntity
					.ok()
					.build();
		} catch (Exception e) {
			log.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
}

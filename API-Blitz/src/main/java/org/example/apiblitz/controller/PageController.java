package org.example.apiblitz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

	@GetMapping("/APITest")
	public String APITestPage() {
		return "APITest";
	}

	@GetMapping("/history")
	public String historyPage() {
		return "history";
	}

	@GetMapping("/collections")
	public String collectionsPage() {
		return "collections";
	}

	@GetMapping(path = "/collections/details")
	public String collectionDetailsPage(
			@RequestParam("collectionName") String collectionName,
			@RequestParam("collectionId") Integer collectionId) {
		return "collectionDetails";
	}

	@GetMapping("/monitor")
	public String modifyTestCasePage() {
		return "monitor";
	}

	@GetMapping("/report")
	public String monitorPage() {
		return "report";
	}

	@GetMapping(path = "signUpIn")
	public String signUpSignInPage() {
		return "signUpIn";
	}
}

package org.example.apiblitz.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.apiblitz.repository.AutoTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class AutoTestService {

	@Autowired
	AutoTestRepository autoTestRepository;

	@Autowired
	APIService apiService;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public String getCompareResult(Integer testCaseId,
	                               Integer statusCode,
	                               Map<String, Object> responseBody) throws IOException {

		// Get expected status code
		Integer expectedStatusCode = autoTestRepository.getExpectedStatusCode(testCaseId);

		if (!statusCode.equals(expectedStatusCode)) return "failed";

		// Get expected response body
		String expectedResponseBodyString = autoTestRepository.getExpectedResponseBody(testCaseId);
		Map<String, Object> expectedResponseBody = objectMapper.readValue(expectedResponseBodyString, new TypeReference<>() {});

		if (expectedResponseBody.size() != responseBody.size()) return "failed";

		// Get key list
		Set<String> keys = expectedResponseBody.keySet();

		for (String key : keys) {
			System.out.println(expectedResponseBody.get(key));
			System.out.println(responseBody.get(key));
			if (!expectedResponseBody.get(key).equals(responseBody.get(key))) {
				return "failed";
			}
		}

		return "pass";
	}
}

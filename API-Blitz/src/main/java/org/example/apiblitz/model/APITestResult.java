package org.example.apiblitz.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class APITestResult {
	private Integer id;
	private Integer userId;
	private String APIUrl;
	private String method;
	private String queryParams;
	private String headers;
	private String body;
	private LocalDateTime testDateTime;
	private String responseHeaders;
	private String responseBody;
	private Integer statusCode;
}

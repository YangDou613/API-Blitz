package org.example.apiblitz.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TestResult {
	private Integer testCaseId;
	private LocalDate testDate;
	private LocalTime testTime;
	private Integer statusCode;
	private Integer executionDuration;
	private Integer contentLength;
	private Object responseHeaders;
	private Object responseBody;
	private String result;
}

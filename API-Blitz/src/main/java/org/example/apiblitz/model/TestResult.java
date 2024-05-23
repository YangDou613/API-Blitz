package org.example.apiblitz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResult {
	private Integer testCaseId;
	private Integer collectionId;
	private Integer collectionDetailsId;
	private String testOptions;
	private LocalDate testDate;
	private LocalTime testTime;
	private Integer statusCode;
	private Integer executionDuration;
	private Integer contentLength;
	private Object responseHeaders;
	private Object responseBody;
	private String result;
}

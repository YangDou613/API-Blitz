package org.example.apiblitz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionTestResult {
	private Integer id;
	private Integer collectionId;
	private Integer collectionDetailsId;
	private Integer collectionTestResultId;
	private String requestName;
	private String method;
	private String apiurl;
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

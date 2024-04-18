package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NextSchedule {
	private Integer testCaseId;
	private String APIUrl;
	private String method;
	private Object queryParams;
	private Object headers;
	private Object body;
	private Integer statusCode;
	private Object expectedResponseBody;
	private String intervalsTimeUnit;
	private Integer intervalsTimeValue;
	private Integer notification;
	private Object recipientEmail;
	private LocalDate nextTestDate;
	private LocalTime  nextTestTime;
	private Integer resetStatus;
}

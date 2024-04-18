package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.*;
import org.example.apiblitz.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TestCaseService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	APIService apiService;

	@Autowired
	TestCaseRepository testCaseRepository;

	@Autowired
	AutoTestService autoTestService;

	public Integer save(TestCase testCase) throws JsonProcessingException {

		// Set testCase data in APIData
		APIData apiData = setAPIData(testCase);

		// Package API data into http request
		Request request = apiService.httpRequest(apiData);

		// Store API data into APIHistory table and return testCaseId
		return testCaseRepository.insertToTestCase(request, testCase);
	}

	public void setTestSchedule(Integer testCaseId, TestCase testCase) throws JsonProcessingException {

		LocalDate testDate = LocalDate.now(); // Test Date
		LocalTime testTime = LocalTime.now(); // Test time

		// Confirm the test case id exist or not
		boolean testCaseIdExist = testCaseRepository.isTestCaseIdExistInNextTestSchedule(testCaseId);

		// Store to next test schedule table
		if (!testCaseIdExist) {
			testCaseRepository.insertToNextTestSchedule(testCaseId, testDate, testTime);
		}

		// Set test schedule
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Runnable test = () -> {
			try {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				// Confirm the reset status
				Integer resetStatus = testCaseRepository.getResetStatusByTestCaseId(testCaseId);
				if (resetStatus == 0) {
					autoTestService.autoTest(testCaseId);
					log.info(timestamp + " : testCaseId <" + testCaseId + "> Finish testing!");
				} else {
					executor.shutdown();
					log.info(timestamp + " : testCaseId <" + testCaseId + "> Shutdown!");
					if (resetStatus == 1) {
						executor.shutdown();
						setTestSchedule(testCaseId, testCase);
						testCaseRepository.updateResetStatusByTestCaseId(testCaseId);
						log.info(timestamp + " : testCaseId <" + testCaseId + "> Reset Successfully!");
					}
				}

				LocalDate nextTestDate = timestamp.toLocalDateTime().toLocalDate();
				LocalTime nextTestTime = timestamp.toLocalDateTime().toLocalTime();

				LocalTime originalTime = nextTestTime;

				switch(testCase.getIntervalsTimeUnit()) {
					case "Hour":
						nextTestTime = nextTestTime.plusHours(testCase.getIntervalsTimeValue());
						if (nextTestTime.isBefore(originalTime)) {
							nextTestDate = nextTestDate.plusDays(1);
						}
						break;
					case "Day":
						nextTestTime = nextTestTime.plusHours(testCase.getIntervalsTimeValue() * 24);
						nextTestDate = nextTestDate.plusDays(testCase.getIntervalsTimeValue());
						break;
					case "Sec":
						nextTestTime = nextTestTime.plusSeconds(testCase.getIntervalsTimeValue());
						if (nextTestTime.isBefore(originalTime)) {
							nextTestDate = nextTestDate.plusDays(1);
						}
						break;
				}

				testCaseRepository.updateNextTestTime(testCaseId, nextTestDate, nextTestTime);

			} catch (IOException | UnirestException e) {
				throw new RuntimeException(e);
			}
		};

		// Get intervals time unit
		String intervalsTimeUnit = testCase.getIntervalsTimeUnit();

		TimeUnit timeUnit = null;

		switch(intervalsTimeUnit) {
			case "Hour":
				timeUnit = TimeUnit.HOURS;
				break;
			case "Day":
				timeUnit = TimeUnit.DAYS;
				break;
			case "Sec":
				timeUnit = TimeUnit.SECONDS;
				break;
		}

		// Get intervals time value
		Integer intervalsTimeValue = testCase.getIntervalsTimeValue();

		executor.scheduleAtFixedRate(test, 0, intervalsTimeValue, timeUnit);
	}

	public List<NextSchedule> get(Integer userId) {
		try {
			return testCaseRepository.getTestCase(userId);
		} catch (Exception e) {
			log.info(e.getMessage());
			return null;
		}
	}

	public String update(ResetTestCase resetTestCase) throws JsonProcessingException {

		// Set testCase data in APIData
		APIData apiData = resetAPIData(resetTestCase);

		// Package API data into http request
		Request request = apiService.httpRequest(apiData);

		// Update API data in APIHistory table
		try {
			testCaseRepository.updateTestCase(request, resetTestCase);
			return "Update successfully!";
		} catch (Exception e) {
			log.info(e.getMessage());
			return "Error";
		}
	}

	public String delete(Integer testCaseId) {

		// Delete API data in APIHistory table
		try {
			testCaseRepository.deleteTestCase(testCaseId);
			return "Delete successfully!";
		} catch (Exception e) {
			log.info(e.getMessage());
			return "Error";
		}
	}

	public APIData setAPIData(TestCase testCase) {

		APIData apiData = new APIData();

		apiData.setMethod(testCase.getMethod());
		apiData.setUrl(testCase.getUrl());
		apiData.setParamsKey(testCase.getParamsKey());
		apiData.setParamsValue(testCase.getParamsValue());
		apiData.setAuthorizationKey(testCase.getAuthorizationKey());
		apiData.setAuthorizationValue(testCase.getAuthorizationValue());
		apiData.setHeadersKey(testCase.getHeadersKey());
		apiData.setHeadersValue(testCase.getHeadersValue());
		apiData.setBody(testCase.getBody());

		return apiData;
	}

	public APIData resetAPIData(ResetTestCase resetTestCase) {

		APIData apiData = new APIData();

		apiData.setMethod(resetTestCase.getMethod());
		apiData.setUrl(resetTestCase.getUrl());
		apiData.setParamsKey(resetTestCase.getParamsKey());
		apiData.setParamsValue(resetTestCase.getParamsValue());
		apiData.setAuthorizationKey(resetTestCase.getAuthorizationKey());
		apiData.setAuthorizationValue(resetTestCase.getAuthorizationValue());
		apiData.setHeadersKey(resetTestCase.getHeadersKey());
		apiData.setHeadersValue(resetTestCase.getHeadersValue());
		apiData.setBody(resetTestCase.getBody());

		return apiData;
	}
}

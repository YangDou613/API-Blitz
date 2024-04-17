package org.example.apiblitz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.APIData;
import org.example.apiblitz.model.Request;
import org.example.apiblitz.model.TestCase;
import org.example.apiblitz.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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

	public void setTestSchedule(TestCase testCase) throws JsonProcessingException {

		// Store test case data into testCase table
		Integer testCaseId = save(testCase);

		// Store to next test schedule table
		LocalDate testDate = LocalDate.now(); // Test Date
		LocalTime testTime = LocalTime.now(); // Test time
		LocalTime nextTestTime = testTime.plusSeconds(testCase.getIntervalsTimeValue());

		testCaseRepository.insertToNextTestSchedule(testCaseId, testDate, nextTestTime);

		// Set test schedule
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Runnable test = () -> {
			try {
				autoTestService.autoTest(testCaseId);
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				log.info(timestamp + " : testCaseId <" + testCaseId + "> Finish testing!");
				log.info("--------------------------------------------------------------");
			} catch (IOException e) {
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

	public Integer save(TestCase testCase) throws JsonProcessingException {

		// Set testCase data in APIData
		APIData apiData = setAPIData(testCase);

		// Package API data into http request
		Request request = apiService.httpRequest(apiData);

		// Store API data into APIHistory table and return testCaseId
		return testCaseRepository.insertToTestCase(request, testCase);
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
}

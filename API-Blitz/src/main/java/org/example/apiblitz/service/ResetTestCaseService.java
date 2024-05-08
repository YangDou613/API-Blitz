package org.example.apiblitz.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.repository.ResetTestCaseRepository;
import org.example.apiblitz.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Profile("Producer")
@Service
@Slf4j
public class ResetTestCaseService {

	final
	ResetTestCaseRepository resetTestCaseRepository;

	final
	TestCaseRepository testCaseRepository;

	final
	AutoTestService autoTestService;

	final
	Publisher publisher;

	public ResetTestCaseService(ResetTestCaseRepository resetTestCaseRepository, TestCaseRepository testCaseRepository, AutoTestService autoTestService, Publisher publisher) {
		this.resetTestCaseRepository = resetTestCaseRepository;
		this.testCaseRepository = testCaseRepository;
		this.autoTestService = autoTestService;
		this.publisher = publisher;
		try {
			this.resetTestCase();
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	public void resetTestCase() throws SQLException {

		// Get all exist test case
		List<NextSchedule> nextSchedules = resetTestCaseRepository.getAllNextSchedule();

		for (NextSchedule nextSchedule : nextSchedules) {

			// Get current time
			LocalDateTime currentDateTime = LocalDateTime.now();

			// Get the next test date and time in the database
			LocalDateTime nextTestDateTime =
					LocalDateTime.of(nextSchedule.getNextTestDate(), nextSchedule.getNextTestTime());

			if (nextTestDateTime.isBefore(currentDateTime)) {
				nextTestDateTime = updateNextTestTime(nextSchedule, nextTestDateTime, currentDateTime);
				testCaseRepository.updateNextTestTime(
						nextSchedule.getTestCaseId(),
						nextTestDateTime.toLocalDate(),
						nextTestDateTime.toLocalTime());
			}

			// Set test schedule
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			Runnable test = () -> {
				try {
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());

					// User ID
					Integer userId = null;

					// Category
					String category = "TestCase";

					// ID
					Integer id = nextSchedule.getTestCaseId();

					// Test dateTime
					LocalDateTime currentDateTimeForTest = LocalDateTime.now().withNano(0);
					Timestamp testDateTime = Timestamp.valueOf(currentDateTimeForTest);

					// Content
					Object content = null;

					publisher.publishMessage(userId, category, id, testDateTime, content);

//					autoTestService.autoTest(nextSchedule.getTestCaseId());
					log.info(timestamp + " : testCaseId <" + nextSchedule.getTestCaseId() + "> Set Schedule Successfully!");

					// Calculate next test date and time
					LocalDateTime updateNextTestDateTime = calculateNextTestTime(nextSchedule, timestamp);

					testCaseRepository.updateNextTestTime(
							nextSchedule.getTestCaseId(),
							updateNextTestDateTime.toLocalDate(),
							updateNextTestDateTime.toLocalTime());

				} catch (SQLException e) {
					log.error(e.getMessage());
				}
			};

			// Get intervals time unit
			String intervalsTimeUnit = nextSchedule.getIntervalsTimeUnit();

			// Get intervals time value
			Integer intervalsTimeValue = nextSchedule.getIntervalsTimeValue();

			TimeUnit timeUnit = null;
			long initialDelay = 0;

			switch (intervalsTimeUnit) {
				case "Hour":
					timeUnit = TimeUnit.HOURS;
					initialDelay = ChronoUnit.HOURS.between(currentDateTime, nextTestDateTime);
					break;
				case "Day":
					timeUnit = TimeUnit.DAYS;
					initialDelay = ChronoUnit.DAYS.between(currentDateTime, nextTestDateTime);
					break;
				case "Sec":
					timeUnit = TimeUnit.SECONDS;
					initialDelay = ChronoUnit.SECONDS.between(currentDateTime, nextTestDateTime);
					break;
			}

			executor.scheduleAtFixedRate(test, initialDelay, intervalsTimeValue, timeUnit);

			testCaseRepository.updateResetStatusByTestCaseId(nextSchedule.getTestCaseId());
		}
	}

	public LocalDateTime updateNextTestTime(NextSchedule nextSchedule,
	                                        LocalDateTime nextTestDateTime,
	                                        LocalDateTime currentDateTime) {

		LocalTime originalTestTime;
		LocalDate nextTestDate = nextTestDateTime.toLocalDate();
		LocalTime nextTestTime = nextTestDateTime.toLocalTime();
		LocalDateTime updateNextTestTime = nextTestDateTime;

		while (updateNextTestTime.isBefore(currentDateTime)) {

			originalTestTime = nextTestTime;

			switch (nextSchedule.getIntervalsTimeUnit()) {
				case "Hour":
					nextTestTime = nextTestTime.plusHours(nextSchedule.getIntervalsTimeValue());
					if (nextTestTime.isBefore(originalTestTime)) {
						nextTestDate = nextTestDate.plusDays(1);
					}
					break;
				case "Day":
					nextTestTime = nextTestTime.plusHours(nextSchedule.getIntervalsTimeValue() * 24);
					nextTestDate = nextTestDate.plusDays(nextSchedule.getIntervalsTimeValue());
					break;
				case "Sec":
					nextTestTime = nextTestTime.plusSeconds(nextSchedule.getIntervalsTimeValue());
					if (nextTestTime.isBefore(originalTestTime)) {
						nextTestDate = nextTestDate.plusDays(1);
					}
					break;
			}

			updateNextTestTime = LocalDateTime.of(nextTestDate, nextTestTime);
		}

		return updateNextTestTime;
	}

	public LocalDateTime calculateNextTestTime(NextSchedule nextSchedule, Timestamp timestamp) {

		LocalDate nextTestDate = timestamp.toLocalDateTime().toLocalDate();
		LocalTime nextTestTime = timestamp.toLocalDateTime().toLocalTime();

		LocalTime originalTime = nextTestTime;

		switch(nextSchedule.getIntervalsTimeUnit()) {
			case "Hour":
				nextTestTime = nextTestTime.plusHours(nextSchedule.getIntervalsTimeValue());
				if (nextTestTime.isBefore(originalTime)) {
					nextTestDate = nextTestDate.plusDays(1);
				}
				break;
			case "Day":
				nextTestTime = nextTestTime.plusHours(nextSchedule.getIntervalsTimeValue() * 24);
				nextTestDate = nextTestDate.plusDays(nextSchedule.getIntervalsTimeValue());
				break;
			case "Sec":
				nextTestTime = nextTestTime.plusSeconds(nextSchedule.getIntervalsTimeValue());
				if (nextTestTime.isBefore(originalTime)) {
					nextTestDate = nextTestDate.plusDays(1);
				}
				break;
		}

		return LocalDateTime.of(nextTestDate, nextTestTime);
	}
}

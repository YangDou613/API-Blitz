package org.example.apiblitz.service;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.repository.ResetTestCaseRepository;
import org.example.apiblitz.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ResetTestCaseService {

	@Autowired
	ResetTestCaseRepository resetTestCaseRepository;

	@Autowired
	TestCaseRepository testCaseRepository;

	@Autowired
	AutoTestService autoTestService;

	public void resetTestCase() {

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
					autoTestService.autoTest(nextSchedule.getTestCaseId());
					log.info(timestamp + " : testCaseId <" + nextSchedule.getTestCaseId() + "> Finish testing!");

					// Calculate next test date and time
					LocalDateTime updateNextTestDateTime = calculateNextTestTime(nextSchedule, timestamp);

					testCaseRepository.updateNextTestTime(
							nextSchedule.getTestCaseId(),
							updateNextTestDateTime.toLocalDate(),
							updateNextTestDateTime.toLocalTime());

				} catch (IOException e) {
					throw new RuntimeException(e);
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

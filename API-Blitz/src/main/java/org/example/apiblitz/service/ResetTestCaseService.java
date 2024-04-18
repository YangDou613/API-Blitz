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
			LocalDate currentTestDate = LocalDate.now();
			LocalTime currentTestTime = LocalTime.now();
			LocalDateTime currentDateTime = LocalDateTime.of(currentTestDate, currentTestTime);

			// Get the next test date and time in the database
			LocalDate originNextTestDate = nextSchedule.getNextTestDate();
			LocalTime originNextTestTime = nextSchedule.getNextTestTime();
			LocalDateTime originNextTestDateTime = LocalDateTime.of(originNextTestDate, originNextTestTime);

			LocalTime originalTestTime;

			while (originNextTestDateTime.isBefore(currentDateTime)) {

				originalTestTime = originNextTestTime;

				switch (nextSchedule.getIntervalsTimeUnit()) {
					case "Hour":
						originNextTestTime = originNextTestTime.plusHours(nextSchedule.getIntervalsTimeValue());
						if (originNextTestTime.isBefore(originalTestTime)) {
							originNextTestDate = originNextTestDate.plusDays(1);
						}
						break;
					case "Day":
						originNextTestTime = originNextTestTime.plusHours(nextSchedule.getIntervalsTimeValue() * 24);
						originNextTestDate = originNextTestDate.plusDays(nextSchedule.getIntervalsTimeValue());
						break;
					case "Sec":
						originNextTestTime = originNextTestTime.plusSeconds(nextSchedule.getIntervalsTimeValue());
						if (originNextTestTime.isBefore(originalTestTime)) {
							originNextTestDate = originNextTestDate.plusDays(1);
						}
						break;
				}

				originNextTestDateTime = LocalDateTime.of(originNextTestDate, originNextTestTime);

			}

			// Set test schedule
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			Runnable test = () -> {
				try {
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					autoTestService.autoTest(nextSchedule.getTestCaseId());
					log.info(timestamp + " : testCaseId <" + nextSchedule.getTestCaseId() + "> Finish testing!");

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

					testCaseRepository.updateNextTestTime(nextSchedule.getTestCaseId(), nextTestDate, nextTestTime);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};

			// Get intervals time unit
			String intervalsTimeUnit = nextSchedule.getIntervalsTimeUnit();

			TimeUnit timeUnit = null;

			switch (intervalsTimeUnit) {
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
			Integer intervalsTimeValue = nextSchedule.getIntervalsTimeValue();

			executor.scheduleAtFixedRate(test, 0, intervalsTimeValue, timeUnit);

			testCaseRepository.updateResetStatusByTestCaseId(nextSchedule.getTestCaseId());
		}
	}
}

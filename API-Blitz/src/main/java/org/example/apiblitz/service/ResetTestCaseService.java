package org.example.apiblitz.service;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.model.NextSchedule;
import org.example.apiblitz.repository.ResetTestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
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
	AutoTestService autoTestService;

	public void resetTestCase() {

		// Get all exist test case
		List<NextSchedule> nextSchedules = resetTestCaseRepository.getAllNextSchedule();

		for (NextSchedule nextSchedule : nextSchedules) {

			// Set test schedule
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			Runnable test = () -> {
				try {
					autoTestService.autoTest(nextSchedule.getTestCaseId());
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					log.info(timestamp + " : testCaseId <" + nextSchedule.getTestCaseId() + "> Finish testing!");
					log.info("--------------------------------------------------------------");
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
		}
	}
}

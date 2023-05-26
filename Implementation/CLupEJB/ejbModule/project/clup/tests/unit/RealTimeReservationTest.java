package project.clup.tests.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.RealTimeReservation;
import project.clup.exceptions.BadReservationException;

class RealTimeReservationTest {

	RealTimeReservation r;
	
	@BeforeEach
	void setUp() throws Exception {
		r = new RealTimeReservation();
		assertNotNull(r);
	}

	@AfterEach
	void tearDown() throws Exception {
		this.r = null;
		assertNull(r);
	}

	@Test
	void testGetExitTimeWithEntranceTime() throws BadReservationException {
		r.setEntranceTime(Time.valueOf("00:00:00"));
		r.setVisitDuration(60);
		Time expected = Time.valueOf("01:00:00");
		Time actual = Time.valueOf(r.getExitTime());
		assertEquals(expected, actual);
	}
	
	@Test
	void testGetExitTimeWithETE() throws BadReservationException {
		LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
		r.setETE(now);
		r.setVisitDuration(60);
		LocalTime expected = now.plusMinutes(60);
		LocalTime actual = r.getExitTime();
		assertEquals(expected, actual);
	}

}

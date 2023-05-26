package project.clup.tests.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.PlannedReservation;
import project.clup.entities.RealTimeReservation;

class PlannedReservationTest {

	PlannedReservation p;
	
	@BeforeEach
	void setUp() throws Exception {
		p = new PlannedReservation();
		assertNotNull(p);
	}

	@AfterEach
	void tearDown() throws Exception {
		this.p = null;
		assertNull(p);
	}

	@Test
	void testGetExitTime() {
		p.setStartTime(Time.valueOf("00:00:00"));
		p.setVisitDuration(60);
		Time expected = Time.valueOf("01:00:00");
		Time actual = Time.valueOf(p.getExitTime());
		assertEquals(expected, actual);
	}

}

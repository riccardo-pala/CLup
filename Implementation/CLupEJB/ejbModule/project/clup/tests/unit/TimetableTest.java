package project.clup.tests.unit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import project.clup.entities.PlannedReservation;
import project.clup.exceptions.BadReservationException;
import project.clup.utilities.Timetable;

class TimetableTest {

	Timetable timetable;
	//Supermarket s;
	ArrayList<PlannedReservation> reservations;
	
	@BeforeEach
	void setUp() throws Exception {
		reservations = new ArrayList<PlannedReservation>();
		reservations.add(new PlannedReservation(null, null, 0, null, null, Time.valueOf("00:00:00")));
		reservations.add(new PlannedReservation(null, null, 0, Time.valueOf("00:00:00"), null, Time.valueOf("00:00:00")));
		reservations.add(new PlannedReservation(null, null, 0, null, null, Time.valueOf("00:00:00")));
		reservations.add(new PlannedReservation(null, null, 0, Time.valueOf("00:00:00"), null, Time.valueOf("00:00:00")));
		timetable = new Timetable();
		assertNotNull(timetable);
	}

	@AfterEach
	void tearDown() throws Exception {
		timetable = null;
		assertNull(timetable);
	}

	@Test
	void testComputeSchedules() throws BadReservationException {
		timetable.computeSchedules(reservations);
		assertTrue(timetable.getSchedule().get(0).size() == 4);
	}

	@Test
	void testGetAllScheduledNotScanned() throws BadReservationException {
		timetable.computeSchedules(reservations);
		LocalTime start = Time.valueOf("00:00:00").toLocalTime();
		LocalTime end = Time.valueOf("23:59:00").toLocalTime();
		assertTrue(timetable.getAllScheduledNotScanned(start, end).size() == 2);
	}
	
	@Nested
	class WhenStartTimeIsNull {
		
		@BeforeEach
		void setUp() throws Exception {
			reservations = new ArrayList<PlannedReservation>();
			reservations.add(new PlannedReservation(null, null, 0, null, null, null));
			timetable = new Timetable();
			assertNotNull(timetable);
		}
		
		@Test
		void testComputeSchedules() {
			try{
				timetable.computeSchedules(reservations);
				fail("StartTime must be not null");
			} catch(BadReservationException e) {
			}
			
		}
	}
}

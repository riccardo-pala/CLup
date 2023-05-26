package project.clup.tests.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.PlannedReservation;
import project.clup.entities.RealTimeReservation;
import project.clup.entities.Supermarket;
import project.clup.exceptions.BadReservationException;

class SupermarketTest {

	private Supermarket s;
	
	@BeforeEach
	void setUp() throws Exception {
		s = new Supermarket();
		assertNotNull(s);
		s.addReservation(new RealTimeReservation(s, null, 0, Time.valueOf("00:00:00"), null, 0));
		s.addReservation(new PlannedReservation(s, null, 0, Time.valueOf("00:00:00"), null, Time.valueOf("00:00:00")));
		s.addReservation(new RealTimeReservation(s, null, 0, null, null, 1000));
		s.addReservation(new PlannedReservation(s, null, 0, null, null, Time.valueOf("00:00:00")));
	}

	@AfterEach
	void tearDown() throws Exception {
		s.getReservations().clear();
		assertTrue(s.getReservations().isEmpty());
	}

	@Test
	void testAddReservation() {
		assertTrue(s.getReservations().size() == 4);
	}

	@Test
	void testRemoveReservation() {
		s.getReservations().remove(0);
		s.getReservations().remove(s.getReservations().size()-1);
		assertTrue(s.getReservations().size() == 2);
	}

	@Test
	void testGetTimetable() throws BadReservationException {
		// given this is a unit test, it can invoke only Supermarket methods
		// therefore Timetable.schedule.size cannot be tested
		assertTrue(s.getTimetable().size() == 2); 
	}

	@Test
	void testGetVirtualLineUp() {
		assertTrue(s.getVirtualLineUp().size() == 1);
	}

	@Test
	void testGetDoingGroceryShopping() {
		assertTrue(s.getDoingGroceryShopping().size() == 2);
	}

}

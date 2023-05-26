package project.clup.tests.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;

import org.junit.jupiter.api.Test;

import project.clup.entities.PlannedReservation;
import project.clup.entities.RealTimeReservation;
import project.clup.entities.Reservation;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.ReservationService;

class ReservationServiceTest {
	

	@Test
	void testExpiredReservation() {
		TestReservationService service = new TestReservationService();
		char p='p';
		char r='r';
		try {
			assertEquals(true,service.expiredReservation(1,p));
		} catch (BadRetrievalException e) {
			
			e.printStackTrace();
		}
		
		try {
			assertEquals(true,service.expiredReservation(2,r));
		} catch (BadRetrievalException e) {
			
			e.printStackTrace();
		}
	}
	
	class TestReservationService extends ReservationService {
		private PlannedReservation planned;
		private RealTimeReservation real;
		
		public TestReservationService() {
			planned=new PlannedReservation();
			real=new RealTimeReservation();
			planned.setCode(1);
			planned.setEntranceTime(null);
			planned.setMaxToleratedDelay(Time.valueOf("12:10:00"));
			real.setCode(2);
			real.setEntranceTime(null);
			real.setMaxToleratedDelay(Time.valueOf("12:10:00"));
		}
		
		public Reservation findReservationById(int reservationId) {
		
			if(reservationId==planned.getCode())
			return planned;
			else if(reservationId==real.getCode())
			return real;
			else 
			return null;
			}
		
		
		
	}

}

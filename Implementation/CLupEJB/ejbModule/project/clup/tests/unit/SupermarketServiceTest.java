package project.clup.tests.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.Test;

import project.clup.entities.PlannedReservation;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.TimetableException;
import project.clup.services.SupermarketService;

class SupermarketServiceTest {
	
	

	@Test
	void testGetSchedule() throws BadReservationException {
		
		TestSupermarketService service = new TestSupermarketService();
		List<Time> list=null;
		try {
			list = service.getSchedule(1);
		} catch (BadRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*tested at 15:52, if you need to test in another time you just need
		 * to change 19 as parameters of assertEquals method with the number of expected
		 * time slots from the hour the test is taken plus two hour (each time slot has a duration of 20 min) */
		
		assertEquals(15,list.size());
		
		
	}
	
	@Test
	void testCheckAvailability() throws BadRetrievalException, TimetableException, BadReservationException {
		
		Supermarket supermarket = new Supermarket();
		supermarket.setId(1);
		supermarket.setMaxCapacity(4);
		supermarket.setClosingtime(Time.valueOf("23:00:00"));
		supermarket.setOpeningtime(Time.valueOf("10:00:00"));
	
		TestSupermarketService service = new TestSupermarketService();
		assertEquals(false,service.checkAvailability(1,Time.valueOf("12:00:00"),20));

	}
	
	class TestSupermarketService extends SupermarketService{
		private Supermarket supermarket;
		
		
		
		
		public TestSupermarketService() throws BadReservationException {
			
			supermarket=new Supermarket();
			supermarket.setId(1);
			supermarket.setMaxCapacity(4);
			supermarket.setClosingtime(Time.valueOf("23:00:00"));
			supermarket.setOpeningtime(Time.valueOf("10:00:00"));
			PlannedReservation planned1= new PlannedReservation(supermarket,new User(),20,Time.valueOf("12:00:00"),Time.valueOf("12:10:00"),Time.valueOf("12:00:00"));
			PlannedReservation planned2= new PlannedReservation(supermarket,new User(),20,Time.valueOf("12:00:00"),Time.valueOf("12:10:00"),Time.valueOf("12:00:00"));
			PlannedReservation planned3= new PlannedReservation(supermarket,new User(),20,Time.valueOf("12:00:00"),Time.valueOf("12:10:00"),Time.valueOf("12:00:00"));
			PlannedReservation planned4= new PlannedReservation(supermarket,new User(),20,Time.valueOf("12:00:00"),Time.valueOf("12:10:00"),Time.valueOf("12:00:00"));
			supermarket.addReservation(planned1);
			supermarket.addReservation(planned2);
			supermarket.addReservation(planned3);
			supermarket.addReservation(planned4);
			supermarket.getTimetable();
			
			
		}




		public Supermarket findsupermarketbyId(int supermarketId) {
			if(supermarket.getId()==supermarketId) {
				return supermarket;
			}
			return null;
		
	}

	}
}
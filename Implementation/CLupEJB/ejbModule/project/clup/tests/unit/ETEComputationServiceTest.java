package project.clup.tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.RealTimeReservation;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadReservationException;
import project.clup.services.ETEComputationService;

class ETEComputationServiceTest {

	private Supermarket supermarket;
	private ETEComputationService service; 
	
	@BeforeEach
	public void setUp() {
		
		service=new ETEComputationService();
		supermarket=new Supermarket();
		supermarket.setMaxCapacity(4);
		RealTimeReservation rel1 = new RealTimeReservation(supermarket,new User(),20,Time.valueOf("13:00:00"),Time.valueOf("13:10:00"),5);
		RealTimeReservation rel2 = new RealTimeReservation(supermarket,new User(),40,Time.valueOf("13:00:00"),Time.valueOf("13:10:00"),6);
		RealTimeReservation rel3 = new RealTimeReservation(supermarket,new User(),20,Time.valueOf("13:00:00"),Time.valueOf("13:10:00"),7);
		RealTimeReservation rel4 = new RealTimeReservation(supermarket,new User(),20,Time.valueOf("13:00:00"),Time.valueOf("13:10:00"),8);
		RealTimeReservation rel5 = new RealTimeReservation(supermarket,new User(),40,null,Time.valueOf("13:30:00"),9);
		supermarket.addReservation(rel1);
		supermarket.addReservation(rel2);
		supermarket.addReservation(rel3);
		supermarket.addReservation(rel4);
		supermarket.addReservation(rel5);
		
		
		
	}

	@Test
	void test() throws BadReservationException {
		
		LocalTime ete= service.computeETE(new RealTimeReservation(supermarket,new User(),40,null,null,10));
		assertEquals(Time.valueOf("13:20:00").toLocalTime(),ete);

		
		
		
	}

}

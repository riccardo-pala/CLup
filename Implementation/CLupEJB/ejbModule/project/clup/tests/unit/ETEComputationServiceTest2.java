package project.clup.tests.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.PlannedReservation;
import project.clup.entities.RealTimeReservation;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadReservationException;
import project.clup.services.ETEComputationService;

class ETEComputationServiceTest2 {

	private Supermarket supermarket;
	private ETEComputationService service; 
	
	@BeforeEach
	public void setUp() {
		
		service=new ETEComputationService();
		supermarket=new Supermarket();
		supermarket.setMaxCapacity(4);
		
		RealTimeReservation rel6 = new RealTimeReservation(supermarket,new User(),60,Time.valueOf("19:40:00"),null,4);
		RealTimeReservation rel1 = new RealTimeReservation(supermarket,new User(),40,Time.valueOf("19:40:00"),null,5);
		RealTimeReservation rel2 = new RealTimeReservation(supermarket,new User(),40,null,null,6);
		RealTimeReservation rel3 = new RealTimeReservation(supermarket,new User(),20,null,null,7);
		RealTimeReservation rel4 = new RealTimeReservation(supermarket,new User(),20,null,null,8);
		RealTimeReservation rel5 = new RealTimeReservation(supermarket,new User(),40,null,null,9);

		PlannedReservation planned1 = new PlannedReservation(supermarket,new User(),40,null,null,Time.valueOf("20:20:00"));
		PlannedReservation planned2 = new PlannedReservation(supermarket,new User(),40,null,null,Time.valueOf("20:20:00"));
		PlannedReservation planned3 = new PlannedReservation(supermarket,new User(),40,null,null,Time.valueOf("20:20:00"));
		PlannedReservation planned4 = new PlannedReservation(supermarket,new User(),40,null,null,Time.valueOf("20:40:00"));
		
		supermarket.addReservation(rel1);
		supermarket.addReservation(rel2);
		supermarket.addReservation(rel3);
		supermarket.addReservation(rel4);
		supermarket.addReservation(rel5);
		supermarket.addReservation(rel6);
		supermarket.addReservation(planned1);
		supermarket.addReservation(planned2);
		supermarket.addReservation(planned3);
		supermarket.addReservation(planned4);
		
	}

	@Test
	void test() throws BadReservationException {
		
		LocalTime ete= service.computeETE(new RealTimeReservation(supermarket,new User(),40,null,null,10));
		assertEquals(Time.valueOf("21:20:00").toLocalTime(),ete);

		
		
		
	}

}

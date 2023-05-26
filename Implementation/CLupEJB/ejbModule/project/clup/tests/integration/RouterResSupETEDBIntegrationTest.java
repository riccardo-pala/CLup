package project.clup.tests.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.ReservationService;
import project.clup.services.Router;
import project.clup.services.SupermarketService;

class RouterResSupETEDBIntegrationTest {
	
	private EntityManager em;
	private EntityManagerFactory emf;

	@BeforeEach
	public void setUp() {
		emf = Persistence.createEntityManagerFactory("CLupEJB");
		em = emf.createEntityManager();
		createTestData();
	}



	@AfterEach
	public void tearDown() throws BadRetrievalException {
	
		if (em != null) {
			removeTestData();
			em.close();
		}
		if (emf != null) {
			emf.close();
		}
	}

	private void createTestData() {
	
		Supermarket supermarket = new Supermarket();
		supermarket.setId(1111);
		supermarket.setAddress("via Isonzo");
		supermarket.setMaxCapacity(40);
		supermarket.setName("Ekom");
		supermarket.setOpeningtime(Time.valueOf("08:00:00"));
		supermarket.setClosingtime(Time.valueOf("22:00:00"));
		
		User user = new User(333,"Ale98","aleale","Alessandro","Bianchi",null);
		
		
		em.getTransaction().begin();
		em.persist(user);
		em.persist(supermarket);
		em.getTransaction().commit();
		
		}

	private void removeTestData() throws BadRetrievalException {
		
		em= emf.createEntityManager();
		
		Supermarket supermarket = em.find(Supermarket.class,1111);
		User user = em.find(User.class,333);
		
		if (supermarket != null) {
			
			em.getTransaction().begin();
			em.remove(supermarket);
			em.remove(user);
			em.getTransaction().commit();
		}
		
		}

	@Test
	public void testForwardRealTimeReservationCreationRequest() throws Exception {
		
	em= emf.createEntityManager();
	
	Router router = new Router();
	ReservationService service = new ReservationService();
	service.setEm(em);
	router.setReservationService(service);
	assertEquals(true,router.forwardRealTimeReservationCreationRequest(20, 333, 1111));
	User user=em.find(User.class,333);
	assertNotNull(user.getReservation());
	
	
	
	
	}
	
	
	@Nested 
	public class PlannedReservationTest {
		
		private EntityManager em;
		private EntityManagerFactory emf;

		@BeforeEach
		public void setUp() {
			emf = Persistence.createEntityManagerFactory("CLupEJB");
			em = emf.createEntityManager();
			createTestData();
		}



		@AfterEach
		public void tearDown() throws BadRetrievalException {
		
			if (em != null) {
				removeTestData();
				em.close();
			}
			if (emf != null) {
				emf.close();
			}
		}

		private void createTestData() {
		
			Supermarket supermarket = new Supermarket();
			supermarket.setId(2222);
			supermarket.setAddress("via Roma");
			supermarket.setMaxCapacity(40);
			supermarket.setName("Ekom");
			supermarket.setOpeningtime(Time.valueOf("08:00:00"));
			supermarket.setClosingtime(Time.valueOf("22:00:00"));
			
			User user = new User(444,"Ricky98","aleale","Alessandro","Bianchi",null);
			
			
			em.getTransaction().begin();
			em.persist(user);
			em.persist(supermarket);
			em.getTransaction().commit();
			
			}

		private void removeTestData() throws BadRetrievalException {
			
			em= emf.createEntityManager();
			
			Supermarket supermarket = em.find(Supermarket.class,2222);
			User user = em.find(User.class,444);
			
			if (supermarket != null) {
				
				em.getTransaction().begin();
				em.remove(supermarket);
				em.remove(user);
				em.getTransaction().commit();
			}
			
			}
		

		@Test
		public void testForwardPlannedReservationCreationRequest() throws Exception {
			
			em= emf.createEntityManager();
	
			Router router = new Router();
			ReservationService reservationservice = new ReservationService();
			SupermarketService supermarketservice = new SupermarketService();
			supermarketservice.setEm(em);
			reservationservice.setEm(em);
			router.setSupermarketService(supermarketservice);
			router.setReservationService(reservationservice);
			assertEquals(true,router.forwardPlannedReservationCreationRequest(2222,Time.valueOf("13:00:00"),20, 444));
			User user=em.find(User.class,444);
			assertNotNull(user.getReservation());
		}
	}
	
	
	
	
	}
	
	
	
	



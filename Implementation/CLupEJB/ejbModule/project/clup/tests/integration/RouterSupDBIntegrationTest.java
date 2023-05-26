package project.clup.tests.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import project.clup.entities.Supermarket;
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.Router;
import project.clup.services.SupermarketService;


class RouterSupDBIntegrationTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private int size;
	
	
	

	@BeforeEach
	public void setUp() throws BadRetrievalException {
		emf = Persistence.createEntityManagerFactory("CLupEJB");
		em = emf.createEntityManager();
		createTestData();
	}
	

	public void createTestData() throws BadRetrievalException {
		
		emf = Persistence.createEntityManagerFactory("CLupEJB");
		em = emf.createEntityManager();
		
		Router router=new Router();
		SupermarketService service = new SupermarketService();
		service.setEm(em);
		router.setSupermarketService(service);
		List<Supermarket> list = router.forwardSupermarketListRequest();
		size=list.size();
		
		Supermarket supermarket = new Supermarket();
		supermarket.setAddress("Corso Francia");
		supermarket.setId(112);
		supermarket.setMaxCapacity(40);
		supermarket.setName("Ekom");
		supermarket.setOpeningtime(Time.valueOf("08:00:00"));
		supermarket.setClosingtime(Time.valueOf("08:00:00"));
		
		em.getTransaction().begin();
		em.persist(supermarket);
		em.getTransaction().commit();
		
		
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
	
	private void removeTestData() throws BadRetrievalException {
		
		em=emf.createEntityManager();
		
		SupermarketService service= new SupermarketService();
		service.setEm(em);
		
		Supermarket supermarket = service.findSupermarketByAddress("Corso Francia");
		
		
		if (supermarket != null) {
			System.out.println(supermarket.getName()+" "+supermarket.getAddress());
		em.getTransaction().begin();	
		em.remove(supermarket);
		em.getTransaction().commit();
		}
			
		}

	
	
	@Test
	public void testFowardSupermarketListRequest() throws BadRetrievalException {
		
		Router router=new Router();
		SupermarketService service = new SupermarketService();
		service.setEm(em);
		router.setSupermarketService(service);
		List<Supermarket> list = router.forwardSupermarketListRequest();
		size++;
		assertEquals(size,list.size());
	}
	
	
	
	
	
	
	@Nested
	public class AvailableSchedule{
		
		private EntityManager em;
		private EntityManagerFactory emf;
		
		@BeforeEach
		public void setUp() throws BadRetrievalException {
			emf = Persistence.createEntityManagerFactory("CLupEJB");
			em = emf.createEntityManager();
			createTestData();
			
		}
		
		public void createTestData() throws BadRetrievalException {
			
			emf = Persistence.createEntityManagerFactory("CLupEJB");
			em = emf.createEntityManager();
			
			Router router=new Router();
			SupermarketService service = new SupermarketService();
			service.setEm(em);
			router.setSupermarketService(service);
			List<Supermarket> list = router.forwardSupermarketListRequest();
			size=list.size();
			
			Supermarket supermarket = new Supermarket();
			supermarket.setAddress("Corso Europa");
			supermarket.setId(117);
			supermarket.setMaxCapacity(40);
			supermarket.setName("Ekom");
			supermarket.setOpeningtime(Time.valueOf("08:00:00"));
			supermarket.setClosingtime(Time.valueOf("23:00:00"));
			
			
			
			em.getTransaction().begin();
			em.persist(supermarket);
			em.getTransaction().commit();
		
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
		
		private void removeTestData() throws BadRetrievalException {
			
			em=emf.createEntityManager();
			
			SupermarketService service= new SupermarketService();
			service.setEm(em);
			
			Supermarket supermarket = service.findSupermarketByAddress("Corso Europa");
			
			
			if (supermarket != null) {
				System.out.println(supermarket.getName()+" "+supermarket.getAddress());
			em.getTransaction().begin();	
			em.remove(supermarket);
			em.getTransaction().commit();
			}
				
			}
		
		@Test
		void testForwardAvailableScheduleRequest() throws BadReservationException {
			
			Router router = new Router();
			SupermarketService service = new SupermarketService();
			service.setEm(em);
			router.setSupermarketService(service);
			
			List<Time> list=null;
			try {
				list = router.forwardAvailableScheduleRequest(117);
			} catch (BadRetrievalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*tested at 18:54, if you need to test in another time you just need
			 * to change 6 as parameters of assertEquals method with the number of expected
			 * time slots from the hour the test is taken plus two hour (each time slot has a duration of 20 min) */
			
			assertEquals(6,list.size());
			
			
		}
		
		
		
		
		
	}
	
	
}

package project.clup.tests.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.Supermarket;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.SupermarketService;

class SupDBIntegrationTest {

	static final String SUPERMARKET_NAME = "Carefour Express";
	static final String ADDRESS = "Piazza Dante";
	static final int MAXCAPACITY =30;
	int lastId;
	
	private EntityManagerFactory emf;
	private EntityManager em;



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
		supermarket.setAddress(ADDRESS);
		supermarket.setName(SUPERMARKET_NAME);
		
		
		List<Supermarket> lastSupermarket = null;
		lastSupermarket = em.createQuery("SELECT s FROM Supermarket s WHERE s.id = (SELECT MAX(r.id) FROM Supermarket r)", Supermarket.class)
				.getResultList();
		
		lastId=0;
		
		if(lastSupermarket.isEmpty()) lastId = 1;
		else if(lastSupermarket.size() == 1)
		lastId = lastSupermarket.get(0).getId();
		
		lastId ++;
		supermarket.setId(lastId);
	
		em.getTransaction().begin();
		em.persist(supermarket);
		em.getTransaction().commit();
		}

	
	private void removeTestData() throws BadRetrievalException {
		
		em=emf.createEntityManager();
		
		SupermarketService service= new SupermarketService();
		service.setEm(em);
		
		Supermarket supermarket = service.findSupermarketByAddress(ADDRESS);
		
		
		if (supermarket != null) {
			System.out.println(supermarket.getName()+" "+supermarket.getAddress());
		em.getTransaction().begin();	
		em.remove(supermarket);
		em.getTransaction().commit();
		}
			
		}

	@Test
	public void testFindAll() throws Exception {
	
	SupermarketService service = new SupermarketService();
	service.setEm(em);
	
	List<Supermarket> supermarkets = service.findAllSupermarket();
	
	assertNotNull(supermarkets);
	
	/*Check the number of rows in the supermarket table*/
	assertEquals(lastId,supermarkets.size());
	
	
	}
	
}

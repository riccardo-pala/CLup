package project.clup.tests.integration;

import static org.junit.jupiter.api.Assertions.*;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.RealTimeReservation;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.SupermarketService;
import project.clup.services.UserService;

class ResDBIntegrationTest {

	
	
	static final String USER_NAME="Giancarlo87";
	static final String SUPERMARKET_ADDRESS="Corso Napoleone";
	
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
		supermarket.setAddress(SUPERMARKET_ADDRESS);
		supermarket.setName("Lidl");
		List<Supermarket> lastSupermarket = null;
		lastSupermarket = em.createQuery("SELECT s FROM Supermarket s WHERE s.id = (SELECT MAX(r.id) FROM Supermarket r)", Supermarket.class)
				.getResultList();
		
		int lastId=0;
		
		if(lastSupermarket.isEmpty()) lastId = 1;
		else if(lastSupermarket.size() == 1)
		lastId = lastSupermarket.get(0).getId();
		lastId ++;
		supermarket.setId(lastId);
		User user = new User(110,"Giancarlo87","ciaocomestai","Giancarlo","Giancarlo",supermarket);
		RealTimeReservation real = new RealTimeReservation(supermarket,user,20,null,null,10);
		user.setReservation(real);
				
		em.getTransaction().begin();
		em.persist(supermarket);
		em.persist(user);
		em.getTransaction().commit();
		}

	
	private void removeTestData() throws BadRetrievalException {
		
		em=emf.createEntityManager();
		
		SupermarketService supermarketService = new SupermarketService();
		UserService userService = new UserService();
		userService.setEm(em);
		supermarketService.setEm(em);
		Supermarket supermarket = supermarketService.findSupermarketByAddress(SUPERMARKET_ADDRESS);
		User user =userService.findByUserName(USER_NAME);
		
			em.getTransaction().begin();
			em.remove(supermarket);
			em.remove(user);
			em.getTransaction().commit();
		
		
		}

	@Test
	public void testFindAll() throws Exception {
	
	
	User user=em.find(User.class,110);
	
	assertNotNull(user.getReservation());
	assertNotNull(user.getFavouriteSupermarket());
	
	
	}
}

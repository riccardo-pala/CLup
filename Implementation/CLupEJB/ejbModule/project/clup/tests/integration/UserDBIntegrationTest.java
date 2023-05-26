package project.clup.tests.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.clup.entities.User;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.UserService;

class UserDBIntegrationTest {

	static final String USER_ID = "Giacomo98";
	static final String PASSWORD = "Riccardo";
	static final String INVALID_USER_ID = "Alessandro";
	
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
	
		User user = new User();
		user.setUsername(USER_ID);
		user.setPassword(PASSWORD);
		user.setFirstname("Giacomo");
		user.setLastname("Polvanesi");
		
		List<User> lastUser = null;
		lastUser = em.createQuery("SELECT u FROM User u WHERE u.id = (SELECT MAX(r.id) FROM User r)", User.class)
				.getResultList();
		
		int lastId=0;
		
		if(lastUser.isEmpty()) lastId = 1;
		else if(lastUser.size() == 1)
		lastId = lastUser.get(0).getId();
		
		lastId ++;
		user.setId(lastId);
	
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
		}

	private void removeTestData() throws BadRetrievalException {
	
		
		UserService service= new UserService();
		service.setEm(em);
		
		em.getTransaction().begin();
		User user = service.findByUserName(USER_ID);
		
		if (user != null) {
			em.remove(user);
		}
		em.getTransaction().commit();
		}

	@Test
	public void testAuthenticateValidUser() throws Exception {
	
	UserService service = new UserService();
	service.setEm(em);
	User user = service.checkCredentials(USER_ID, PASSWORD);
	assertNotNull(user);
	assertEquals(USER_ID, user.getUsername());
	assertEquals(PASSWORD, user.getPassword());
	
	}
	
	@Test
	public void testAuthenticateInvalidUser() throws Exception {
		
	UserService service = new UserService();
	service.setEm(em);
	User user = service.checkCredentials(INVALID_USER_ID, PASSWORD);
	assertNull(user);
	
	}
}


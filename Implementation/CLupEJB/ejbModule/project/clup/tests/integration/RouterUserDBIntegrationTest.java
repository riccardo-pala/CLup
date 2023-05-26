package project.clup.tests.integration;

import static org.junit.Assert.assertNotNull;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import project.clup.entities.User;
import project.clup.exceptions.CreateProfileException;
import project.clup.exceptions.CredentialsException;
import project.clup.services.Router;
import project.clup.services.UserService;

class RouterUserDBIntegrationTest {
	
	
	private EntityManager em;
	private EntityManagerFactory emf;
	Router router;
	

	
	
	@Test
	public void testSignUpRequest() throws CredentialsException, CreateProfileException {
		emf = Persistence.createEntityManagerFactory("CLupEJB");
		em = emf.createEntityManager();	
		Router router=new Router();
		UserService service= new UserService();
		router.setUserService(service);
		router.getUserService().setEm(em);
		User user = router.forwardSignUpRequest("Carlo86","Verdi","Carlo","sonocarlo");
		assertNotNull(user);	
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
	}
	
	@Test
	public void testSignInRequest() throws NonUniqueResultException, CredentialsException {
		emf = Persistence.createEntityManagerFactory("CLupEJB");
		em = emf.createEntityManager();	
		Router router=new Router();
		UserService service= new UserService();
		router.setUserService(service);
		router.getUserService().setEm(em);
		User user = router.forwardSignInRequest("Carlo","sonocarlo");
		assertNotNull(user);	
	}
	
	

	
}

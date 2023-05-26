package project.clup.tests.unit;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.UpdateProfileException;
import project.clup.services.UserService;

class UserServiceTest {
	
	private String username="Giorgio";
	private String password="Whong";
	private int id;
	
  
    @Test
    public void testAuthenticateValidUser() throws Exception {
    	
    UserService service = new TestUserService();
    User user2 = service.checkCredentials(username, password);
    assertNotNull(user2);
    assertEquals(username, user2.getUsername());
    assertEquals(password, user2.getPassword());
    
    }
    
    @Test
    public void testFavouriteSupermarket() throws BadRetrievalException, UpdateProfileException {
    	
    	  UserService service = new TestUserService();
    	  
    	  Supermarket supermarket=new Supermarket();
    	  supermarket.setId(1);
    	  
    	  User user = service.findByUserName(username);
    	  
    	  user.setFavouriteSupermarket(supermarket);
    	  
    	  assertNotNull(user.getFavouriteSupermarket());
    	  
    	  service.deleteFavouriteSupermarket(user.getUsername());
    	  
    	  user=service.findByUserName(username);
    	  
    	  assertNull(user.getFavouriteSupermarket());
    	
    }
    
    
	
	
	class TestUserService extends UserService {
		private User user;
		
		
		public TestUserService() {
		user = new User();
		Supermarket supermarket =new Supermarket();
		supermarket.setId(1);
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		user.setFavouriteSupermarket(supermarket);
		
		}
		
		public User checkCredentials(String usern,String pwd) {
			if(usern.equals(username) && pwd.equals(password))
			return user;
			
			else 
			return null;
		
		}
		public User findByUserName(String username) {
		if (username.equals(user.getUsername())) {
		return user;
		}
		return null;
		}
		
	
		public User deleteFavouriteSupermarket(String username) {
			if(user.getUsername().equals(username)) {
				user.setFavouriteSupermarket(null);
				return user;
			}
			return null;	
			
		}
		
		public void setFavouriteSupermarket(int supermarketId, int userId) {
			
			
			
		}
	}
}

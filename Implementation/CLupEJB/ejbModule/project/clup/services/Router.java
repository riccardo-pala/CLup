package project.clup.services;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NonUniqueResultException;

import project.clup.entities.RealTimeReservation;
import project.clup.entities.Reservation;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.CreateProfileException;
import project.clup.exceptions.CredentialsException;
import project.clup.exceptions.UpdateProfileException;
import project.clup.exceptions.UpdateReservationException;
import project.clup.exceptions.UpdateSupermarketException;

@Stateless
public class Router {

	@EJB(name = "project.clup.services/UserService")
	private UserService userService;
	
	@EJB(name = "project.clup.services/SupermarketService")
	private SupermarketService supermarketService;
	
	@EJB(name = "project.clup.services/ETEComputationService")
	private ETEComputationService eteComputationService;
	
	@EJB(name = "project.clup.services/ReservationService")
	private ReservationService reservationService;
	
	
	
	public UserService getUserService() {
		return userService;
	}


	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public SupermarketService getSupermarketService() {
		return supermarketService;
	}


	public void setSupermarketService(SupermarketService supermarketService) {
		this.supermarketService = supermarketService;
	}


	public ETEComputationService getEteComputationService() {
		return eteComputationService;
	}


	public void setEteComputationService(ETEComputationService eteComputationService) {
		this.eteComputationService = eteComputationService;
	}


	public ReservationService getReservationService() {
		return reservationService;
	}


	public void setReservationService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}


	public Router() {
	}
	
	
	public User forwardSignInRequest(String username, String password) throws NonUniqueResultException, CredentialsException {
		return userService.checkCredentials(username, password);
	}
	
	public User forwardSignUpRequest(String username, String firstname, String lastname, String password) throws CredentialsException, CreateProfileException {
		return userService.insertNewUser(username, firstname, lastname, password);
	}
	
	public User forwardUserDetailsRequest(String username) throws BadRetrievalException {
		return userService.findByUserName(username);
	}
	
	public User forwardProfileUpdateRequest(String username, String newusername, String firstname, String lastname, String password, Integer favSupermarketId) throws UpdateProfileException, BadRetrievalException {
		Supermarket favSupermarket = null;
		if(favSupermarketId != null)
			if(favSupermarketId == -1) {
				return userService.deleteFavouriteSupermarket(username);
			}
			else favSupermarket = supermarketService.findsupermarketbyId(favSupermarketId);
		return userService.updateUser(username, newusername, firstname, lastname, password, favSupermarket);
	}
	
	public List<Supermarket> forwardSupermarketListRequest() throws BadRetrievalException {
		return supermarketService.findAllSupermarket();
	}
	
	public LocalTime forwardETEComputationRequest(int supermarketId, int visitDuration) throws BadRetrievalException, BadReservationException {
		Supermarket supermarket = supermarketService.findsupermarketbyId(supermarketId);
		return eteComputationService.computeETE(new RealTimeReservation(supermarket, null, visitDuration, null, null, -1));
	}
	
	public LocalTime forwardETEComputationRequest(RealTimeReservation r) throws BadReservationException {
		return eteComputationService.computeETE(r);
	}
	
	public List<Time> forwardAvailableScheduleRequest(int supermarketId) throws BadRetrievalException {
		return supermarketService.getSchedule(supermarketId);
	}
	
	public boolean forwardRealTimeReservationCreationRequest(int visitDuration, int userId, int supermarketId) throws BadRetrievalException, UpdateProfileException, UpdateSupermarketException, BadReservationException {
		return reservationService.insertRealTimeReservation(visitDuration, userId, supermarketId);
	}
	
	public boolean forwardPlannedReservationCreationRequest(int supermarketId, Time startTime, int visitDuration, int userId) {
		try {
			if(supermarketService.checkAvailability(supermarketId, startTime, visitDuration)) {
				reservationService.insertPlannedReservation(visitDuration, startTime, userId, supermarketId);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void forwardReservationPersistRequest(Reservation r) throws UpdateReservationException {
		reservationService.persistReservation(r);
	}
	
	public void forwardReservationDeletionRequest(int reservationId, int userId) throws BadRetrievalException {
		reservationService.deleteReservation(reservationId, userId);
	}
	
	public Reservation forwardReservationDetailsRequest(int userId) throws BadRetrievalException {
		return reservationService.findReservationByUserId(userId);
	}
}

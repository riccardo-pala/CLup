package project.clup.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import project.clup.entities.PlannedReservation;
import project.clup.entities.RealTimeReservation;
import project.clup.entities.Reservation;
import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.UpdateProfileException;
import project.clup.exceptions.UpdateReservationException;
import project.clup.exceptions.UpdateSupermarketException;

@Stateless
public class ReservationService {
	
	@PersistenceContext(unitName = "CLupEJB")
	private EntityManager em;

	public ReservationService() {
	}
	
	
	
	public EntityManager getEm() {
		return em;
	}



	public void setEm(EntityManager em) {
		this.em = em;
	}



	public Reservation findReservationByUserId(int userId) throws BadRetrievalException {
		//em.flush();
		User user;
		try {
			user = em.find(User.class, userId);
			em.refresh(user);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not retrieve user information");
		}
		Reservation reservation = user.getReservation();
		return reservation;	
	}
	
	public void deleteReservation(int reservationId, int ownerId) throws BadRetrievalException {
		Reservation reservation;
		try {
			reservation = em.find(Reservation.class, reservationId);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not retrieve reservation information");
		}
		User owner;
		try {
			owner = em.find(User.class, ownerId);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not retrieve user information");
		}
		owner.setReservation(null);
		em.remove(reservation);
	}
	
	public Reservation findReservationById(int reservationId) throws BadRetrievalException {

		Reservation reservation;
		try {
			reservation = em.find(Reservation.class, reservationId);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not retrieve reservation information");
		}
		return reservation;	 
	}
	
	public void insertPlannedReservation(int visitDuration, Time startTime, int ownerId, int supermarketId) throws BadRetrievalException, UpdateProfileException {
		
		User owner = null;
		
		try {
			owner = em.find(User.class, ownerId);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not retrieve user information");
		}
		owner.setReservation(null);

		Supermarket supermarket = null;
		
		try {
			supermarket = em.find(Supermarket.class, supermarketId);
		} catch(PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not fetch supermarket from DB");
		}
		
		// PlannedReservation(Supermarket supermarket, User user, int visitDuration, Time entranceTime, Time maxToleratedDelay, Time startTime)
		PlannedReservation reservation = 
				new PlannedReservation(supermarket, owner, visitDuration, null, Time.valueOf(startTime.toLocalTime().plusMinutes(10)), startTime);
		
		owner.setReservation(reservation);
		
		try {
			em.persist(owner);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new UpdateProfileException("Could not update profile");
		}		
	}
	
	public boolean insertRealTimeReservation(int visitDuration, int ownerId, int supermarketId) throws BadRetrievalException, UpdateProfileException, UpdateSupermarketException, BadReservationException {
		
		User owner = null;
		
		try {
			owner = em.find(User.class, ownerId);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not retrieve user information");
		}
		owner.setReservation(null);
		
		Supermarket supermarket = null;
		
		try {
			supermarket = em.find(Supermarket.class, supermarketId);
		} catch(PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not fetch supermarket from DB");
		}
		
		int ticketNumber = supermarket.getLastTicketNumber()+1;
		// RealTimeReservation(Supermarket supermarket, User user, int visitDuration, Time entranceTime, Time maxToleratedDelay, int ticketNumber)
		RealTimeReservation reservation = new RealTimeReservation(supermarket,owner,visitDuration,null,null,ticketNumber);
		
		ETEComputationService eteComputationService = new ETEComputationService();
		LocalTime ETE = eteComputationService.computeETE(reservation);
		
		if (ETE.plusMinutes(visitDuration).isAfter(supermarket.getClosingtime().toLocalTime())) {
			return false;
		}
		
		supermarket.setLastTicketNumber(ticketNumber);
		try {
			em.persist(supermarket);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new UpdateSupermarketException("Could not update supermarket information");
		}

		owner.setReservation(reservation);
		try {
			em.persist(owner);
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new UpdateProfileException("Could not update profile");
		}
		
		return true;
	}
	
	
	public boolean expiredReservation(int reservationId, char type) throws BadRetrievalException {
		
		if(type == 'p') {
			
			PlannedReservation reservation = null;
			try {
				reservation = (PlannedReservation) /*em.find(PlannedReservation.class, reservationId)*/ this.findReservationById(reservationId);
			} catch (PersistenceException e) {
				//e.printStackTrace();
				throw new BadRetrievalException("Could not retrieve reservation information");
			}
			
			if(	reservation.getEntranceTime()==null && !LocalTime.now().isBefore(reservation.getMaxToleratedDelay().toLocalTime())
					||
				reservation.getEntranceTime()!=null && !LocalTime.now().isBefore(reservation.getStartTime().toLocalTime().plusMinutes(reservation.getVisitDuration()))
			) return true;
			
			else return false;
		}
		else {
			
			RealTimeReservation reservation = null;
			try {
				reservation = (RealTimeReservation) /*em.find(RealTimeReservation.class, reservationId)*/ this.findReservationById(reservationId);
			} catch (PersistenceException e) {
				//e.printStackTrace();
				throw new BadRetrievalException("Could not retrieve reservation information");
			}
			
			if( reservation.getEntranceTime()==null && !LocalTime.now().isBefore(reservation.getMaxToleratedDelay().toLocalTime())
					||
				reservation.getEntranceTime()!=null && !LocalTime.now().isBefore(reservation.getEntranceTime().toLocalTime().plusMinutes(reservation.getVisitDuration())))
				return true;
			else return false;
		}
	}
	
	public void persistReservation(Reservation r) throws UpdateReservationException {
	
		try {
			em.merge(r);
		} catch(PersistenceException e) {
			//e.printStackTrace();
			throw new UpdateReservationException("Could not update reservation information");
		}
		
	}
	
	public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		byte[] pngData = pngOutputStream.toByteArray(); 
		return pngData;
	}
}

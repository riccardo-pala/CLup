package project.clup.services;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import project.clup.entities.PlannedReservation;
import project.clup.entities.Reservation;
import project.clup.entities.Supermarket;
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.TimetableException;
import project.clup.utilities.Timetable;

@Stateless
public class SupermarketService {
	
	@PersistenceContext(unitName = "CLupEJB")
	private EntityManager em;

	public SupermarketService() {
	}
	
	
	
	public EntityManager getEm() {
		return em;
	}



	public void setEm(EntityManager em) {
		this.em = em;
	}

	public Supermarket findSupermarketByAddress(String address) {
		List<Supermarket> list = null;
		
		list = em.createQuery("SELECT s FROM Supermarket s WHERE s.address=?1 ").setParameter(1,address).getResultList();
		if(list!=null) {
			if(!list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
		
	}

	public Supermarket findsupermarketbyId(int supermarketId) throws BadRetrievalException {
		
		Supermarket supermarket = null;
		
		try {
			supermarket = em.find(Supermarket.class, supermarketId);
		} catch(PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not fetch supermarket from DB");
		}
		
		return supermarket;
	}
	
	public List<Supermarket> findAllSupermarket() throws BadRetrievalException{
		List<Supermarket> supermarkets = null;
		try {
			supermarkets = em.createQuery("SELECT s FROM Supermarket s").getResultList();
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not fetch supermarkets list from DB");
		}
		return supermarkets;
	}
	
	public List<Reservation> findDoingGroceryShopping(int supermarketId) throws BadRetrievalException{
		List<Reservation> doingGroceryShopping = null;
		try {
			doingGroceryShopping = em.createNamedQuery("Reservation.retrieveDoingGroceryShopping", Reservation.class)
					.setParameter("sId", supermarketId).getResultList();
		} catch (PersistenceException e) {
			//e.printStackTrace();
			throw new BadRetrievalException("Could not fetch Doing Grocery Shopping List from DB");
		}
		return doingGroceryShopping;
	}
	
	public List<Time> getSchedule(int supermarketId) throws BadRetrievalException{
		
		Supermarket supermarket = findsupermarketbyId(supermarketId);
		
		// Time now = Time.valueOf(LocalTime.now());
		LocalTime now = LocalTime.now();
		LocalTime openingTime = supermarket.getOpeningtime().toLocalTime();
		LocalTime closingTime = supermarket.getClosingtime().toLocalTime();
		
		LocalTime startSlot;
		int startSlotHour;
		int startSlotMin;
		
		List<Time> lt = new ArrayList<Time>();
	
		// now is equal or after the closure time
		
		if(!now.isBefore(closingTime.minusHours(2))) 
			return lt;
		// surely now will be before the closure time
		if(now.isAfter(openingTime.minusHours(2)) && now.isBefore(closingTime.minusHours(2))) {
			if(now.getMinute() == 0) { startSlotMin = 0; startSlotHour = now.plusHours(2).getHour(); }
			else if(now.getMinute() <= 20) { startSlotMin = 20; startSlotHour = now.plusHours(2).getHour(); }
			else if (now.getMinute() <= 40) { startSlotMin = 40; startSlotHour = now.plusHours(2).getHour(); }
			else { startSlotMin = 0; startSlotHour = now.plusHours(3).getHour(); }
			startSlot = LocalTime.of(startSlotHour, startSlotMin);
		}
		else { 
			if(openingTime.getMinute()%20 == 0) startSlot = openingTime;
			else startSlot = openingTime.plusMinutes(20 - openingTime.getMinute()%20); // opentime is not aligned with the start slot
		}
		
		// adding the slots up to closure time
		Time e;
		while(startSlot.isBefore(closingTime) && !startSlot.equals(LocalTime.MIDNIGHT)) {
			e = Time.valueOf(startSlot);
			lt.add(e);
			startSlot = startSlot.plusMinutes(20);
		}
		
		// remove last slot before the closure time
		if(!lt.isEmpty() && (closingTime.getMinute()%20 != 0))
			lt.remove(lt.size()-1);
		
		//System.out.println(lt);
		return lt;
	}
	
	/**
	 * 
	 * @param superAddr or superId [(1) or (2)]
	 * @param start time of reservation request
	 * @param duration time of the visit
	 * @return true if making the reservation is possible
	 * @throws BadRetrievalException, TimetableException
	 * @throws BadReservationException 
	 */
	public boolean checkAvailability(int supermarketId, Time startTime, int visitDuration) throws BadRetrievalException, TimetableException, BadReservationException {
		
		Supermarket supermarket = findsupermarketbyId(supermarketId);
		
		int maxCapacity = supermarket.getMaxCapacity();
		
		Timetable timetable = supermarket.getTimetable();
		ArrayList<PlannedReservation> slotList;
		
		Integer startSlot = timetable.getSlotFromTime(startTime.toLocalTime());
		
		int counter = startSlot;
		
		while (counter < startSlot + visitDuration) {
			
			slotList = timetable.getSchedule().get(counter);
			
			if (slotList != null) {
				if(slotList.size() == maxCapacity)
					return false;
				if(slotList.size() > maxCapacity)
					throw new TimetableException("Could not check availability in timetable");
			}
			counter+=20;
		}
		
		return true;
	}

	
}
	

	
	



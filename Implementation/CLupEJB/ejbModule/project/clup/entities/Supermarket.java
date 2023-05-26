package project.clup.entities;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import project.clup.exceptions.BadReservationException;
import project.clup.utilities.Timetable;


/**
 * 
 * Persistent class for supermarkets table
 * 
 * @author PalaImmordinoPolvanesi
 *
 */
@Entity
@Table(name = "supermarkets")
@NamedQuery(name = "Supermarket.findByAddress", 
			query = "SELECT s "
					+ "FROM Supermarket s "
					+ "WHERE s.address = :address")
public class Supermarket {
	
	@Id 
	private int id;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "lastticketnumber")
	private int lastTicketNumber;
	
	@Column(name = "maxcapacity")
	private int maxCapacity;
	
	// bi-directional relationship Reservation<->Supermarket (owner is Reservation entity)
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "supermarket", cascade = CascadeType.ALL, orphanRemoval = true)
	// @OrderBy("starttime ASC")
	private List<Reservation> reservations;
	
	@Transient
	private Timetable timetable;
	
	@Column(name = "openingtime")
	private Time openingTime;
	
	@Column(name = "closingtime")
	private Time closingTime;
	

	public Supermarket() {}
	
	public Supermarket(String address, String name, int maxCapacity) {
		this.address = address;
		this.name = name;
		this.lastTicketNumber = 0;
		this.maxCapacity = maxCapacity;
		this.timetable = null;
	
		
	}
	
	public Time getOpeningtime() {
		return openingTime;
	}

	public void setOpeningtime(Time opentime) {
		this.openingTime = opentime;
	}

	public Time getClosingtime() {
		return closingTime;
	}

	public void setClosingtime(Time closuretime) {
		this.closingTime = closuretime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getLastTicketNumber() {
		return lastTicketNumber;
	}

	public void setLastTicketNumber(int lastTicketNumber) {
		this.lastTicketNumber = lastTicketNumber;
	}
	
	public List<Reservation> getReservations() {
		return reservations;
	}

	public void addReservation(Reservation reservation) {
		
		if(getReservations() == null) reservations = new ArrayList<Reservation>();
		getReservations().add(reservation);
		reservation.setSupermarket(this); // alignment
	}
	
	public void removeReservation(Reservation reservation) {
		getReservations().remove(reservation);
	}
	
	public Timetable getTimetable() throws BadReservationException {
		
		ArrayList<PlannedReservation> prList = new ArrayList<PlannedReservation>();
		
		for (Reservation r : reservations)
			if(r.getClass().equals(PlannedReservation.class))
				prList.add((PlannedReservation) r);
		
		timetable = new Timetable(prList);
		
		return timetable;
	}
	
	public ArrayList<RealTimeReservation> getVirtualLineUp() {
		
		ArrayList<RealTimeReservation> virtualLineUp = new ArrayList<RealTimeReservation>();
		
		for (Reservation r : reservations)
			if(r.getClass().equals(RealTimeReservation.class) && r.getEntranceTime() == null)
				virtualLineUp.add((RealTimeReservation) r);
		
		return virtualLineUp;
	}
	
	public ArrayList<Reservation> getDoingGroceryShopping() {
		
		ArrayList<Reservation> doingGroceryShopping = new ArrayList<Reservation>();
		
		for (Reservation r : reservations)
			if(r.getEntranceTime() != null)
				doingGroceryShopping.add(r);
		
		return doingGroceryShopping;
	}
}
package project.clup.entities;


import java.sql.Time;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import project.clup.exceptions.BadReservationException;
import project.clup.services.ETEComputationService;



@Entity
@DiscriminatorValue("r")
@NamedQuery(name = "RealTimeReservation.findVirtualLineUp", 
			query = "SELECT r "
					+ "FROM RealTimeReservation r JOIN r.supermarket s "
					+ "WHERE s.id = :sId AND r.entranceTime IS NULL "
					+ "ORDER BY r.ticketNumber ASC")
public class RealTimeReservation extends Reservation {
	
	@Column(name="ticketnumber")
	private int ticketNumber;
	
	@Transient
	private LocalTime ETE;
	
	/** 
	 * 
	 * Constructors, getters and setters
	 * 
	 */
	
	public RealTimeReservation() {
		super();
	}

	public RealTimeReservation(Supermarket supermarket, User user, int visitDuration, Time entranceTime, Time maxToleratedDelay, int ticketNumber) {
		super(supermarket, user, visitDuration, entranceTime, maxToleratedDelay);
		this.ticketNumber = ticketNumber;
	}
	
	public int getTicketNumber() {
		return ticketNumber;
	}
	
	public void setTicketNumber(int ticketNumber) {
		this.ticketNumber = ticketNumber;
	}
	
	public void setETE(LocalTime ETE) {
		this.ETE = ETE;
	}
	
	public LocalTime getExitTime() throws BadReservationException {
		
		if (getEntranceTime() != null) {
			return entranceTime.toLocalTime().plusMinutes(visitDuration);
		}
		
		if (ETE == null) {
			ETE = new ETEComputationService().computeETE(this);
		}
		
		return ETE.plusMinutes(visitDuration);
	}
	
	@Override
	public String toString() {
		return "(RT, id: " + id + ", tn: " + ticketNumber + ", vd: " + visitDuration + ")";
	}

}
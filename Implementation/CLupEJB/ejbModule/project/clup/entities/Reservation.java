package project.clup.entities;

import java.sql.Time;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import project.clup.exceptions.BadReservationException;


/**
 * 
 * Persistent class for reservations table
 * 
 * @author PalaImmordinoPolvanesi
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "restype")
@Table(name="reservations")

@NamedQueries({
	@NamedQuery(name = "Reservation.retrieveDoingGroceryShopping", 
			query = "SELECT r "
					+ "FROM Reservation r JOIN r.supermarket s "
					+ "WHERE s.id = :sId AND r.entranceTime IS NOT NULL "
					+ "ORDER BY r.entranceTime ASC"),
	@NamedQuery(name = "Reservation.retrieveVirtualLineUp", 
	query = "SELECT r "
			+ "FROM RealTimeReservation r JOIN r.supermarket s "
			+ "WHERE s.id = :sId AND r.entranceTime IS NULL "
			+ "ORDER BY r.ticketNumber ASC")
})
public abstract class Reservation implements Comparable<Reservation> {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;
	
	@ManyToOne
	@JoinColumn(name = "supermarket_fk")
	protected Supermarket supermarket;
	
	@OneToOne
	@JoinColumn(name = "user_fk")
	protected User user;
	
	@Column(name="visitduration")
	protected int visitDuration;
	
	@Column(name="entrancetime")
	protected Time entranceTime;
	
	@Column(name="maxtolerateddelay")
	private Time maxToleratedDelay;
	
	/** 
	 * 
	 * Constructors, getters and setters
	 * 
	 */
	
	protected Reservation() {}
	
	public Reservation(Supermarket supermarket, User user, int visitDuration, Time entranceTime, Time maxToleratedDelay) {
		this.supermarket = supermarket;
		this.user = user;
		this.visitDuration = visitDuration;
		this.entranceTime = entranceTime;
		this.maxToleratedDelay = maxToleratedDelay;
	}
	
	
	public int getCode() {
		return id;
	}
	
	public void setCode(int id) {
		this.id = id;
	}
	
	
	public Supermarket getSupermarket() {
		return supermarket;
	}
	
	public void setSupermarket(Supermarket supermarket) {
		this.supermarket = supermarket;
	}
	
	
	public int getVisitDuration() {
		return visitDuration;
	}
	
	public void setVisitDuration(int visitDuration) {
		this.visitDuration = visitDuration;
	}
	
	public Time getEntranceTime() {
		return entranceTime;
	}
	
	public void setEntranceTime(Time entranceTime) {
		this.entranceTime = entranceTime;
	}
	
	public Time getMaxToleratedDelay() {
		return maxToleratedDelay;
	}

	public void setMaxToleratedDelay(Time maxToleratedDelay) {
		this.maxToleratedDelay = maxToleratedDelay;
	}
	
	public abstract LocalTime getExitTime() throws BadReservationException;
	
	@Override
	public int compareTo(Reservation r) {
		
		try {
			if (getExitTime() == null || r.getExitTime() == null) {
				return 0;
			}
			
			return getExitTime().compareTo(r.getExitTime());
		} catch (BadReservationException e) {
			//e.printStackTrace();
			return 0;
		}
	}
}
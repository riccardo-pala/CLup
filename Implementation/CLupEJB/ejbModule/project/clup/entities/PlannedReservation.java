package project.clup.entities;

import java.sql.Time;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue("p")
@NamedQuery(name = "PlannedReservation.findScheduling", 
			query = "SELECT r "
					+ "FROM PlannedReservation r JOIN r.supermarket s "
					+ "WHERE s.id = :sId AND r.entranceTime IS NULL "
					+ "ORDER BY r.startTime ASC")
public class PlannedReservation extends Reservation {
	
	@Column(name="starttime")
	private Time startTime;
	
	
	/** 
	 * 
	 * Constructors, getters and setters
	 * 
	 */
	
	public PlannedReservation() {
		super();
	}
	
	public PlannedReservation(Supermarket supermarket, User user, int visitDuration, Time entranceTime, Time maxToleratedDelay, Time startTime) {
		super(supermarket, user, visitDuration, entranceTime, maxToleratedDelay);
		this.startTime = startTime;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	
	public void getStartTime(Time startTime) {
		this.startTime = startTime;
	}

	@Override
	public LocalTime getExitTime() {
		return startTime.toLocalTime().plusMinutes(visitDuration);
	}
	
	@Override
	public String toString() {
		return "(P, id: " + id + ", st: " + startTime + ", vd: " + visitDuration + ")";
	}
}
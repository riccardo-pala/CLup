package project.clup.utilities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import project.clup.entities.PlannedReservation;
import project.clup.exceptions.BadReservationException;

public class Timetable {

	private HashMap<Integer, ArrayList<PlannedReservation>> schedule;
	
	public Timetable() {
		schedule = new HashMap<Integer, ArrayList<PlannedReservation>>();
	}
	
	public Timetable(ArrayList<PlannedReservation> resList) throws BadReservationException {
		schedule = new HashMap<Integer, ArrayList<PlannedReservation>>();
		computeSchedules(resList);
	}
	
	public HashMap<Integer, ArrayList<PlannedReservation>> getSchedule() {
		return schedule;
	}
	
	public void computeSchedules(ArrayList<PlannedReservation> resList) throws BadReservationException {
		
		PlannedReservation r;
		LocalTime startTime;
		Integer slot;
		ArrayList<PlannedReservation> slotList;
		
		for (int i=0; i<resList.size(); i++) {
			
			r = resList.get(i);
			try {
				startTime = r.getStartTime().toLocalTime();
			} catch(NullPointerException e) {
				//e.printStackTrace();
				throw new BadReservationException("Null start time.");
			}
			
			slot = getSlotFromTime(startTime);
			
			slotList = schedule.get(slot);
			
			if (slotList == null)
				schedule.put(slot, slotList = new ArrayList<PlannedReservation>());
			
			slotList.add(r);
		}
	}
	
	public ArrayList<PlannedReservation> getAllScheduledNotScanned(LocalTime start, LocalTime end) {
		
		ArrayList<PlannedReservation> scheduledNotScanned = new ArrayList<PlannedReservation>();
		ArrayList<PlannedReservation> slotList;
		
		Integer startSlot = getSlotFromTime(start);
		Integer endSlot = getSlotFromTime(end);
		
		Integer counter = startSlot;
		
		while (counter<=endSlot) {
			
			slotList = schedule.get(counter);
			
			if (slotList != null)
				for (PlannedReservation r : slotList)
					if (r.getEntranceTime() == null)
						scheduledNotScanned.add(r);
			
			counter+=20;
		}
		
		return scheduledNotScanned;
	}
	
	public Integer getSlotFromTime(LocalTime t) {
		
		return 100 * t.getHour() + t.getMinute() - t.getMinute() % 20;
	}
	
	public int size() {
		
		int count = 0;
		for(int i = 0; i <= 2340; i+=20)
			if(getSchedule().get(i) != null)
				count += getSchedule().get(i).size();
		return count;
	}
}

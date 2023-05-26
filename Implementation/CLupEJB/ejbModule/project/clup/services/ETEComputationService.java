package project.clup.services;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import project.clup.entities.PlannedReservation;
import project.clup.entities.RealTimeReservation;
import project.clup.entities.Reservation;
import project.clup.exceptions.BadReservationException;
import project.clup.utilities.Timetable;

@Stateless
public class ETEComputationService {

	@PersistenceContext(unitName = "CLupEJB")
	private EntityManager em;
	
	public ETEComputationService() {
	}
	
	public LocalTime computeETE(RealTimeReservation r) throws BadReservationException {

		LocalTime ETE;
		
		System.out.println("COMPUTING ETE OF RESERVATION " + r.getCode() + "...");
		ArrayList<RealTimeReservation> virtualLineUp = r.getSupermarket().getVirtualLineUp();
		
		for (int i=0; i<virtualLineUp.size(); i++)
			for (int j=i+1; j<virtualLineUp.size(); j++)
				if(virtualLineUp.get(j-1).getTicketNumber() > virtualLineUp.get(j).getTicketNumber())
					Collections.swap(virtualLineUp, j-1, j);
		
		ArrayList<Reservation> doingGroceryShopping = r.getSupermarket().getDoingGroceryShopping();
		Timetable timetable = r.getSupermarket().getTimetable();
		LocalTime now = LocalTime.now();
		
		int queuePosition = 0;

		if (r.getTicketNumber() < 0 || !virtualLineUp.contains(r))
			queuePosition = virtualLineUp.size();
		else
			for (int i=0; i<virtualLineUp.size(); i++)
				if (virtualLineUp.get(i).getTicketNumber() == r.getTicketNumber())
					queuePosition = i;

		ArrayList<PlannedReservation> precScheds = timetable.getAllScheduledNotScanned(now, now.plusMinutes(r.getVisitDuration()));
		
		int allowedEntrances = r.getSupermarket().getMaxCapacity() - doingGroceryShopping.size() - precScheds.size();

		if (queuePosition < allowedEntrances) {
			ETE = LocalTime.now();
			System.out.println("ETE of Reservation " + r.getCode() + ": " + ETE);
			if(r.getMaxToleratedDelay() == null)
				r.setMaxToleratedDelay(Time.valueOf(ETE.plusMinutes(10)));
			return ETE;
		}

		ArrayList<Reservation> precList = new ArrayList<Reservation>();

		for (Reservation res : doingGroceryShopping)
			precList.add(res);

		for (Reservation res : precScheds)
			precList.add(res);

		for (int i=0; i<queuePosition; i++)
			precList.add(virtualLineUp.get(i));

		int n;
		Reservation precRes;

		do {
			
			n = precScheds.size();

			sortByExitTime(precList);

			precRes = precList.get(queuePosition - allowedEntrances);

			ETE = precRes.getExitTime();

			precScheds = timetable.getAllScheduledNotScanned(now, ETE.plusMinutes(r.getVisitDuration()-1));

			allowedEntrances = r.getSupermarket().getMaxCapacity() - doingGroceryShopping.size() - precScheds.size();
			
			precList = new ArrayList<Reservation>();

			for (Reservation res : doingGroceryShopping)
				precList.add(res);

			for (Reservation res : precScheds)
				precList.add(res);
			
			for (int i=0; i<queuePosition; i++)
				precList.add(virtualLineUp.get(i));
		}
		while (n != precScheds.size());
		
		r.setETE(ETE);
		
		System.out.println("ETE of Reservation " + r.getCode() + ": " + ETE);
		
		r.setMaxToleratedDelay(Time.valueOf(ETE.plusMinutes(10)));
		
		return ETE;
	}
	
	private void sortByExitTime(ArrayList<Reservation> resList) {
		resList.sort(null);
	}
}

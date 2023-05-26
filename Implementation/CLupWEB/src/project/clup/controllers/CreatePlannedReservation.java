package project.clup.controllers;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import project.clup.entities.Supermarket;
import project.clup.entities.User;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.ReservationService;
import project.clup.services.Router;
import project.clup.services.SupermarketService;


@WebServlet("/CreatePlannedReservation")
public class CreatePlannedReservation extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;	

	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	@EJB(name="project.clup.services/ReservationService")
	private ReservationService reservationService;
	
	@EJB(name = "project.clup.services/SupermarketService")
	private SupermarketService supermarketService;
	
    public CreatePlannedReservation() {
        super();
     
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}


	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process("/WEB-INF/homepage.html", ctx, response.getWriter());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		Time startTime = null;
		int supermarketId = 0;
		int visitDuration = 0;
		 
		User user = (User) session.getAttribute("user");
		
		visitDuration = Integer.parseInt(request.getParameter("Visit"));
		supermarketId = Integer.parseInt( request.getParameter("supermarketId"));
		startTime = Time.valueOf(request.getParameter("TimeVisit"));
		
		List<Time> schedule;
		try {
			schedule = router.forwardAvailableScheduleRequest(supermarketId);
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		}
		
		if(	(	schedule.size() > 0
				&& startTime.toLocalTime().equals(schedule.get(schedule.size()-1).toLocalTime()) 
				&& (visitDuration == 40 || visitDuration == 60)
			)
			||
			(	schedule.size() > 1
				&& startTime.toLocalTime().equals(schedule.get(schedule.size()-2).toLocalTime()) 
				&& (visitDuration == 60)
			)
		) {
			
			Supermarket supermarket;
			try {
				supermarket = supermarketService.findsupermarketbyId(supermarketId);
			} catch (BadRetrievalException e) {
				//e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
				return;
			}
			
			ctx.setVariable("selectedsupermarket", supermarket);
			ctx.setVariable("schedule", schedule);
			ctx.setVariable("supermarketId", supermarketId);
			ctx.setVariable("exceedMsg", "Desired schedule exceeds closing time!");
			
			templateEngine.process("/WEB-INF/PlanYourVisit.html", ctx, response.getWriter());
		} 
		else {
			String path;
			if(router.forwardPlannedReservationCreationRequest(supermarketId, startTime, visitDuration, user.getId())) {
				path = servletContext.getContextPath() + "/Homepage";
				response.sendRedirect(path);
			}
			else {
				
				Supermarket supermarket;
				try {
					supermarket = supermarketService.findsupermarketbyId(supermarketId);
				} catch (BadRetrievalException e) {
					//e.printStackTrace();
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
					return;
				}

				ctx.setVariable("selectedsupermarket", supermarket);
				ctx.setVariable("schedule", schedule);
				ctx.setVariable("supermarketId", supermarketId);
				ctx.setVariable("exceedMsg", "Desired schedule is not available since the selected supermarket will have reached the maximum capacity!");
				
				templateEngine.process("/WEB-INF/PlanYourVisit.html", ctx, response.getWriter());
			}
		}
		
		/*
		java.sql.Time sqlTime = entranceTime;
		LocalTime exitTime = sqlTime.toLocalTime();
		
		exitTime = exitTime.plusMinutes(visitDuration);
		
		java.sql.Time sqlTime1  = schedule.get(schedule.size()-1);
		LocalTime lastTimeSlot = sqlTime1.toLocalTime();
		
		if(exitTime.isBefore(lastTimeSlot)) {
		
		resService.insertPlannedReservation(visitDuration, entranceTime,user.getId(), supermarketId);
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Homepage";
		response.sendRedirect(path);
		}
		else
			this.doGet(request, response);
		*/
	}

}

package project.clup.controllers;

import java.io.IOException;
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

import project.clup.entities.RealTimeReservation;
import project.clup.entities.Reservation;
import project.clup.entities.User;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.ReservationService;
import project.clup.services.Router;
import project.clup.services.SupermarketService;
import project.clup.services.UserService;

/**
 * Servlet implementation class GoToHomePage
 */
@WebServlet("/GoToMyReservation")
public class GoToMyReservation extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;	

	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	@EJB(name = "project.clup.services/SupermarketService")
	private SupermarketService supermarketService;
	
	@EJB(name = "project.clup.services/UserService")
	private UserService userService;
	
	@EJB(name="project.clup.services/ReservationService")
	private ReservationService reservationService;
       
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
    public GoToMyReservation() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String loginpath = getServletContext().getContextPath() + "/index.html";
		
		HttpSession session = request.getSession();
		
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		
		Reservation reservation;
		try {
			reservation = router.forwardReservationDetailsRequest(user.getId());
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if (reservation != null) {	
		
			char reservationType;
			
			if(reservation.getClass().equals(RealTimeReservation.class))
				reservationType = 'r';
			else
				reservationType = 'p';
			
			try {
				if(reservationService.expiredReservation(reservation.getCode(), reservationType))
					ctx.setVariable("errorMsg", "Expired reservation!");
				else {
					ctx.setVariable("reservation", reservation);
					ctx.setVariable("reservationtype", reservationType);
				}
			} catch (BadRetrievalException e) {
				//e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
				return;
			}
		}
		else {
			ctx.setVariable("errorMsg", "You currently have no active reservations!");
		}
		
		templateEngine.process("/WEB-INF/MyReservation.html", ctx, response.getWriter());	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
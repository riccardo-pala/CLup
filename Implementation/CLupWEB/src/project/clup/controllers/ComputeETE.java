package project.clup.controllers;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.services.ETEComputationService;
import project.clup.services.ReservationService;
import project.clup.services.Router;
import project.clup.services.SupermarketService;
import project.clup.services.UserService;

/**
 * Servlet implementation class MakeRealtimeReservation
 */
@WebServlet("/ComputeETE")
public class ComputeETE extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;	

	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	@EJB(name = "project.clup.services/UserService")
	private UserService userService;
	
	@EJB(name = "project.clup.services/SupermarketService")
	private SupermarketService supermarketService;
	
	@EJB(name = "project.clup.services/ETEComputationService")
	private ETEComputationService eteComputationService;
	
	@EJB(name = "project.clup.services/ReservationService")
	private ReservationService reservationService;
       
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
    public ComputeETE() {
        super();
       
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer supermarketId = null;
		Integer visitDuration = null;
		
		try {
			supermarketId = Integer.parseInt(request.getParameter("supermarketid"));
			visitDuration = Integer.parseInt(request.getParameter("visitduration"));
		} catch (NumberFormatException | NullPointerException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		Supermarket supermarket;
		try {
			supermarket = supermarketService.findsupermarketbyId(supermarketId);
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		}
		
		LocalTime ETE = null;
		
		try {
			ETE = router.forwardETEComputationRequest(supermarketId, visitDuration);
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		} catch (BadReservationException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reservation start time");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if (ETE.isAfter(supermarket.getClosingtime().toLocalTime().minusMinutes(visitDuration))) {
			ctx.setVariable("errorMsg", "Supermarket cannot accept more reservations for today. Please, try back tomorrow!");
			ctx.setVariable("ete", null);
		}
		else {
			ctx.setVariable("ete", ETE.format(DateTimeFormatter.ofPattern("HH:mm")));
			ctx.setVariable("sid", supermarketId);
			ctx.setVariable("vd", visitDuration);
		}
		
		templateEngine.process("/WEB-INF/BookNowETE.html", ctx, response.getWriter());
	}

}
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

import project.clup.entities.User;
import project.clup.exceptions.BadReservationException;
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.UpdateProfileException;
import project.clup.exceptions.UpdateSupermarketException;
import project.clup.services.ETEComputationService;
import project.clup.services.ReservationService;
import project.clup.services.Router;
import project.clup.services.SupermarketService;


@WebServlet("/CreateRealTimeReservation")
public class CreateRealTimeReservation extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;	

	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	@EJB(name="project.clup.services/ReservationService")
	private ReservationService reservationService;
	
	@EJB(name = "project.clup.services/SupermarketService")
	private SupermarketService supermarketService;
	
	@EJB(name = "project.clup.services/ETEComputationService")
	private ETEComputationService eteComputationService;       
    
    public CreateRealTimeReservation() {
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
		
		int supermarketId;
		int visitDuration;
		
		User user = (User) session.getAttribute("user");
		
		try {
			supermarketId = Integer.parseInt(request.getParameter("supermarketid"));
			visitDuration = Integer.parseInt(request.getParameter("visitduration"));
		} catch (NumberFormatException | NullPointerException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try {
			if (!router.forwardRealTimeReservationCreationRequest(visitDuration, user.getId(), supermarketId)) {
				ctx.setVariable("errorMsg", "Supermarket cannot accept more Real Time Reservations. Please, try back tomorrow!");
				templateEngine.process("/RealTimeReservation.html", ctx, response.getWriter());
				return;
			}
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		} catch (UpdateProfileException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to update the profile");
			return;
		} catch (UpdateSupermarketException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to update the supermarket");
			return;
		} catch (BadReservationException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reservation start time");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Homepage";
		response.sendRedirect(path);
	}

}

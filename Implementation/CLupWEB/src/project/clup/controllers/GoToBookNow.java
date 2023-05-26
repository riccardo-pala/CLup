package project.clup.controllers;

import java.io.IOException;
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
import project.clup.services.UserService;

/**
 * Servlet implementation class MakePlannedReservation
 */
@WebServlet("/GoToBookNow")
public class GoToBookNow extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;
	
	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	@EJB(name = "project.clup.services/UserService")
	private UserService userService;
	
	@EJB(name = "project.clup.services/SupermarketService")
	private SupermarketService supermarketService;
	
	@EJB(name = "project.clup.services/ReservationService")
	private ReservationService reservationService;
       
       
   
    public GoToBookNow() {
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
		
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		User user = (User) session.getAttribute("user");
		User userPersisted = null;
		try {
			userPersisted = userService.findByUserName(user.getUsername());
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		List<Supermarket> supermarkets;
		try {
			supermarkets = router.forwardSupermarketListRequest();
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		}
		ctx.setVariable("supermarkets", supermarkets);
		
		if(userPersisted.getReservation() != null) {
			ctx.setVariable("errorMsg", "You already have an active reservation!");
			templateEngine.process("/WEB-INF/PlanYourVisit.html", ctx, response.getWriter());
			return;
		}
		
		templateEngine.process("/WEB-INF/BookNow.html", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

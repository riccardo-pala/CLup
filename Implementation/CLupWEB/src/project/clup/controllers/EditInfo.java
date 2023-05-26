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
import project.clup.exceptions.BadRetrievalException;
import project.clup.exceptions.UpdateProfileException;
import project.clup.services.Router;

/**
 * Servlet implementation class MakePlannedReservation
 */
@WebServlet("/EditInfo")
public class EditInfo extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;
	
	@EJB(name = "project.clup.services/Router")
	private Router router;
	
    public EditInfo() {
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

		String username = request.getParameter("username");
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		
		User user = (User) session.getAttribute("user");
		User userPersisted = null;
		
		try {
			userPersisted = router.forwardUserDetailsRequest(user.getUsername());
		} catch (BadRetrievalException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(userPersisted == null) {
			ctx.setVariable("errorMsg", "Error while retrieving user info, please retry later.");
			templateEngine.process("/WEB-INF/EditInfo.html", ctx, response.getWriter());
		}
		else {
			if(username==userPersisted.getUsername() || username.isEmpty()) username = null;
			if(firstname==userPersisted.getFirstname() || firstname.isEmpty()) firstname = null;
			if(lastname==userPersisted.getLastname() || lastname.isEmpty()) lastname = null;
			
			if(username!=null || firstname!=null || lastname!=null)
				try {
					user = router.forwardProfileUpdateRequest(userPersisted.getUsername(), username, firstname, lastname, null, null);
				} catch (UpdateProfileException e) {
					//e.printStackTrace();
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to update profile information");
					return;
				} catch (BadRetrievalException e) {
					//e.printStackTrace();
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to retrieve data");
					return;
				}
			
			request.getSession().setAttribute("user", user);
			templateEngine.process("/WEB-INF/ShowProfile.html", ctx, response.getWriter());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
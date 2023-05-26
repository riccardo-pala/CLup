package project.clup.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
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
import project.clup.exceptions.CredentialsException;
import project.clup.exceptions.UpdateProfileException;
import project.clup.services.Router;

/**
 * Servlet implementation class MakePlannedReservation
 */
@WebServlet("/EditPassword")
public class EditPassword extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;
	
	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	public EditPassword() {
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
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		String newpasswordconfirm = request.getParameter("newpasswordconfirm");
		
		User user = (User) session.getAttribute("user");
		
		if(oldpassword==null || newpassword==null || newpasswordconfirm==null ||
				oldpassword.isEmpty() || newpassword.isEmpty() || newpasswordconfirm.isEmpty()) {
			ctx.setVariable("errorMsg", "Please fill all form fields.");
			templateEngine.process("/WEB-INF/EditPassword.html", ctx, response.getWriter());
			return;
		}
		
		User userChecked = null;
		try {
			userChecked = router.forwardSignInRequest(user.getUsername(), oldpassword);
		} catch (NonUniqueResultException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_CONFLICT, "Could not verify credentials due to conflicting data");
			return;
		} catch (CredentialsException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Could not verify credentials");
			return;
		}
		
		if(userChecked == null) ctx.setVariable("errorMsg", "Password is incorrect.");
		else if(!newpassword.equals(newpasswordconfirm)) ctx.setVariable("errorMsg", "New password confirmation is incorrect.");
		else if(newpassword.equals(oldpassword)) ctx.setVariable("errorMsg", "Please insert a password different from the previous.");
		else {
			try {
				user = router.forwardProfileUpdateRequest(userChecked.getUsername(), null, null, null, newpassword, null);
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
			return;
		}
		
		templateEngine.process("/WEB-INF/EditPassword.html", ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
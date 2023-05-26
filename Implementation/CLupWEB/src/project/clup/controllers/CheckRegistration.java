package project.clup.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import project.clup.services.Router;
import project.clup.services.UserService;
import project.clup.entities.User;
import project.clup.exceptions.CreateProfileException;
import project.clup.exceptions.CredentialsException;

/**
 * Servlet implementation class CheckRegistration
 */
@WebServlet("/CheckRegistration")
public class CheckRegistration extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;
	
	@EJB(name = "project.clup.services/Router")
	private Router router;
	
	@EJB(name = "project.clup.services/UserService")
	private UserService userService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckRegistration() {
        super();
        // TODO Auto-generated constructor stub
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = null;
		String password = null;
		String lastname = null;
		String firstname = null;
		
		try {
			username = request.getParameter("username");
			password = request.getParameter("pwd");
			lastname= request.getParameter("lastname");
			firstname= request.getParameter("firstname");
			if (username == null || password == null || lastname==null || firstname==null||
					lastname.isEmpty()||firstname.isEmpty()|| username.isEmpty() || password.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
		
		User user = null;
		
		try {
			user = router.forwardSignUpRequest(username, firstname,lastname, password);
		} catch (CredentialsException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_CONFLICT, "Could not verify credentials");
			return;
		} catch (CreateProfileException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Could not create a new profile");
			return;
		}
		String path;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if (user == null) {
			ctx.setVariable("errorMsg", "The username is already used by another account");
			path = "/Registration.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			/*
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Homepage";
			response.sendRedirect(path);
			*/
		}
		
		
	}

}

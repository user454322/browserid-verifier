package info.modprobe.browserid.sample.servlet;

import info.modprobe.browserid.BrowserIDResponse;
import info.modprobe.browserid.BrowserIDResponse.Status;
import info.modprobe.browserid.Verifier;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = "/in")
public class In extends HttpServlet {

	private static final long serialVersionUID = -452837824924983487L;
	private static final Logger log = LoggerFactory.getLogger(In.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();

		final Verifier verifier = new Verifier();
		final String audience = req.getServerName();
		final String assertion = req.getParameter("assertion");
		BrowserIDResponse personaResponse = verifier
				.verify(assertion, audience);
		Status status = personaResponse.getStatus();
		if (status == Status.OK) {
			String email = personaResponse.getEmail();
			log.info("{} has sucessfully signed in", email);
			HttpSession session = req.getSession(true);
			session.setAttribute("email", email);
			out.print("okay");

		} else {
			log.info("Failed...");
			out.print("failure");
		}

		out.close();
	}
}

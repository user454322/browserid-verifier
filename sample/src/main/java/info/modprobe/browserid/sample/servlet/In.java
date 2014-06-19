package info.modprobe.browserid.sample.servlet;

import info.modprobe.browserid.BrowserIDResponse;
import info.modprobe.browserid.BrowserIDResponse.Status;
import info.modprobe.browserid.Verifier;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

	private static final long serialVersionUID = -452837824924983489L;
	private static final Logger log = LoggerFactory.getLogger(In.class);

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		URL url = null;
		try {
			url = new URL(req.getRequestURL().toString());
		} catch (MalformedURLException ignored) {
		}
		StringBuilder urlBuilder = new StringBuilder()
				.append(url.getProtocol()).append("://").append(url.getHost())
				.append(':').append(url.getPort());
		final String audience = urlBuilder.toString();
		final String assertion = req.getParameter("assertion");
		final Verifier verifier = new Verifier();
		final BrowserIDResponse personaResponse = verifier.verify(assertion,
				audience);
		final Status status = personaResponse.getStatus();

		if (status == Status.OK) {
			/* Authentication with Persona was successful */
			String email = personaResponse.getEmail();
			log.info("{} has sucessfully signed in", email);
			HttpSession session = req.getSession(true);
			session.setAttribute("email", email);

		} else {
			/* Authentication with Persona failed */
			log.info("Sign in failed: {}", personaResponse.getReason());

		}

	}
}

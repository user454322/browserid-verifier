package info.modprobe.browserid.sample.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(value = "/out")
public class Out extends HttpServlet {

	private static final long serialVersionUID = -2559085906064523567L;

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		final HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}

	}

}

package info.modprobe.browserid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import info.modprobe.browserid.BrowserIDResponse.Status;

import java.util.Date;

import org.junit.Test;

public class BrowserIDResponseTest {

	private static final String AUDIENCE = "example.com";
	private static final String EMAIL = "bob@mail.com";
	private static final long EXPIRES = 1223334444150L;
	private static final String ISSUER = "login.persona.org";
	private static final String REASON = "assertion has expired";

	private static final String OKAY_RESPONSE = "{\"audience\":\"example.com\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}";
	private static final String FAILURE_RESPONSE = "{\"status\":\"failure\",\"reason\":\"assertion has expired\"}";
	private static final String INVALID_STATUS_RESPONSE = "{\"audience\":\"example.com\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okayy\"}";
	private static final String NO_EXPIRES_RESPONSE = "{\"audience\":\"example.com\",\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}";
	private static final String INVALID_RESPONSE = "\"status\":\"failure\",\"reason\":\"assertion has expired\"";
	private static final String NO_STATUS_RESPONSE = "{\"audience\":\"example.com\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\"}";
	private static final String WRONG_EXPIRES_TYPE_RESPONSE = "{\"audience\":\"example.com\",\"expires\":\"1223334444150\",\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}";
	private static final String WRONG_AUDIENCE_TYPE_RESPONSE = "{\"audience\":\"example.com\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}";

	@Test
	public void testOkayResponse() {
		final BrowserIDResponse ok = new BrowserIDResponse(OKAY_RESPONSE);
		assertEquals(Status.OK, ok.getStatus());
		assertEquals(AUDIENCE, ok.getAudience());
		assertEquals(EMAIL, ok.getEmail());
		assertEquals(new Date(EXPIRES), ok.getExpires());
		assertEquals(ISSUER, ok.getIssuer());
		assertNull(ok.getReason());
	}

	@Test
	public void testFailureResponse() {
		final BrowserIDResponse fail = new BrowserIDResponse(FAILURE_RESPONSE);
		assertEquals(Status.FAILURE, fail.getStatus());
		assertNull(fail.getAudience());
		assertNull(fail.getEmail());
		assertNull(fail.getExpires());
		assertNull(fail.getIssuer());
		assertEquals(REASON, fail.getReason());
	}

	@Test
	public void testResponseWithoutExpires() {
		final BrowserIDResponse expires = new BrowserIDResponse(
				NO_EXPIRES_RESPONSE);
		assertEquals(Status.OK, expires.getStatus());
		assertEquals(AUDIENCE, expires.getAudience());
		assertEquals(EMAIL, expires.getEmail());
		assertNull(expires.getExpires());
		assertEquals(ISSUER, expires.getIssuer());
		assertNull(expires.getReason());
	}

	@Test(expected = BrowserIDException.class)
	public void testInvalidResponse() {
		final BrowserIDResponse fail = new BrowserIDResponse(INVALID_RESPONSE);
		assertEquals(Status.FAILURE, fail.getStatus());
		assertNull(fail.getAudience());
		assertNull(fail.getEmail());
		assertNull(fail.getExpires());
		assertNull(fail.getIssuer());
		assertEquals(REASON, fail.getReason());
	}

	@Test(expected = BrowserIDException.class)
	public void testNoStatusResponse() {
		new BrowserIDResponse(
				NO_STATUS_RESPONSE);
	}

	@Test(expected = BrowserIDException.class)
	public void testWrongExpireTypeResponse() {
		new BrowserIDResponse(WRONG_EXPIRES_TYPE_RESPONSE);
	}

	@Test(expected = BrowserIDException.class)
	public void invalidStatus(){
		BrowserIDResponse browserIDResponse = new BrowserIDResponse(INVALID_STATUS_RESPONSE);
		browserIDResponse.getStatus();
	}
	
}

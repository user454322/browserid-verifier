/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

import static info.modprobe.browserid.TestUtils.AUDIENCE;
import static info.modprobe.browserid.TestUtils.EMAIL;
import static info.modprobe.browserid.TestUtils.EXPIRES;
import static info.modprobe.browserid.TestUtils.INVALID_STATUS;
import static info.modprobe.browserid.TestUtils.ISSUER;
import static info.modprobe.browserid.TestUtils.REASON;
import static info.modprobe.browserid.TestUtils.RESPONSE_FAILURE;
import static info.modprobe.browserid.TestUtils.RESPONSE_INVALID_JSON;
import static info.modprobe.browserid.TestUtils.RESPONSE_INVALID_STATUS;
import static info.modprobe.browserid.TestUtils.RESPONSE_MIXED;
import static info.modprobe.browserid.TestUtils.RESPONSE_NO_EXPIRES;
import static info.modprobe.browserid.TestUtils.RESPONSE_NO_STATUS;
import static info.modprobe.browserid.TestUtils.RESPONSE_OKAY;
import static info.modprobe.browserid.TestUtils.RESPONSE_WRONG_AUDIENCE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import info.modprobe.browserid.BrowserIDResponse.Status;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BrowserIDResponseTest {


	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void failureResponse() {
		final BrowserIDResponse fail = new BrowserIDResponse(RESPONSE_FAILURE);
		assertEquals(Status.FAILURE, fail.getStatus());
		assertNull(fail.getAudience());
		assertNull(fail.getEmail());
		assertNull(fail.getExpires());
		assertNull(fail.getIssuer());
		assertEquals(REASON, fail.getReason());
	}

	@Test
	public void invalidResponse() {
		expectedException.expect(BrowserIDException.class);
		new BrowserIDResponse(RESPONSE_INVALID_JSON);
	}

	@Test
	public void invalidStatus() {
		expectedException.expect(BrowserIDException.class);
		expectedException.expectMessage(String.format("Invalid status '%s'",
				INVALID_STATUS));
		BrowserIDResponse browserIDResponse = new BrowserIDResponse(
				RESPONSE_INVALID_STATUS);
		browserIDResponse.getStatus();
	}

	@Test
	public void noStatusResponse() {
		expectedException.expect(BrowserIDException.class);
		new BrowserIDResponse(RESPONSE_NO_STATUS);
	}

	@Test
	public void mixedResponse() {
		expectedException.expect(BrowserIDException.class);
		expectedException.expectMessage("Invalid JSON");
		new BrowserIDResponse(RESPONSE_MIXED);
	}

	@Test
	public void okayResponse() {
		final BrowserIDResponse ok = new BrowserIDResponse(RESPONSE_OKAY);
		assertEquals(Status.OK, ok.getStatus());
		assertEquals(AUDIENCE, ok.getAudience());
		assertEquals(EMAIL, ok.getEmail());
		assertEquals(new Date(EXPIRES), ok.getExpires());
		assertEquals(ISSUER, ok.getIssuer());
		assertNull(ok.getReason());
	}

	@Test
	public void responseWithoutExpires() {
		final BrowserIDResponse expires = new BrowserIDResponse(
				RESPONSE_NO_EXPIRES);
		assertEquals(Status.OK, expires.getStatus());
		assertEquals(AUDIENCE, expires.getAudience());
		assertEquals(EMAIL, expires.getEmail());
		assertNull(expires.getExpires());
		assertEquals(ISSUER, expires.getIssuer());
		assertNull(expires.getReason());
	}

	@Test
	public void wrongAudienceTypeResponse() {
		expectedException.expect(BrowserIDException.class);
		new BrowserIDResponse(RESPONSE_WRONG_AUDIENCE_TYPE);
	}

}

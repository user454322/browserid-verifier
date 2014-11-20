/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * A response from a BrowserID verification service. The status via
 * {@link #getStatus()} method will always be available, other fields'
 * availability depend on its value.
 * 
 * 
 * @see <a
 *      href="https://developer.mozilla.org/en/Persona/Remote_Verification_API">Persona's
 *      <i>Remote Verification API</i> </a>
 * 
 * 
 * <br/>
 * <br/>
 *      The majority of the code on this class has been borrowed from <a href=
 *      "https://github.com/mozilla/browserid-cookbook/blob/109ff3f1446ce3f28bdb09bf500749a6fa4cca55/java/spring/src/pt/webdetails/browserid/BrowserIdResponse.java"
 *      >Mozilla's browser-id cook book BrowserIdResponse</a> class
 */
public class BrowserIDResponse {

	private final String audience;
	private final String email;
	private final long expires;
	private final String issuer;
	private JSONResponse jsonResponse;
	private final String reason;
	private final Status status;

	/**
	 * 
	 * @return status of the verification. Will be {@link Status#OK} if the
	 *         assertion is valid.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * 
	 * @return email address (the identity) that was verified
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @return domain for which the assertion is valid
	 */
	public String getAudience() {
		return audience;
	}

	/**
	 * 
	 * @return Expiration date for the assertion. {@code null} if the response
	 *         doesn't contain this field
	 */
	public Date getExpires() {
		if (expires == 0) {
			return null;
		}
		return new Date(expires);
	}

	/**
	 * 
	 * @return domain of the certifying authority
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * 
	 * @return reason for verification failure
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * 
	 * @param response
	 *            result of a call to a BrowserID verify service
	 * @throws BrowserIDException
	 *             if the response cannot be parsed or the <i>status</i> is
	 *             invalid, i.e., not "okay" nor "failure"
	 */
	public BrowserIDResponse(final String response) {
		try {

			jsonResponse = new JSONResponse(response);
			status = Status.parse(jsonResponse.getStatus());

			switch (status) {
			case OK:
				audience = jsonResponse.getAudience();
				email = jsonResponse.getEmail();
				expires = jsonResponse.getExpires();
				issuer = jsonResponse.getIssuer();
				reason = null;
				try {
					validateAudience();
					validateEmail();

				} catch (final MalformedURLException
						| MalformedEmailAddressException illegalURLOrEmail) {
					throw new BrowserIDException(new IllegalArgumentException(
							illegalURLOrEmail));
				}
				break;

			case FAILURE:
				audience = null;
				email = null;
				expires = 0;
				issuer = null;
				reason = jsonResponse.getReason();
				break;

			default:
				throw new BrowserIDException(new IllegalArgumentException(
						"Invalid response status"));
			}

		} catch (final BrowserIDException invalidStatusExc) {
			throw invalidStatusExc;

		}
	}

	/**
	 * BrowserID response status
	 */
	public enum Status {
		/**
		 * Verification was successful. ("okay")
		 */
		OK("okay"),
		/**
		 * Verification failed. ("failure")
		 */
		FAILURE("failure");

		private String value;

		Status(String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}

		public static Status parse(String value) {
			if (OK.value.equals(value)) {
				return OK;
			}
			return FAILURE;
		}
	}

	public String toString() {
		return jsonResponse.toString();
	}

	/*
	 * audience
	 * The protocol, domain name, and port of your site. For example, 
	 * "https://example.com:443".
	 * See: https://developer.mozilla.org/en/Persona/Remote_Verification_API 
	 */
	public void validateAudience() throws MalformedURLException {
		new URL(this.audience);
	}

	public void validateEmail() throws MalformedEmailAddressException {
		if (this.email == null || !this.email.contains("@")) {
			throw new MalformedEmailAddressException(String.format(
					"Invalid email '%s'", this.email));
		}
	}

}

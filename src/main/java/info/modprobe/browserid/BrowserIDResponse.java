/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A response from a BrowserID verification service. The status via
 * {@link #getStatus()} method will always be available, other fields'
 * availability depend on its value.
 * 
 * 
 * @see <a
 *      href="https://developer.mozilla.org/en/Persona/Remote_Verification_API"
 *      >Persona's <i>Remote Verification API</i> </a>
 * 
 * 
 * <br/>
 * <br/>
 *      The majority of the code on this class has been borrowed from <a href=
 *      "https://github.com/mozilla/browserid-cookbook/blob/109ff3f1446ce3f28bdb09bf500749a6fa4cca55/java/spring/src/pt/webdetails/browserid/BrowserIdResponse.java"
 *      > Mozilla's browser-id cook book BrowserIdResponse</a> class
 */
public class BrowserIDResponse {

	private final String audience;
	private final String email;
	private final long expires;
	private final String issuer;
	private JSONResponse jsonResponse;
	private final String reason;
	private final Status status;

	private static final Logger log = LoggerFactory
			.getLogger(BrowserIDResponse.class);

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
	public BrowserIDResponse(String response) {
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

		} catch (BrowserIDException exc) {
			log.error(exc.toString(), exc);
			throw new BrowserIDException(exc);
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

}

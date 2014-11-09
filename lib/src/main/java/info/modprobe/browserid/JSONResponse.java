/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

import org.json.JSONException;
import org.json.JSONObject;

import info.modprobe.browserid.BrowserIDResponse.Status;

/**
 * Class to handle JSON responses from the issuer.
 * 
 * Although there are plenty of JSON libraries, is desired to keep the external
 * dependencies to the minimum possible, and since the response is very simple
 * it is feasible to parse the response here instead of using a JSON library.
 * But for the time being we are using json.org. I want this library to be used
 * only for good but, I can't restrict what other people do.
 * 
 */
// TODO: Localize exceptions
class JSONResponse {

	private final String audience;
	private final String email;
	private final long expires;
	private final String issuer;
	private final JSONObject jsonObject;
	private final String reason;
	private final String response;
	private final String status;

	/**
	 * 
	 * @param response
	 *            The JSON response
	 * @throws BrowserIDException
	 *             If unable to parse the {@code response}
	 */
	JSONResponse(final String response) {
		this.response = response;

		try {
			jsonObject = new JSONObject(response);
		} catch (final JSONException exc) {
			throw new BrowserIDException("Invalid JSON", exc);
		}

		if ((status = getStringFromJSON("status")) == null) {
			throw new BrowserIDException("The response doesn't contain status");
		}
		if (!Status.OK.toString().equals(status)
				&& !Status.FAILURE.toString().equals(status)) {
			String message = String.format("Invalid status '%s' ", status);
			throw new BrowserIDException(message);
		}

		audience = getStringFromJSON("audience");
		email = getStringFromJSON("email");
		expires = getLongFromJSON("expires");
		issuer = getStringFromJSON("issuer");
		reason = getStringFromJSON("reason");
	}

	String getAudience() {
		return audience;
	}

	String getEmail() {
		return email;
	}

	long getExpires() {
		return expires;
	}

	String getIssuer() {
		return issuer;
	}

	String getReason() {
		return reason;
	}

	String getStatus() {
		return status;
	}

	private long getLongFromJSON(final String key) {
		if (jsonObject.has(key)) {
			try {				
				return jsonObject.getLong(key);

			} catch (final JSONException jsonExc) {
				throw new BrowserIDException(String.format(
						"Invalid value for '%s'", key));
			}
		}

		return 0L;
	}
		
	private String getStringFromJSON(final String key) {
		if (jsonObject.has(key)) {
			try {				
				return jsonObject.getString(key);

			} catch (final JSONException jsonExc) {
				throw new BrowserIDException(String.format(
						"Invalid value for '%s'", key));
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return response;
	}
}

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
 * But for the time being minimal-json is used.
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
		} catch (JSONException exc) {
			throw new BrowserIDException("Invalid JSON",exc);
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
		expires = jsonObject.get("expires") == null ? 0 : (long) jsonObject
				.get("expires");

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

	private String getStringFromJSON(final String str) {
		try {
			Object jsonStr = jsonObject.get(str);
			if (jsonStr != null) {
				return (String) jsonStr;
			}

		} catch (RuntimeException exc) {
			throw new BrowserIDException(String.format(
					"Couldn't get string value for '%s'", str));
		}
		return null;
	}

	@Override
	public String toString() {
		return response;
	}
}

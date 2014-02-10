package info.modprobe.browserid;

import info.modprobe.browserid.BrowserIDResponse.Status;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Class to handle JSON responses from the issuer.
 * 
 * Although there are plenty of JSON libraries, is desired to keep the external
 * dependencies to the minimum possible, and since the response is very simple
 * it is feasible to parse the response here instead of using a JSON library.
 * But for the time being minimal-json is used.
 * 
 */
class JSONResponse {

	private final String audience;
	private final String email;
	private final long expires;
	private final String issuer;
	private final JsonObject jsonObject;
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
			jsonObject = JsonObject.readFrom(response);
		} catch (RuntimeException exc) {
			throw new BrowserIDException(exc);
		}

		if ((status = getStringFromJSON("status")) == null) {
			throw new BrowserIDException("The response doesn't contain status");
		}
		if (!Status.OK.toString().equals(status)
				|| !Status.FAILURE.toString().equals(status)) {
			String message = String.format("Invalid status '%s' ", status);
			throw new BrowserIDException(message);
		}
		
		audience = getStringFromJSON("audience");
		email = getStringFromJSON("email");
		try {
			expires = jsonObject.get("expires") == null ? 0 : jsonObject.get(
					"expires").asLong();
		} catch (RuntimeException exc) {
			throw new BrowserIDException(exc);
		}
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
		JsonValue jsonVal = jsonObject.get(str);
		if (jsonVal != null) {
			return jsonVal.asString();
		}
		return null;
	}

	@Override
	public String toString() {
		return response;
	}
}
/*
 * {"status":"failure","reason":"assertion has expired"}
 * 
 * 
 * {"audience":"localhost","expires":1391698500490,"issuer":"login.persona.org",
 * "email":"javucho1000@yahoo.com.mx","status":"okay"}
 */
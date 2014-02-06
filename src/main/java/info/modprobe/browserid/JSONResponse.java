package info.modprobe.browserid;

/**
 * Class to handle JSON responses from the issuer.
 * 
 * Although there are plenty of JSON libraries, is desired to keep the external
 * dependencies to the minimum possible, and since the response is very simple
 * it is feasible to parse the response here instead of using a JSON library.
 * 
 * 
 */
class JSONResponse {

	private final String response;

	/**
	 * 
	 * @param response
	 *            The JSON response
	 * @throws BrowserIDException
	 *             If unable to parse the {@code response}
	 */
	JSONResponse(final String response) {
		this.response = response;
		boolean errorInParsingResponse = false;
		if (errorInParsingResponse) {
			throw new BrowserIDException("Unable to parse: " + response);
		}
	}

	String getAudience() {
		return null;
	}

	String getEmail() {
		return null;
	}

	long getExpires() {
		return 0;
	}

	String getIssuer() {
		return null;
	}

	String getReason() {
		return null;
	}

	String getStatus() {
		return null;
	}

	private String extractAudience() {
		return null;
	}

	private String extractEmail() {
		return null;
	}

	private long extractExpires() {
		return 0;
	}

	private String extractIssuer() {
		return null;
	}

	private String extractReason() {
		return null;
	}

	private String extractStatus() {
		return null;
	}

	@Override
	public String toString() {
		return response;
	}
}

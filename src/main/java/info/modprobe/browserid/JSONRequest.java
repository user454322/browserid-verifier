package info.modprobe.browserid;

/**
 * Class to create requests
 * 
 * Because the request is very simple and we want to keep the external
 * dependencies at the minimum this is class is used to create JSONRequests
 * instead of using a JSON library.
 * 
 */
class JSONRequest {

	private final String assertion;
	private final String audience;
	private final String request;
	private static final String REQUEST_TEMPLATE = "{\"audience\": \"%s\", \"assertion\": \"%s\"}";

	JSONRequest(final String assertion, final String audience) {
		this.assertion = assertion;
		this.audience = audience;
		request = String.format(REQUEST_TEMPLATE, audience, assertion);
	}

	String getAssertion() {
		return assertion;
	}

	String getAudience() {
		return audience;
	}

	@Override
	public String toString() {
		return request;
	}

}

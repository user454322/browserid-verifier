package info.modprobe.browserid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class JSONRequestTest {

	final String assertion = "W3XfU4v3...pU4SVafB";
	final String audience = "https://login.modprobe.info";

	@Test
	public void createJSONRequest() {
		JSONRequest request = new JSONRequest(assertion, audience);
		assertEquals("Assertions are not equal", request.getAssertion(),
				assertion);
		assertEquals("Audiences are not equal", request.getAudience(), audience);
	}

	@Test
	public void validateJSON() {
		final JSONRequest request = new JSONRequest(assertion, audience);
		final String requestStr = request.toString();
		try {
			new JSONObject(requestStr);

		} catch (JSONException exc) {
			fail(String.format("'%s' seems to be invalid", requestStr));
		}
	}

}

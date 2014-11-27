package info.modprobe.browserid;

public final class TestUtils {

	public static final String AUDIENCE = "https://example.com";
	public static final String EMAIL = "bob@mail.com";
	public static final long EXPIRES = 1223334444150L;
	public static final String INVALID_STATUS = "invStatu";
	public static final String ISSUER = "login.persona.org";
	public static final String REASON = "assertion has expired";

	/*Responses*/
	public static final String RESPONSE_FAILURE = "{\"status\":\"failure\",\"reason\":\"assertion has expired\"}";
	public static final String RESPONSE_INVALID_JSON = "\"status\":\"failure\",\"reason\":\"assertion has expired\"";
	public static final String RESPONSE_INVALID_STATUS = String
			.format("{\"audience\":\"%s\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"%s\"}",
					AUDIENCE, INVALID_STATUS);
	public static final String RESPONSE_MIXED = String
			.format("{\"audience\":\"%s\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"failure\",\"status\":\"okay\"}",
					AUDIENCE);
	public static final String RESPONSE_NO_EXPIRES = String
			.format("{\"audience\":\"%s\",\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}",
					AUDIENCE);
	public static final String RESPONSE_NO_STATUS = String
			.format("{\"audience\":\"%s\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\"}",
					AUDIENCE);
	public static final String RESPONSE_OKAY = String
			.format("{\"audience\":\"%s\",\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}",
					AUDIENCE);
	public static final String RESPONSE_WRONG_AUDIENCE_TYPE = "{\"audience\":true,\"expires\":1223334444150,\"issuer\":\"login.persona.org\", \"email\":\"bob@mail.com\",\"status\":\"okay\"}";

	
	private TestUtils() {
	}

}

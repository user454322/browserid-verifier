/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple client for a BrowserID verify call.
 * 
 * <br/>
 * The majority of the code on this class has been borrowed from <a href="https://github.com/mozilla/browserid-cookbook/blob/109ff3f1446ce3f28bdb09bf500749a6fa4cca55/java/spring/src/pt/webdetails/browserid/BrowserIdVerifier.java">
 * Mozilla's browser-id cook book BrowserIdVerifier</a> class
 */
public class Verifier {

	public static final String DEFAULT_URL = "https://verifier.login.persona.org/verify";
	private static final Logger log = LoggerFactory.getLogger(Verifier.class);
	private final String url;

	/**
	 * Creates a {@code Verifier} object with the default URL
	 * {@link Verifier#DEFAULT_URL}.
	 */
	public Verifier() {
		this.url = DEFAULT_URL;
	}

	public Verifier(final String url) {
		this.url = url;
	}

	public String getURL() {
		return url;
	}

	/***
	 * @param assertion
	 * @param audience
	 * @return the result of the verification
	 * 
	 * @throws {@code BrowserIDResponse} if there is a failure in the
	 *         verification process
	 * @throws {@code IllegalArgumentException} if assertion or audience are not
	 *         provided
	 */
	public BrowserIDResponse verify(final String assertion,
			final String audience) {
		log.debug("assertion: {}{} audience: {} ", assertion,
				System.lineSeparator(), audience);
		if (assertion == null || !(assertion.length() > 0)) {
			throw new IllegalArgumentException("assertion is mandatory");
		}
		if (audience == null || !(audience.length() > 0)) {
			throw new IllegalArgumentException("audience is mandatory");
		}

		try {
			/* Prepare connection */
			JSONRequest body = new JSONRequest(assertion, audience);
			log.debug("Verifying {} using: {}", body.toString(), this.url);
			URL verifierURL = new URL(this.url);
			String response = "";
			HttpsURLConnection connection = (HttpsURLConnection) verifierURL
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			/* Write to the connection */
			try (DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream())) {
				wr.writeBytes(body.toString());
				wr.flush();
			} catch (IOException wrExc) {
				throw wrExc;
			}

			/* Read from the connection */
			try (Scanner scanner = new Scanner(connection.getInputStream(),
					StandardCharsets.UTF_8.toString())) {
				response = scanner.useDelimiter("\\A").hasNext() ? scanner
						.next() : "";
			} catch (IOException rdExc) {
				throw rdExc;
			}

			log.debug("Response from verifier: [{}] {} {}",
					connection.getResponseCode(), System.lineSeparator(),
					response);

			return new BrowserIDResponse(response);

		} catch (IOException exc) {
			throw new BrowserIDException(exc);
		}
	}

}

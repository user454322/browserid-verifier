/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package info.modprobe.browserid;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple client for a BrowserID verify call.
 * 
 * <br/>
 * The majority of the code on this class has been borrowed from <a href=
 * "https://github.com/mozilla/browserid-cookbook/blob/109ff3f1446ce3f28bdb09bf500749a6fa4cca55/java/spring/src/pt/webdetails/browserid/BrowserIdVerifier.java"
 * > Mozilla's browser-id cook book BrowserIdVerifier</a> class
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

	@Override
	public String toString() {
		return String.format("URL: %s", this.url);
	}

	public BrowserIDResponse verify(final String assertion,
			final String audience){
		return verify(assertion, audience, 1, TimeUnit.MINUTES);
	}

	/***
	 * @param assertion
	 * @param audience
	 * @param timeOut
	 *            To wait for the verification process. Unlike
	 *            the low level {@link URLConnection#setConnectTimeout(int)} and
	 *            {@link URLConnection#setReadTimeout(int)} which set different
	 *            time outs for the connection and read processes, this a global
	 *            time out for the entire verification process.
	 *            Its value should be > 0.
	 *            
	 * @return the result of the verification
	 * 
	 * @throws {@code BrowserIDResponse} if there is a failure in the
	 *         verification process
	 * @throws {@code IllegalArgumentException} if the arguments are invalid.
	 */
	public BrowserIDResponse verify(final String assertion,
			final String audience, final long timeOut, final TimeUnit timeUnit) {
		log.debug("assertion: {}{} audience: {} ", assertion,
				System.lineSeparator(), audience);
		if (assertion == null || !(assertion.length() > 0)) {
			throw new IllegalArgumentException("assertion is mandatory");
		}
		if (audience == null || !(audience.length() > 0)) {
			throw new IllegalArgumentException("audience is mandatory");
		}
		if (timeOut <= 0) {
			throw new IllegalArgumentException("Timeout value should be > 0");
		}

		try {
			final JSONRequest body = new JSONRequest(assertion, audience);
			log.debug("Verifying {} using: {}", body.toString(), this.url);
			final URL verifierURL = new URL(this.url);
			final HttpsURLConnection connection = prepareConnection(verifierURL);
			String response = "";
			/* Execute the verification task */
			final ExecutorService verifierExecutor = Executors
					.newSingleThreadExecutor();
			final Callable<String> verifyTask = new VerifyTask(connection,
					body);
			final Future<String> verifyExcution = verifierExecutor
					.submit(verifyTask);

			verifierExecutor.shutdown();
			try {
				response = verifyExcution.get(timeOut, timeUnit);

			} catch (final InterruptedException interruptedExc) {
				log.warn("{} {}", interruptedExc.getLocalizedMessage(),
						interruptedExc);

			} catch (final TimeoutException tiomeOutExc) {
				throw new BrowserIDException(tiomeOutExc);

			} catch (final ExecutionException execExc) {
				throw new BrowserIDException(execExc.getCause());
			}

			log.debug("Response from verifier: [{}] {} {}",
					connection.getResponseCode(), System.lineSeparator(),
					response);

			return new BrowserIDResponse(response);

		} catch (final IOException ioexc) {
			throw new BrowserIDException(ioexc);
		}
	}

	private HttpsURLConnection prepareConnection(final URL verifierURL)
			throws IOException {

		final HttpsURLConnection connection = (HttpsURLConnection) verifierURL
				.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/json; charset=utf-8");
		connection.setDoOutput(true);

		return connection;
	}

	private static class VerifyTask implements Callable<String> {

		private final HttpsURLConnection connection;
		private final JSONRequest jsonRequest;

		VerifyTask(final HttpsURLConnection connection,
				final JSONRequest jsonRequest) {
			this.connection = connection;
			this.jsonRequest = jsonRequest;
		}

		@Override
		public String call() throws IOException {
			/* Write to the connection */
			try (final DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream())) {
				wr.writeBytes(jsonRequest.toString());
				wr.flush();
			}

			/* Read from the connection */
			try (final Scanner scanner = new Scanner(
					connection.getInputStream(),
					StandardCharsets.UTF_8.toString())) {
				return scanner.useDelimiter("\\A").hasNext() ? scanner.next()
						: "";
			}
		}//public String call() throws IOException {
	}

}

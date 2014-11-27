/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package info.modprobe.browserid;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static info.modprobe.browserid.TestUtils.AUDIENCE;
import static info.modprobe.browserid.TestUtils.RESPONSE_FAILURE;
import static info.modprobe.browserid.TestUtils.RESPONSE_MIXED;
import static info.modprobe.browserid.TestUtils.RESPONSE_OKAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import info.modprobe.browserid.BrowserIDResponse.Status;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class VerifierTest {

	private static final int PORT = 8443;
	private static URL VERIFIER_URL;
	private static final String VERIFIER_URL_PATTERN = "https://localhost:%d/verify";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().httpsPort(PORT)
			.keystorePath(fullPathOfKeyStore()));

	@BeforeClass
	public static void setUp() {
		acceptMyCertificate();

		try {
			VERIFIER_URL = new URL(String.format(VERIFIER_URL_PATTERN, PORT));

		} catch (final MalformedURLException exc) {
			throw new RuntimeException(exc);
		}
	}

	@Test
	public void verifyException() {
		stubFor(post(urlEqualTo(VERIFIER_URL.getFile()))
				.willReturn(aResponse().withBody(RESPONSE_MIXED)));

		expectedException.expect(BrowserIDException.class);
		final Verifier verifier = new Verifier(VERIFIER_URL.toString());
		final BrowserIDResponse response = verifier.verify("assertion-verifyException", AUDIENCE);
		assertEquals(Status.FAILURE, response.getStatus());
	}

	@Test
	public void verifyFailure() {
		stubFor(post(urlEqualTo(VERIFIER_URL.getFile()))
				.willReturn(aResponse().withBody(RESPONSE_FAILURE)));

		final Verifier verifier = new Verifier(VERIFIER_URL.toString());
		final BrowserIDResponse response = verifier.verify("assertion-verifyFailure", AUDIENCE);
		assertEquals(Status.FAILURE, response.getStatus());
	}

	@Test
	public void verifyNoAssertion() {
		stubFor(post(urlEqualTo(VERIFIER_URL.getFile()))
				.willReturn(aResponse().withBody(RESPONSE_OKAY)));

		expectedException.expect(IllegalArgumentException.class);
		final Verifier verifier = new Verifier(VERIFIER_URL.toString());
		final BrowserIDResponse response = verifier.verify(null, AUDIENCE);
		assertEquals(Status.FAILURE, response.getStatus());
	}

	@Test
	public void verifyOkay() {
		stubFor(post(urlEqualTo(VERIFIER_URL.getFile()))
				.willReturn(aResponse().withBody(RESPONSE_OKAY)));

		final Verifier verifier = new Verifier(VERIFIER_URL.toString());
		final BrowserIDResponse response = verifier.verify("assertion-verifyOkay", AUDIENCE);
		assertEquals(Status.OK, response.getStatus());
	}

	@Ignore
	@Test
	public void verifyWithTimeOut() {
		final int delay = 1000; //1 second
		stubFor(post(urlEqualTo(VERIFIER_URL.getFile()))
				.willReturn(aResponse()
						.withFixedDelay(delay)
						.withBody(RESPONSE_OKAY)));

		final Verifier verifier = new Verifier(VERIFIER_URL.toString());
		Throwable timeoutExc = null;
		try{
			verifier.verify("assertion-verifyOkay", AUDIENCE, delay/2, TimeUnit.MILLISECONDS );

		}catch (final BrowserIDException exc){
			timeoutExc = exc.getCause();
		}
		assertTrue(timeoutExc instanceof TimeoutException);
	}

	/**
	 * Makes {@link HttpsURLConnection} accepts our certificate.
	 */
	private static void acceptMyCertificate() {
		final char[] JKS_PASSWORD = "persona".toCharArray();
		final char[] KEY_PASSWORD = "password".toCharArray();
		try {
			/* Get the JKS contents */
			final KeyStore keyStore = KeyStore.getInstance("JKS");
			try (final InputStream is = new FileInputStream(fullPathOfKeyStore())) {
				keyStore.load(is, JKS_PASSWORD);
			}
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
			kmf.init(keyStore, KEY_PASSWORD);
			final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
			tmf.init(keyStore);

			/*
			 * Creates a socket factory for HttpsURLConnection using JKS
			 * contents
			 */
			final SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
			final SSLSocketFactory socketFactory = sc.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);

		} catch (final GeneralSecurityException | IOException exc) {
			throw new RuntimeException(exc);
		}
	}

	private static String fullPathOfKeyStore() {
		final String JKS_RESOURCE_PATH = "/test-info-modprobe-browserid.jks";
		final URL url = VerifierTest.class.getResource(JKS_RESOURCE_PATH);
		try {
			final Path path = Paths.get(url.toURI());
			return path.toAbsolutePath().toString();

		} catch (final URISyntaxException exc) {
			throw new RuntimeException(exc);
		}
	}
}

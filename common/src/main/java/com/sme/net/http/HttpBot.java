package com.sme.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class HttpBot {

	static {
		// disable SSL verification
		if (System.getenv("SSL_INSECURE") == "true") {
			disableSslVerification();
		}
	}

	public static HttpResult get(String uri) {

		if (uri == null || uri.equalsIgnoreCase("")) {
			return new HttpResult(0, null, "uri must not be empty");
		}

		HttpURLConnection connection;
		int code;
		try {
			connection = createHttpRequest(uri);
			connection.setRequestMethod("GET");

			code = connection.getResponseCode();
		} catch (Exception e) {
			return new HttpResult(0, null, "Failed to open request; " + e.getMessage());
		}

		return readAll(connection, code);
	}

	public static HttpResult post(String uri, String contentType, String payload) {

		if (uri == null || uri.equalsIgnoreCase("")) {
			return new HttpResult(0, null, "uri must not be empty");
		}
		if (contentType == null || contentType.equalsIgnoreCase("")) {
			contentType = "text/plain";
		}
		if (payload == null) {
			payload = "";
		}

		HttpURLConnection connection;
		int code;
		try {
			connection = createHttpRequest(uri);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Length", String.valueOf(payload.length()));
			connection.setRequestProperty("Content-Type", contentType);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
			writer.write(payload);
			writer.flush();
			writer.close();

			code = connection.getResponseCode();
		} catch (Exception e) {
			return new HttpResult(0, null, "Failed to open request; " + e.getMessage());
		}

		return readAll(connection, code);
	}

	private static HttpURLConnection createHttpRequest(String uri)
			throws IOException, MalformedURLException, ProtocolException {
		HttpURLConnection connection;
		connection = (HttpURLConnection) new URL(uri).openConnection();
		connection.setInstanceFollowRedirects(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		return connection;
	}

	private static HttpResult readAll(HttpURLConnection connection, int status) {
		final int blockSize = 1024;

		try {
			StringBuffer buff = new StringBuffer();
			char[] block = new char[blockSize];
			int len;

			// http://stackoverflow.com/questions/9129766/urlconnection-is-not-allowing-me-to-access-data-on-http-errors-404-500-etc
			InputStream s = (status >= 400 ? connection.getErrorStream() : connection.getInputStream());

			BufferedReader reader = new BufferedReader(new InputStreamReader(s));
			while ((len = reader.read(block)) != -1) {
				buff.append(block, 0, len);
			}
			reader.close();

			return new HttpResult(status, connection.getHeaderFields(), buff.toString());
		} catch (IOException e) {
			return new HttpResult(0, null, "Failed to read response; " + e.getMessage());
		}
	}

	public static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

}

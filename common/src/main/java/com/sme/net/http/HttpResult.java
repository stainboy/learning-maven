package com.sme.net.http;

import java.util.List;
import java.util.Map;

public class HttpResult {
	private int status;
	private String body;
	private Map<String, List<String>> headers;

	public HttpResult(int status, Map<String, List<String>> headers, String body) {
		this.status = status;
		this.headers = headers;
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public String getBody() {
		return body;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}
}
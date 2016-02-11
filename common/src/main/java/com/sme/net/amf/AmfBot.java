package com.sme.net.amf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;

public final class AmfBot {

	public static void registerType(Class<?> type) {
		// register type
		AMFConnection.registerAlias(type.getName(), type.getName());
	}

	public static AmfResponse call(AmfRequest request, String cookie) {
		Map<String, String> httpHeaders = new HashMap<>();
		httpHeaders.put("Cookie", cookie);
		return call(request, httpHeaders);
	}

	public static AmfResponse call(AmfRequest request, Map<String, String> httpHeaders) {

		// Create the AMF connection.
		AMFConnection amf = new AMFConnection();

		try {
			amf.connect(request.getUrl());
		} catch (ClientStatusException e) {
			System.out.println("Failed to open AMF connection to " + request.getUrl());
			e.printStackTrace();
			return null;
		}

		// header
		if (httpHeaders != null) {
			for (Entry<String, String> pair : httpHeaders.entrySet()) {
				amf.addHttpRequestHeader(pair.getKey(), pair.getValue());
			}
		}

		// Make a remoting call and retrieve the result.
		try {
			Object reply = amf.call(request.getEntryPoint(), request.getArgs());
			return new AmfResponse(reply);
		} catch (ClientStatusException e) {
			System.out.println("Failed to call AMF function (client error): " + request.getEntryPoint());
			e.printStackTrace();
		} catch (ServerStatusException e) {
			System.out.println("Failed to call AMF function (server error): " + request.getEntryPoint());
			e.printStackTrace();
		} finally {
			// Close the connection.
			amf.close();
		}

		return null;
	}
}

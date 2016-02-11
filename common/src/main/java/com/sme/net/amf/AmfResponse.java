package com.sme.net.amf;

import flex.messaging.io.amf.ASObject;

public class AmfResponse {

	private Object result;

	public AmfResponse(Object reply) {
		this.result = reply;
	}

	@Override
	public String toString() {
		return "AmfResponse [result=" + result + "]";
	}

	public <T> T as(Class<T> type) {
		return type.cast(result);
	}

	public ASObject raw() {
		return (ASObject) result;
	}
}

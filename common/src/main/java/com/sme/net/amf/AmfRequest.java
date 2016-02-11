package com.sme.net.amf;

import java.util.ArrayList;
import java.util.List;

public class AmfRequest {

	private String url;
	private String entryPoint;
	private List<Object> args = new ArrayList<>();

	public Object[] getArgs() {
		return args.toArray();
	}

	public void pushArg(Object value) {
		args.add(value);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEntryPoint() {
		return entryPoint;
	}

	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}

}

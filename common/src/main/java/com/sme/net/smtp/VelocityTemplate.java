package com.sme.net.smtp;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public final class VelocityTemplate {

	private static final VelocityEngine engine;

	static {
		// template engine
		Properties props = new Properties();
		props.setProperty("resource.loader", "class");
		props.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine = new VelocityEngine(props);
	}

	public static String transform(String view, Map<String, Object> model) {
		Template template = engine.getTemplate(view);
		VelocityContext context = new VelocityContext((Map<?, ?>) model);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

}

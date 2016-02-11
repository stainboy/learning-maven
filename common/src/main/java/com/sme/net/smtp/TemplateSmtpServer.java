package com.sme.net.smtp;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.apache.velocity.exception.ResourceNotFoundException;

public class TemplateSmtpServer {

	private static final String mimeType = "text/html";
	private String smtpServer;

	public TemplateSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public void send(String subject, String from, List<String> to, List<String> cc, String view,
			Map<String, Object> model) {
		try {
			String mailBody = VelocityTemplate.transform(view, model);
			send(subject, from, to, cc, mailBody);
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void send(String subject, String from, List<String> to, List<String> cc, String body) {

		// Get system properties
		Properties props = System.getProperties();

		// Setup mail server
		props.setProperty("mail.smtp.host", smtpServer);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(props);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			for (String one : to) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(one));
			}
			for (String one : cc) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(one));
			}

			// Set Subject: header field
			message.setSubject(subject);

			// Send the actual HTML message, as big as you like
			message.setContent(body, mimeType);

			// Send message
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}

package com.alertaSiebel;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Notify {

	String destinatarios = "elainebrasil@protonmail.com, elainebrasil@gmail.com, gustavo.l.ferreira@gmail.com";
	
	public Notify(String destinatarios) {
		this.destinatarios = destinatarios;
	}

	public void enviaEmail(String msg) {
		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp.mail.yahoo.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("glf_20@yahoo.com.br", "Chorume#033");
			}
		});

		/** Ativa Debug para sessão */
		session.setDebug(true);

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("glf_20@yahoo.com.br")); // Remetente

			Address[] toUser = InternetAddress.parse(this.destinatarios);// Destinatário(s)

			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject("Teste aplicativo SIEBEL WATCHER");// Assunto
			message.setText(msg);
			
			/** Método para enviar a mensagem criada */
			Transport.send(message);

			System.out.println("Feito!!!");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}

package aptech.proj_NN_group2.util;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {
	private static final String FROM_EMAIL = "trungnghia.act@gmail.com";
	private static final String APP_PASSWORD = "snyr uedw yfva uxwj";

	public static boolean sendEmail(String toEmail, String subject, String content) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
				}
			});
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(FROM_EMAIL));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

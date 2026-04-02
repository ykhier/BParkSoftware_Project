package data;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;

/**
 * Utility class for sending emails using Gmail's SMTP server.
 */
public class EmailSender {

	/**
	 * sender address
	 */
	private static final String senderEmail = "brazialaa@gmail.com"; 
	
	/**
	 * App-specific password 
	 */
	private static final String senderPassword = "zprf smod hyhh eypc"; 
	private static Session session;

	// Static block to initialize the email session with SMTP properties
	static {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");                  // Gmail SMTP host
		props.put("mail.smtp.port", "465");                             // SSL port
		props.put("mail.smtp.auth", "true");                            // Enable authentication
		props.put("mail.smtp.socketFactory.port", "465");              // Port for socket factory
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL class
		props.put("mail.smtp.socketFactory.fallback", "false");        // Disable fallback

		// Authenticator for Gmail login
		session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		});
	}

	/**
	 * Sends an HTML-designed email to the recipient.
	 * 
	 * @param recipientEmail the recipient's email address
	 * @param htmlContent    the HTML content of the email
	 * @return true if email is sent successfully, false otherwise
	 */
	public static boolean sendEmailDesigned(String recipientEmail, String htmlContent) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject("Auto BPark"); // Email subject
			message.setContent(htmlContent, "text/html"); // Set HTML content

			Transport.send(message);
			System.out.println("Email sent successfully.1");
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sends an email with a default HTML template showing a red code.
	 * 
	 * @param recipientEmail the recipient's email address
	 * @param content        the code or message to be included in the email
	 * @return true if email is sent successfully, false otherwise
	 */
	public static boolean sendEmail(String recipientEmail, String content) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject("Auto BPark");

			// Build HTML content with parking code inside
			String htmlContent = String.format(
					"""
					<html>
					<body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
					    <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 30px;">
					        <h2 style="color: #004080; text-align: center; margin-bottom: 20px;">Your Parking Code</h2>
					        <p style="font-size: 20px; text-align: center;">
					            Your code is: <span style="color: red; font-weight: bold;">%s</span>
					        </p>
					        <div style="text-align: center; margin-top: 30px;">
					            <img src="https://www.keflatwork.com/wp-content/uploads/2019/01/parking-lot-with-trees.jpg" alt="Parking Lot" style="width: 100%%; max-width: 550px; border-radius: 6px;" />
					        </div>
					        <p style="font-size: 16px; text-align: center; margin-top: 40px;">
					            Best regards,<br/>
					            <strong>Auto BPark</strong>
					        </p>
					    </div>
					</body>
					</html>
					""",
					content);

			message.setContent(htmlContent, "text/html"); // Set email as HTML
			Transport.send(message);
			System.out.println("Email sent successfully.2");
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}
}

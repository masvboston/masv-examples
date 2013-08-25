package com.masvboston.examples.net.javamail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * This class demonstrates how to send email messages using JavaMail. The
 * emphasis is on the more complicated example of sending a multi-part
 * alternative (HTML with text alternative) complete with file attachments. This
 * is an example, so there is minimal checking of input parameters and such.
 * Ensure you use proper coding technique when leveraging this example.
 * 
 * @author mmiller@masvboston.com, http://www.masvboston.com
 * 
 */
public class SimpleJavaMail {

	private final Session fSession;

	public SimpleJavaMail(String aServerHostName) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", aServerHostName);
		fSession = Session.getInstance(props);
		fSession.setDebug(true);
	}

	/**
	 * Demonstrates how to send a simple email message.
	 * 
	 * @param aToAddress
	 *            The email address to send to
	 * @param aFromAddress
	 *            The email address sending the email
	 * @param aMsg
	 *            The text message.
	 * @throws MessagingException
	 */
	public void sendSimpleEmail(String aToAddress, String aFromAddress,
			String aMsg) throws MessagingException {

		Address to = new InternetAddress(aToAddress);
		Address[] from = { new InternetAddress(aFromAddress) };

		MimeMessage email = new MimeMessage(fSession);
		email.addFrom(from);
		email.addRecipient(RecipientType.TO, to);
		email.setSentDate(new Date());
		email.setText(aMsg);

		Transport.send(email);

	}

	/**
	 * Demonstrates how to send a simple HTML only email.
	 * 
	 * @param aToAddress
	 *            The email address to send to
	 * @param aFromAddress
	 *            The email address sending the email
	 * @param aHtmlMsg
	 *            The HTML message text.
	 * @throws MessagingException
	 */
	public void sendHtmlEmailOnly(String aToAddress, String aFromAddress,
			String aHtmlMsg) throws MessagingException {

		Address to = new InternetAddress(aToAddress);
		Address[] from = { new InternetAddress(aFromAddress) };

		MimeMessage email = new MimeMessage(fSession);
		email.addFrom(from);
		email.addRecipient(RecipientType.TO, to);
		email.setSentDate(new Date());

		email.setContent(aHtmlMsg, "text/html");

		Transport.send(email);
	}

	/**
	 * Demonstrates how to send a message that has both an HTML and a plain text
	 * version of the HTML message in it.
	 * 
	 * @param aToAddress
	 *            The email address to send to
	 * @param aFromAddress
	 *            The email address sending the email
	 * @param aMsg
	 *            The plain test version of the message.
	 * @param aHtmlMsg
	 *            The HTML version of the message.
	 * @throws MessagingException
	 */
	public void sendAlternateEmail(String aToAddress, String aFromAddress,
			String aMsg, String aHtmlMsg) throws MessagingException {
		Address to = new InternetAddress(aToAddress);
		Address[] from = { new InternetAddress(aFromAddress) };

		/*
		 * Setup the message body as before.
		 */
		MimeMessage email = new MimeMessage(fSession);
		email.addFrom(from);
		email.addRecipient(RecipientType.TO, to);
		email.setSentDate(new Date());

		/*
		 * Instead of just setting the text you have to create a multi-part and
		 * add the text and HTML, IN THAT ORDER, as separate body parts to the
		 * multi-part.
		 */
		MimeMultipart mp = new MimeMultipart("alternative");

		/*
		 * Setup the plain text body part.
		 */
		MimeBodyPart bp = new MimeBodyPart();
		bp.setText(aMsg);

		/*
		 * Add it to the multi-part
		 */
		mp.addBodyPart(bp);

		/*
		 * Setup the HTML body part.
		 */
		bp = new MimeBodyPart();
		/*
		 * Note the use of setContent here, not setText.
		 */
		bp.setContent(aHtmlMsg, "text/html");

		/*
		 * Add it to the multi-part.
		 */
		mp.addBodyPart(bp);

		/*
		 * Finally add the multi-part to the message as it's onlyh content.
		 */
		email.setContent(mp);

		Transport.send(email);
	}

	/**
	 * Demonstrates how to send a message that has both an HTML and a plain text
	 * version of the HTML message in it. Several key things to note: <br/>
	 * <ul>
	 * <li>Requires you to create nested multi-parts.
	 * <ol>
	 * <li>The parent for both the message parts and the file attachment parts.</li>
	 * <li>The multi-part that holds both text and html message parts.</li>
	 * </ol>
	 * </li>
	 * <li>The message multi-part must be "wrapped" in a MimeBodyPart in order
	 * to add it to the parent multi-part.</li>
	 * <li>The routine I demonstrate allows you to use any stream as the source
	 * of the attachment, but if you're going to use files from the file system
	 * the code can be easier by using
	 * {@link MimeBodyPart#attachFile(java.io.File)} method instead of the data
	 * handler technique I demonstrate here.</li>
	 * </ul>
	 * 
	 * 
	 * 
	 * @param aToAddress
	 *            The email address to send to
	 * @param aFromAddress
	 *            The email address sending the email
	 * @param aMsg
	 *            The plain test version of the message.
	 * @param aHtmlMsg
	 *            The HTML version of the message.
	 * @param aAttachments
	 *            A map of file names and their data streams.
	 * @throws MessagingException
	 * @throws IOException
	 *             Error while working with the file streams.
	 */
	public void sendAlternateEmailWithAttachments(String aToAddress,
			String aFromAddress, String aMsg, String aHtmlMsg,
			Map<String, InputStream> aAttachments) throws MessagingException,
			IOException {
		Address to = new InternetAddress(aToAddress);
		Address[] from = { new InternetAddress(aFromAddress) };

		/*
		 * Setup the message body as before.
		 */
		MimeMessage email = new MimeMessage(fSession);
		email.addFrom(from);
		email.addRecipient(RecipientType.TO, to);
		email.setSentDate(new Date());

		/*
		 * Instead of just setting the text you have to create a multi-part and
		 * add the text and HTML, IN THAT ORDER, as separate body parts to the
		 * multi-part.
		 */
		MimeMultipart mp = new MimeMultipart("alternative");

		/*
		 * Setup the plain text body part.
		 */
		MimeBodyPart bp = new MimeBodyPart();
		bp.setText(aMsg);

		/*
		 * Add it to the multi-part
		 */
		mp.addBodyPart(bp);

		/*
		 * Setup the HTML body part.
		 */
		bp = new MimeBodyPart();
		/*
		 * Note the use of setContent here, not setText.
		 */
		bp.setContent(aHtmlMsg, "text/html");

		/*
		 * Add it to the multi-part.
		 */
		mp.addBodyPart(bp);

		/*
		 * We have to create anew mime body part to hold the multi-part that is
		 * holds the message alternates.
		 */
		bp = new MimeBodyPart();
		/*
		 * Add the messages to the body part.
		 */
		bp.setContent(mp);

		/**
		 * Now create a new multi-part that will be the parent of the message
		 * body part and will also hold all the attachments.
		 */
		mp = new MimeMultipart();
		mp.addBodyPart(bp);

		/*
		 * Cycle through each attachment and add it to the message.
		 */

		DataHandler dh = null;
		ByteArrayDataSource bds = null;

		for (Entry<String, InputStream> es : aAttachments.entrySet()) {

			/*
			 * Create a body part for each attachment.
			 */
			bp = new MimeBodyPart();
			bp.setFileName(es.getKey());

			/*
			 * The disposition is a hint to the email client that this data
			 * stream should be preferably treated as a file attachment.
			 */
			bp.setDisposition(Part.ATTACHMENT);

			/*
			 * To get the stream into the email you need to use this handy
			 * build-in datasource that can read both byte arrays and input
			 * streams. then pass it to an instance of a data handler.
			 */
			bds = new ByteArrayDataSource(es.getValue(),
					"application/octet-stream");
			dh = new DataHandler(bds);
			bp.setDataHandler(dh);

			/*
			 * Add the part to the email.
			 */
			mp.addBodyPart(bp);
		}

		email.setContent(mp);

		/*
		 * Making sure the changes are saved never huts. Transport.send(..) does
		 * it for you but it doesn't hurt to call it anyway, and if you should
		 * dispense with Transport.send and instead use
		 * Transport.sendMessage(..) it's a good habit to save changes first.
		 */
		email.saveChanges();

		Transport.send(email);
	}

}

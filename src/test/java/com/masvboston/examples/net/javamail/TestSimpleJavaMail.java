package com.masvboston.examples.net.javamail;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Tests the JavaMail example code using the greenmail email test server.
 * 
 * @author mmiller@masvboston.com, http://www.masvboston.com
 * 
 */
public class TestSimpleJavaMail {

	private static final String MSG_TEXT_PLAIN = "This is a test email";
	private static final String MSG_HTML = "<HTML><HEAD><TITLE>HTML TEST</TITLE></HEAD><BODY>HTML MESSAGE</BODY></HTML>";
	private GreenMail greenMail;

	@Before
	public void setUp() throws Exception {
		greenMail = new GreenMail(ServerSetup.SMTP); // uses test ports by
														// default
		greenMail.start();
	}

	@After
	public void tearDown() throws Exception {
		greenMail.stop();
	}

	@Test
	public void testGreenMailSetup() {
		GreenMailUtil.sendTextEmail("to@localhost.com", "from@localhost.com",
				"subject", "body", ServerSetup.SMTP); // replace this with
		// your send code
		assertEquals("body",
				GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]));
	}

	@Test
	public void testSendSimpleEmail() throws MessagingException {

		SimpleJavaMail sjm = new SimpleJavaMail("localhost");
		sjm.sendSimpleEmail("mmiller@masvbston.com", "greenmailtest@test.com",
				MSG_TEXT_PLAIN);

		assertEquals(MSG_TEXT_PLAIN,
				GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]));
	}

	@Test
	public void testsendHtmlEmailOnly() throws MessagingException {

		SimpleJavaMail sjm = new SimpleJavaMail("localhost");
		sjm.sendHtmlEmailOnly("mmiller@masvbston.com",
				"greenmailtest@test.com", MSG_HTML);

		assertEquals(MSG_HTML,
				GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]));
	}

	@Test
	public void testsendAlternateEmail() throws MessagingException, IOException {

		SimpleJavaMail sjm = new SimpleJavaMail("localhost");
		sjm.sendAlternateEmail("mmiller@masvbston.com",
				"greenmailtest@test.com", MSG_TEXT_PLAIN, MSG_HTML);

		MimeMessage[] msgs = greenMail.getReceivedMessages();

		assertEquals("Unexpected number of messages", 1, msgs.length);

		MimeMultipart mp = (MimeMultipart) msgs[0].getContent();

		assertEquals("Not enough parts", 2, mp.getCount());

		String txt = (String) mp.getBodyPart(0).getContent();
		String html = (String) mp.getBodyPart(1).getContent();

		assertEquals(MSG_TEXT_PLAIN, txt);
		assertEquals(MSG_HTML, html);
	}

	@Test
	public void testsendAlternateEmailAttachments() throws MessagingException,
			IOException {

		SimpleJavaMail sjm = new SimpleJavaMail("localhost");

		ByteArrayInputStream txtStream = new ByteArrayInputStream(
				MSG_TEXT_PLAIN.getBytes());
		ByteArrayInputStream htmlStream = new ByteArrayInputStream(
				MSG_HTML.getBytes());

		HashMap<String, InputStream> hm = new HashMap<String, InputStream>();
		hm.put("fileone.txt", txtStream);
		hm.put("filetwo.html", htmlStream);

		sjm.sendAlternateEmailWithAttachments("mmiller@masvbston.com",
				"greenmailtest@test.com", MSG_TEXT_PLAIN, MSG_HTML, hm);

		MimeMessage[] msgs = greenMail.getReceivedMessages();

		assertEquals("Unexpected number of messages", 1, msgs.length);

		MimeMultipart mp = (MimeMultipart) msgs[0].getContent();

		assertEquals("Not enough parts", 3, mp.getCount());

		MimeMultipart mp2 = (MimeMultipart) mp.getBodyPart(0).getContent();
		assertEquals("Not enough message parts", 2, mp2.getCount());

		String txt = (String) mp2.getBodyPart(0).getContent();
		String html = (String) mp2.getBodyPart(1).getContent();

		assertEquals(MSG_TEXT_PLAIN, txt);
		assertEquals(MSG_HTML, html);

		BodyPart bp = mp.getBodyPart(1);
		InputStream is = bp.getInputStream();
		String t = IOUtils.toString(is);
		System.out.println(t);

		assertEquals("Text messages are not equal", MSG_TEXT_PLAIN, t);

		bp = mp.getBodyPart(2);
		is = bp.getInputStream();
		t = IOUtils.toString(is);

		assertEquals("HTML streams not equal", MSG_HTML, t);

	}

}

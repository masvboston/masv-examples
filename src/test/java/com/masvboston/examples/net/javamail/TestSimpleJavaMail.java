package com.masvboston.examples.net.javamail;

import static org.junit.Assert.assertEquals;

import javax.mail.MessagingException;

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

}

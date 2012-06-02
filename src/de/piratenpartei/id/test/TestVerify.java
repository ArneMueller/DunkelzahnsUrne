package de.piratenpartei.id.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.piratenpartei.id.Account;
import de.piratenpartei.id.KeyException;
import de.piratenpartei.id.Message;
import de.piratenpartei.id.VerificationException;

public class TestVerify {

	@Test
	public void testWrongKey() {
		try {
			Account a = new Account("_5KATXMLH6DJWJCENL57PK4GWAPP5R3JUXAGF64ECO5N6GZ6R25XK7BZKFPNE55CZ6RDRBXK2K34XK7S4OBX42KFGNXNYTAF6CGMAPOI_");
			fail("must not succed, since key does not fit to hash!");
		}
		catch(KeyException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void testCorrectKey() throws KeyException {
		Account a = new Account("_RLVQKMQRBSAXVGJASV6EXO2BVRX4N7HIULYJPTJL7SUE4PYO7QHSGRUYNCRDXI55H2PA5SGE37RXCJMSDTZMAOGGNTXQYZR6BMQ2BVQ_");
		assertTrue(a.isPublished());
		assertFalse(a.isLegitimized());
	}


	@Test
	public void testMessage() throws KeyException, FileNotFoundException, IOException, SAXException {
		Message m = new Message(new FileInputStream("test_files/message.xml"));
		try {
			m.verify();
		} catch (VerificationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertTrue(m.getAuthor().isLegitimized());
		assertTrue(m.getAuthor().isPublished());
	}
}
package de.piratenpartei.id;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;

import org.apache.xml.security.Init;

public class Config {

	public static final URL publishedAccounts = loadPublishedAccountsURL();
	public static final URL publishedAccountsSchema = loadPublishedAccountsSchemaURL();
	public static final URL legitimatedAccounts = loadLegitimatedAccountsURL();
	public static final URL legitimatedAccountsSchema = loadLegitimatedAccountsSchemaURL();
	public static final URL messageSchema = loadMessageSchemaURL();
	
	public static final String messageNamespace = "http://id.piratenpartei.de/message";
	public static final String accountsNamespace = "http://id.piratenpartei.de/accounts";
	public static final String legitimateNamespace = "http://id.piratenpartei.de/legitimate";
	
	public static final String legitimateChecksum = "8+2i9WyfduELKgVS99wgkCtdl5Asjjq7+4v2ghJsiou9Z7SPv83kCvNES+wOBRZAuFh2stAYLJct5Sw7PKEfpw==";
	
	public static final String HASH_ALGORITHM = "SHA-512";
	public static final String MESSAGE_DIGEST_ALGORITHM = DigestMethod.SHA512;
	public static final String SIGNATURE_METHOD = SignatureMethod.DSA_SHA1;
	public static final String CANONICALIZATION_METHOD = CanonicalizationMethod.INCLUSIVE;
	public static final String KEY_TYPE = "DSA";

	
	static {
		Init.init();
	}
	
	private static URL loadPublishedAccountsURL() {
		try {
			return new URL("file:test_files/registered.xml");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static URL loadPublishedAccountsSchemaURL() {
		try {
			return new URL("file:xsd/registered_local.xsd");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static URL loadLegitimatedAccountsURL() {
		try {
			return new URL("file:test_files/legitimated.xml");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static URL loadLegitimatedAccountsSchemaURL() {
		try {
			return new URL("file:xsd/legitimated.xsd");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static URL loadMessageSchemaURL() {
		try {
			return new URL("file:xsd/message_local.xsd");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

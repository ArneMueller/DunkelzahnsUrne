package de.piratenpartei.id;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;

public class Config {

	public static final URL publishedAccounts = loadPublishedAccountsURL();
	public static final URL publishedAccountsSchema = loadPublishedAccountsSchemaURL();
	public static final URL legitimatedAccounts = loadLegitimatedAccountsURL();
	public static final URL legitimatedAccountsSchema = loadLegitimatedAccountsSchemaURL();
	
	public static final String messageNamespace = "http://id.piratenpartei.de/message";
	public static final String accountsNamespace = "http://id.piratenpartei.de/accounts";
	public static final String legitimateNamespace = "http://id.piratenpartei.de/legitimate";
	
	public static final String legitimateChecksum = "wamy/5CAmjBgx1Qrslrk3XqGEzGjFel4AFmPgmvbVZiqGaqCXzPrbqsrPO4ce6SO23cNfBJMoNCHAJ8b+R1mfQ==";
	
	public static final String HASH_ALGORITHM = "SHA-512";
	public static final String MESSAGE_DIGEST_ALGORITHM = DigestMethod.SHA512;
	public static final String SIGNATURE_METHOD = SignatureMethod.DSA_SHA1;
	public static final String CANONICALIZATION_METHOD = CanonicalizationMethod.INCLUSIVE;
	public static final String KEY_TYPE = "DSA";

	
	private static URL loadPublishedAccountsURL() {
		try {
			return new URL("file:test_files/registered.xml");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static URL loadPublishedAccountsSchemaURL() {
		try {
			return new URL("file:test_files/registered.xsd");
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
			return new URL("file:test_files/legitimated.xsd");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

package de.piratenpartei.id;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Provider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Config stores all those configuration parameters that are somehow chosen arbitrarily.
 * 
 * Currently, this file is a mess, since it also contains information that should be configurable, like the URLs of resource files.
 * 
 * @author arne
 *
 */
public class Config {

	public static final URL publishedAccounts = loadPublishedAccountsURL();
	public static final URL verifiedAccounts = loadVerifiedAccountsURL();
	public static final URL publishAccount = loadPublishAccountURL();
	
	public static final String messageNamespace = "http://id.piratenpartei.de/message";
	public static final String accountsNamespace = "http://id.piratenpartei.de/accounts";
	public static final String legitimateNamespace = "http://id.piratenpartei.de/legitimate";
	
	public static final String legitimateChecksum = "8+2i9WyfduELKgVS99wgkCtdl5Asjjq7+4v2ghJsiou9Z7SPv83kCvNES+wOBRZAuFh2stAYLJct5Sw7PKEfpw==";
	
	public static final String accountServer = "hpO/rM/QjX07fGibrUbGtqr7+OcRFda5jaCn2xLpqdt4WmUYphmS/P4ON3GNofjh8fWpOKe+nxDD/zgDC3X6rQ==";
	
	public static final String HASH_ALGORITHM = "SHA-512";
	public static final String SIGNATURE_ALGORITHM = "RSA/NONE/PKCS1Padding";
	
	public static final String CHARSET = "UTF8";
	
	private static final Provider provider = new BouncyCastleProvider();
	
	public static Provider getProvider() {
		return provider;
	}
	
	private static URL loadPublishedAccountsURL() {
		try {
			return new URL("file:test_files/published");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static URL loadVerifiedAccountsURL() {
		try {
			return new URL("file:test_files/verified");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static URL loadPublishAccountURL() {
		try {
			return new URL("file:test_files/publish");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

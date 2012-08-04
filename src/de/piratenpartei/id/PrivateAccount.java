package de.piratenpartei.id;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.Date;

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;

import org.bouncycastle.x509.X509V1CertificateGenerator;

public class PrivateAccount extends Account {
	
	KeyPair keys;
	
	/**
	 * creates a new private Account.
	 */
	public PrivateAccount() throws KeyException {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		kpg.initialize(4096);
		keys = kpg.generateKeyPair();
		init(keys.getPublic());
	}
	
	/**
	 * Loads a previously created PrivateAccount from the KeyStore.
	 * @param password
	 * @throws KeyException 
	 */
	public PrivateAccount(String keyId, char[] password) throws KeyException {
		try {
			KeyStore ks = KeyStore.getInstance(Config.getKeyStoreType(), Config.getProvider());
			ks.load(new FileInputStream(Config.getKeyStore()), password);
			PublicKey publicKey = (PublicKey) ks.getKey(keyId+Config.ACCOUNT_ALIAS_PUBLIC, password);
			PrivateKey privateKey = (PrivateKey) ks.getKey(keyId+Config.ACCOUNT_ALIAS_PRIVATE, password);
			
			if(publicKey == null || privateKey == null) throw new KeyException("Key with id \""+ keyId+ "\" does not exist");
			
			keys = new KeyPair(publicKey, privateKey);
		} catch (UnrecoverableKeyException e) {
			throw new KeyException("Cannot read Key from KeyStore", e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new KeyException("Cannot open KeyStore", e);
		} catch (IOException e) {
			throw new KeyException("Cannot open KeyStore", e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the private key of this account.
	 * Usually used to sign messages.
	 * @see Message#send(PrintWriter)
	 * @return the private key
	 */
	public PrivateKey getPrivateKey() {
		return keys.getPrivate();
	}
	
	/**
	 * stores the keys of this account.
	 * @param password
	 * @throws KeyException 
	 */
	@SuppressWarnings("deprecation")
	public void store(String keyId, char[] password) throws KeyException {
		try {
			KeyStore ks = KeyStore.getInstance(Config.getKeyStoreType(), Config.getProvider());
			ks.load(new FileInputStream(Config.getKeyStore()), password);
			ks.setKeyEntry(keyId+Config.ACCOUNT_ALIAS_PUBLIC, keys.getPublic(), password, null);
			
			/*
			 * We need a stupid certificate to store the key, so just create a self-signed one.
			 */
			Date startDate = Date.valueOf("2000-01-01");              // time from which certificate is valid
			Date expiryDate = Date.valueOf("3000-01-01");             // time after which certificate is not valid
			BigInteger serialNumber = new BigInteger("42");     // serial number for certificate

			// method is deprecated, for production use, we may need to include the CertificateBuilder of the full bouncy-castle lib
			X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
			X500Principal              dnName = new X500Principal("CN=Stupid CA Certificate");

			certGen.setSerialNumber(serialNumber);
			certGen.setIssuerDN(dnName);
			certGen.setNotBefore(startDate);
			certGen.setNotAfter(expiryDate);
			certGen.setSubjectDN(dnName);                       // note: same as issuer
			certGen.setPublicKey(keys.getPublic());
			certGen.setSignatureAlgorithm("MD2withRSA");

			X509Certificate cert = null;
			try {
				cert = certGen.generate(keys.getPrivate(), "BC");
			} catch (InvalidKeyException e) {
				throw new RuntimeException(e);
			} catch (IllegalStateException e) {
				throw new RuntimeException(e);
			} catch (NoSuchProviderException e) {
				throw new RuntimeException(e);
			} catch (SignatureException e) {
				throw new RuntimeException(e);
			}
			
			Certificate[] certs = {cert};
			
			ks.setKeyEntry(keyId+Config.ACCOUNT_ALIAS_PRIVATE, keys.getPrivate(), password, certs);
			ks.store(new FileOutputStream(Config.getKeyStore()), password);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new KeyException("Cannot write KeyStore to store the key", e);
		} catch (IOException e) {
			throw new KeyException("Cannot write KeyStore to store the key", e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * publishes the public key, i.e. registers the Account.
	 */
	public void publish() throws KeyException, IOException {
		Message m = new Message(this);
		StringBuilder builder = new StringBuilder();
		builder.append("Publish Account\n");
		builder.append("Hash: ");
		builder.append(Helper.computeHash(getPublicKey()));
		builder.append("\n");
		Helper.writePublicKey(builder, getPublicKey());
		m.setMessage(builder.toString());
		
		/*
		URLConnection conn = Config.publishAccount.openConnection();
		OutputStream out = conn.getOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, Config.CHARSET));
		m.send(pw);
		*/
		PrintWriter pw = new PrintWriter(System.out);
		m.send(pw); // dirty hack for testing
		pw.flush();
	}
}

package de.piratenpartei.id;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class Helper {

	public static PublicKey readPublicKey(String modulus, String exponent) throws IllegalFormatException {
		RSAPublicKeySpec spec;
		try {
			byte[] enc_modulus = Base64.decodeBase64(modulus);
			byte[] enc_exponent = Base64.decodeBase64(exponent);
			BigInteger int_modulus = new BigInteger(enc_modulus);
			BigInteger int_exponent = new BigInteger(enc_exponent);
			if(int_modulus.compareTo(BigInteger.ZERO) <= 0) throw new IllegalFormatException("Modulus must be positive");
			if(int_exponent.compareTo(BigInteger.ZERO) <= 0) throw new IllegalFormatException("Exponent must be positive");
			spec = new RSAPublicKeySpec(int_modulus, int_exponent);
		} catch (RuntimeException e1) {
			throw new IllegalFormatException("Failed to parse public key");
		}
		
		KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("RSA", Config.getProvider()); // TODO: specify security provider
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No security provider for RSA algorithm", e);
		} 
		
		PublicKey pk;
		try {
			pk = fact.generatePublic(spec);
		} catch (InvalidKeySpecException e) {
			throw new IllegalFormatException("Unable to generate public key");
		}
		
		return pk;
	}
	
	public static void writePublicKey(StringBuilder builder, PublicKey pk) throws KeyException {
		if(!(pk instanceof RSAPublicKey)) throw new KeyException("Key is not a RSAPublicKey");
		RSAPublicKey pub = (RSAPublicKey) pk;
		builder.append("Modulus: ");
		String modulus = Base64.encodeBase64String(pub.getModulus().toByteArray());
		builder.append(modulus);
		builder.append("\n");
		builder.append("Exponent: ");
		String exponent = Base64.encodeBase64String(pub.getPublicExponent().toByteArray());
		builder.append(exponent);
		builder.append("\n");
	}
	
	public static String computeHash(PublicKey pk) throws KeyException {
		if(!(pk instanceof RSAPublicKey)) throw new KeyException("Key is not a RSAPublicKey");
		RSAPublicKey pub = (RSAPublicKey) pk;
		
		// setup MessageDigest algorithm to compute the hash
		MessageDigest d;
		try {
			d = MessageDigest.getInstance(Config.HASH_ALGORITHM, Config.getProvider());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		d.update(pub.getModulus().toByteArray());
		byte[] val = d.digest(pub.getPublicExponent().toByteArray());
		
		String encoded = Base64.encodeBase64String(val);
		return encoded;
	}
	
	public static String computeDigest(String text) {
		MessageDigest d;
		try {
			d = MessageDigest.getInstance(Config.HASH_ALGORITHM, Config.getProvider());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] val;
		try {
			val = d.digest(text.getBytes(Config.CHARSET));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		String encoded = Base64.encodeBase64String(val);
		return encoded;
	}
	
	
	public static String computeSignature(String digest, PublicKey pk) throws KeyException {
		if(!(pk instanceof RSAPublicKey)) throw new KeyException("Key is not a RSAPublicKey");
		byte[] val;
		try {
			Cipher c = Cipher.getInstance(Config.SIGNATURE_ALGORITHM, Config.getProvider());
			c.init(Cipher.ENCRYPT_MODE, pk);
			val = c.doFinal(digest.getBytes(Config.CHARSET));
		} catch (InvalidKeyException e) {
			throw new KeyException("Key is not a valid Key", e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		String encoded = Base64.encodeBase64String(val);
		return encoded;
	}

	
	public static void verifyKey(PublicKey pk, String hash) throws KeyException {
		String encoded = computeHash(pk);
		if(!encoded.equals(hash)) throw new KeyException("Key does not match to hash: "+hash); 
	}
	
	/**
	 * Reads a line of a input file.
	 * The line must have the format: <code>[identifier]:[value]</code>
	 * The identifier must not contain any colons.
	 * The value must not contain any colons.
	 * If it does not have this format, an IllegalFormatException is thrown.
	 * Whitespace around value is trimmed away.
	 * 
	 * @param identifier the identifier with which the line has to start
	 * @param text the text of the complete line
	 * @return the value part of the line (the text following the colon)
	 * @throws IllegalFormatException
	 */
	public static String read(String identifier, String text) throws IllegalFormatException {
		if(identifier.contains(":")) throw new IllegalArgumentException("Identifier must contain no colon!");
		if(!text.startsWith(identifier+":")) throw new IllegalFormatException("Expected \""+identifier+":\"");
		String[] textSplit = text.split(":");
		if(textSplit.length != 2) throw new IllegalFormatException("String after \""+identifier+":\" must contain no \":\"");
		return textSplit[1].trim();
	}
}

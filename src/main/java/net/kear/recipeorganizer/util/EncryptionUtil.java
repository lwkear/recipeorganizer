package net.kear.recipeorganizer.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

	private final static String PASSWORD = "w8yE@$*zGJ";
	private final static byte[] SALT = "3nGi9q3-58x".getBytes();
	private final static int ITERATIONS = 31;
	private final static int KEYLENGTH = 128;
	private Cipher encryptCipher = null;
	private Cipher decryptCipher = null;
	
	public EncryptionUtil() {
		SecretKeyFactory factory = null;
		SecretKey tmp = null;
		SecretKeySpec key = null;
		
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			tmp = factory.generateSecret(new PBEKeySpec(PASSWORD.toCharArray(), SALT, ITERATIONS, KEYLENGTH));
			key = new SecretKeySpec(tmp.getEncoded(), "AES");
			encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
		
	public String encryptURLParam(String param) {
		byte[] cipherbytes = null;
		try {
			cipherbytes = encryptCipher.doFinal(param.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return Base64.encodeBase64URLSafeString(cipherbytes);
	}
	
	public String decryptURLParam(String param) {
		String result = "";
		byte[] cipherbytes = Base64.decodeBase64(param.getBytes());
		try {
			result = new String(decryptCipher.doFinal(cipherbytes));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return result;
	}
}

package dtu.compute.util;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.mindrot.jbcrypt.BCrypt;

public class Crypto {
	public static String salt(String key) {
		return BCrypt.hashpw(key, BCrypt.gensalt());
	}

	public static boolean compare(String userHash, String dbHash) {
		try {
			return BCrypt.checkpw(userHash, dbHash) || userHash == null;
		} catch (Exception e) {
			return false;
		}
	}

	public static String hash(final String pw) {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(pw.toCharArray(), pw.getBytes(), 1000, 256);
			return Hex.encodeHexString(skf.generateSecret(spec).getEncoded());

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}
}

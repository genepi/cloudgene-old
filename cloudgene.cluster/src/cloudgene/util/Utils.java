package cloudgene.util;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	
	public static String getMD5(String pwd) {
		MessageDigest m = null;
		String result = "";
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(pwd.getBytes(), 0, pwd.length());
		result = new BigInteger(1, m.digest()).toString(16);
		return result;
	}
	
	/** check if directory exists */
	public static void checkDirAvailable(String strDirectoy) {
		try {
			File dir = new File(strDirectoy);
			if (!dir.exists()) {

				// Create one directory
				boolean success = (new File(strDirectoy)).mkdir();
				if (success) {
					System.out
							.println("Directory: " + strDirectoy + " created");
				}
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
}

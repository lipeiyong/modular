package com.lpy.common.util.encryp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

/**
 * @author lipeiyong
 */
public class BigFileMD5 {

	static MessageDigest MD5 = null;

	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ne) {

		}
	}

	private static final String TAG = "BigFileMD5";
	/**
	 * 对一个文件获取md5值
	 * 
	 * @return md5串
	 */
	public static String getMD5(File file) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}

			
			return new String(Hex.encodeHex(MD5.digest()));
		} catch (IOException e) {
			Timber.e(e, "Error ");
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException ignored) {

			}
		}
	}
 
}

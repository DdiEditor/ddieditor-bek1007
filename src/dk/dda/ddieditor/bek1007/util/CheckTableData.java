package dk.dda.ddieditor.bek1007.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckTableData {
	public static String getContent(File f, int position, int offset, int length)
			throws IOException {
		StringBuffer result = new StringBuffer();

		Reader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(f), "UTF-8"));
		try {
			int count = position - offset;
			r.skip(count);
			int stop = position + length;

			int intch;
			while ((intch = r.read()) != -1) {
				result.append(Character.toString((char) intch));
				count++;
				if (count > stop) {
					break;
				}
			}
		} finally {
			r.close();
		}
		return result.toString();
	}

	/**
	 * Compute MD5 in conformance with IETFRFC1321 - The MD5 Message-Digest
	 * Algorithm (128 bit (16 bytes))
	 * 
	 * @param file
	 *            to check
	 * @return md5 checksum
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static byte[] md5file(File file) throws NoSuchAlgorithmException,
			IOException {
		MessageDigest md = MessageDigest.getInstance("md5");

		FileChannel channel = new FileInputStream(file).getChannel();
		try {
			ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					file.length());
			md.update(buffer);
		} finally {
			channel.close();
		}

		return md.digest();
	}

	public static String byteArrayToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(2 * bytes.length);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
}

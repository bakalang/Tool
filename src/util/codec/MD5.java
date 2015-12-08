package util.codec;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import util.ByteUtils;

public class MD5
{
	private static MessageDigest md5Instance;

	static
	{
		try
		{
			md5Instance = MessageDigest.getInstance("MD5");
			md5Instance.clone(); // test cloneability
		}
		catch ( NoSuchAlgorithmException e )
		{
			throw new RuntimeException(e);
		}
		catch ( CloneNotSupportedException e )
		{
			throw new RuntimeException(e);
		}
	}
	public static byte[] digest(byte[] bytes)
	{
		try
		{
			MessageDigest md5 = (MessageDigest)md5Instance.clone();
			return md5.digest(bytes);
		}
		catch ( CloneNotSupportedException e )
		{
			throw new RuntimeException(e);
		}
	}
	public static String getMD5(byte[] bytes)
	{
		try
		{
			MessageDigest md5 = (MessageDigest)md5Instance.clone();
			return ByteUtils.getHexString(md5.digest(bytes));
		}
		catch ( CloneNotSupportedException e )
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculate MD5 of the given string with utf-8 encoding
	 * @param msg
	 * @return 
	 */
	public static String getMD5(String msg)
	{
		try
		{
			return getMD5(msg, "utf-8");
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new RuntimeException(e);
		}
	}

	public static String getMD5(String msg, String encoding) throws UnsupportedEncodingException
	{
		return getMD5(msg.getBytes(encoding));
	}
}

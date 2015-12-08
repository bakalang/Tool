package util.codec;

import java.io.UnsupportedEncodingException;

import util.codec.AESCipher;
import util.codec.CryptoException;

public class AESB64
{
	public static byte[] decode(byte[] sArr, String key) throws CryptoException
	{
		byte[] byRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = Base64.decode(sArr);
		if ( byArr != null )
			byRet = cipher.decrypt(byArr);
		return byRet;
	}
	
	public static byte[] decode(String sArr, String key) throws CryptoException
	{
		byte[] byRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = Base64.decode(sArr);
		if ( byArr != null )
			byRet = cipher.decrypt(byArr);

		return byRet;
	}
	
	public static String decodeToString(byte[] sArr, String key) throws CryptoException
	{
		String sRet = null;
		byte[] byArr = decode(sArr, key);
		if ( byArr != null )
			sRet = new String(byArr);
		return sRet;
	}
	
	public static String decodeToString(byte[] sArr, String key, String charset) throws CryptoException, UnsupportedEncodingException
	{
		String sRet = null;
		byte[] byArr = decode(sArr, key);
		if ( byArr != null )
			sRet = new String(byArr, charset);
		return sRet;
	}
	
	public static String decodeToString(String sArr, String key) throws CryptoException
	{
		String sRet = null;
		byte[] byArr = decode(sArr, key);
		if ( byArr != null )
			sRet = new String(byArr);
		return sRet;
	}
	
	public static String decodeToString(String sArr, String key, String charset) throws CryptoException, UnsupportedEncodingException
	{
		String sRet = null;
		byte[] byArr = decode(sArr, key);
		if ( byArr != null )
			sRet = new String(byArr, charset);
		return sRet;
	}
	
	public static byte[] encode(byte[] sArr, String key) throws CryptoException
	{
		byte[] byRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = cipher.encrypt(sArr);
		if ( byArr != null )
			byRet = Base64.encodeToByte(byArr);
		return byRet;
	}
	
	public static byte[] encode(String sArr, String key) throws CryptoException
	{
		byte[] byRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = cipher.encrypt(sArr.getBytes());
		if ( byArr != null )
			byRet = Base64.encodeToByte(byArr);
		return byRet;
	}
	
	public static byte[] encode(String sArr, String key, String charset) throws CryptoException, UnsupportedEncodingException
	{
		byte[] byRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = cipher.encrypt(sArr.getBytes(charset));
		if ( byArr != null )
			byRet = Base64.encodeToByte(byArr);
		return byRet;
	}
	
	public static String encodeToString(byte[] sArr, String key) throws CryptoException
	{
		String sRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = cipher.encrypt(sArr);
		if ( byArr != null )
			sRet = Base64.encodeToString(byArr);
		return sRet;
	}
			
	public static String encodeToString(String sArr, String key) throws CryptoException
	{
		String sRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = cipher.encrypt(sArr.getBytes());
		if ( byArr != null )
			sRet = Base64.encodeToString(byArr);
		return sRet;
	}
	
	public static String encodeToString(String sArr, String key, String charset) throws CryptoException, UnsupportedEncodingException
	{
		String sRet = null;
		AESCipher cipher = AESCipher.getInstance(key.getBytes());
		byte[] byArr = cipher.encrypt(sArr.getBytes(charset));
		if ( byArr != null )
			sRet = Base64.encodeToString(byArr);
		return sRet;
	}
}

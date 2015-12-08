package util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import util.codec.Base64;

/**
 * String utility functions
 * <p>
 * Note: this class methods doesn't not support padding with <a
 * href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a> as they require
 * a pair of <code>char</code>s to be represented.
 * </p>
 */
public final class StringUtils
{
	private StringUtils()
	{
	// prevent instanciation
	}

	private final static char CHAR_SPACE = ' ';
	private final static char CHAR_ZERO = '0';
	private final static char[] EMPTY_CHAR_ARRAY = new char[0];

	/**
		use Properties.load(reader) to load properties from a given string
	 */
	public static final Properties string2Properties(String str)
	{
		Properties props = new Properties();
		try
		{
			props.load(new StringReader(str));
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return props;
	}

	/**
		use Properties.store(writer) to store a given properties into string
	 */
	public static final String properties2String(Properties props)
	{
		StringWriter sw = new StringWriter();
		try
		{
			props.store(sw, "");
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return sw.toString();
	}

	/**
	 * Construct a char[] with given char and len
	 * 
	 * @param c the char to fill the array
	 * @param len the desire len of the array
	 */
	public final static char[] getCharArray(char c, int len)
	{
		if ( len < 0 )
		{
			throw new NegativeArraySizeException("length must > 0. len=" + len);
		}
		if ( len == 0 )
		{
			return EMPTY_CHAR_ARRAY;
		}
		return makeCharArray(c, len);
	}

	private final static char[] makeCharArray(char c, int len)
	{
		char[] chars = new char[len];
		// giveup it. its not a native method.
		// Arrays.fill(chars, c);
		while ( len > 0 )
		{
			chars[--len] = c;
		}
		return chars;
	}

	/**
	 * padding a given string to total len with given char at leading or tailing
	 * 
	 * @param inStr the string to padding
	 * @param padChar the char to padding with
	 * @param totalLength total length after padding
	 * @param leading padding at leading or tailing
	 */
	public final static String paddingString(String inStr, char padChar, int totalLength, boolean leading)
	{
		if ( inStr == null )
		{
			return new String(makeCharArray(padChar, totalLength));
		}
		if ( totalLength <= inStr.length() )
		{
			return inStr;
		}
		if ( leading )
		{
			StringBuilder sb = new StringBuilder();
			sb.append(makeCharArray(padChar, totalLength - inStr.length()));
			return sb.append(inStr).toString();
		}
		else
		{
			StringBuilder sb = new StringBuilder(inStr);
			sb.append(makeCharArray(padChar, totalLength - inStr.length()));
			return sb.toString();
		}
	}

	/**
	 * padding a given string with leading space to given total length
	 */
	public final static String padLeadingSpace(String inStr, int totalLength)
	{
		return paddingString(inStr, CHAR_SPACE, totalLength, true);
	}

	/**
	 * padding a given string with tailing space to given total length
	 */
	public final static String padTailingSpace(String inStr, int totalLength)
	{
		return paddingString(inStr, CHAR_SPACE, totalLength, false);
	}

	/**
	 * padding a given string with leading zero to given total length
	 */
	public final static String padLeadingZero(String inStr, int totalLength)
	{
		return paddingString(inStr, CHAR_ZERO, totalLength, true);
	}

	/**
	 * padding a given string with tailing space to given total length
	 */
	public final static String padTailingZero(String inStr, int totalLength)
	{
		return paddingString(inStr, CHAR_ZERO, totalLength, false);
	}

	/**
	 * padding a given int that convert by String.value(x) with leading space to given total length
	 */
	public final static String padLeadingSpace(int x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_SPACE, totalLength, true);
	}

	/**
	 * padding a given int that convert by String.value(x) with tailing space to given total length
	 */
	public final static String padTailingSpace(int x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_SPACE, totalLength, false);
	}

	/**
	 * padding a given int that convert by String.value(x) with leading zero to given total length
	 */
	public final static String padLeadingZero(int x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_ZERO, totalLength, true);
	}

	/**
	 * padding a given int that convert by String.value(x) with tailing zero to given total length
	 */
	public final static String padTailingZero(int x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_ZERO, totalLength, false);
	}

	/**
	 * padding a given long that convert by String.value(x) with leading space to given total length
	 */
	public final static String padLeadingSpace(long x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_SPACE, totalLength, true);
	}

	/**
	 * padding a given long that convert by String.value(x) with tailing space to given total length
	 */
	public final static String padTailingSpace(long x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_SPACE, totalLength, false);
	}

	/**
	 * padding a given long that convert by String.value(x) with leading zero to given total length
	 */
	public final static String padLeadingZero(long x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_ZERO, totalLength, true);
	}

	/**
	 * padding a given long that convert by String.value(x) with tailing zero to given total length
	 */
	public final static String padTailingZero(long x, int totalLength)
	{
		return paddingString(String.valueOf(x), CHAR_ZERO, totalLength, false);
	}

	/**
	 * Convert utf8 string(<code>String.getBytes("UTF-8")</code>) to base64 string
	 */
	public final static String toBase64StringByUTF8(String src)
	{
		if ( src == null )
		{
			return null;
		}
		try
		{
			return Base64.encodeToString(src.getBytes("UTF-8"));
		}
		catch ( UnsupportedEncodingException e )
		{
			// huh!! not support UTF-8 encoding??
			return src;
		}
	}

	/**
	 * Convert base string to utf8 string
	 */
	public final static String toUTF8String(String base64)
	{
		if ( base64 == null )
		{
			return null;
		}
		try
		{
			return new String(Base64.decodeFast(base64), "UTF-8");
		}
		catch ( UnsupportedEncodingException e )
		{
			// huh!! not support UTF-8 encoding??
			return base64;
		}
	}

	/**
	 * convert string encoding by new String(astring.getBytes(fromEncoding), toEncoding));
	 * if any exception occured, e.printStackTrace() then the original string will be returned.
	 */
	public static final String convertEncoding(String original, String fromEncoding, String toEncoding)
	{
		try
		{
			return new String(original.getBytes(fromEncoding), toEncoding);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return original;
		}
	}

	/** if (str == null) return false */
	public static final boolean toBoolean(String str)
	{
		return toBoolean(str, false);
	}

	public static final boolean toBoolean(String str, boolean defaultValue)
	{
		// Optimize implementation is from org.apache.commons.lang.BooleanUtils.
		// add other interned string reference check. and, "0" and charAt[0] == '1' for true
		/////////////////////////// Original Comment ///////////////////////////////
		// Previously used equalsIgnoreCase, which was fast for interned 'true'.
		// Non interned 'true' matched 15 times slower.
		// 
		// Optimisation provides same performance as before for interned 'true'.
		// Similar performance for null, 'false', and other strings not length 2/3/4.
		// 'true'/'TRUE' match 4 times slower, 'tRUE'/'True' 7 times slower.
		if ( str == "true" || str == "yes" || str == "on" || str == "1")
		{
			return true;
		}
		if ( str == "false" || str == "no" || str == "off" || str == "0")
		{
			return false;
		}
		if ( str == null || str.length() == 0 )
		{
			return defaultValue;
		}
		switch ( str.length() )
		{
			case 1 :
			{
				return str.charAt(0) == '1';
			}
			case 2 :
			{
				return ( str.charAt(0) == 'o' || str.charAt(0) == 'O' ) && ( str.charAt(1) == 'n' || str.charAt(1) == 'N' );
			}
			case 3 :
			{
				return ( str.charAt(0) == 'y' || str.charAt(0) == 'Y' ) && ( str.charAt(1) == 'e' || str.charAt(1) == 'E' ) && ( str.charAt(2) == 's' || str.charAt(2) == 'S' );
			}
			case 4 :
			{
				return ( str.charAt(0) == 't' || str.charAt(0) == 'T' ) && ( str.charAt(1) == 'r' || str.charAt(1) == 'R' ) && ( str.charAt(2) == 'u' || str.charAt(2) == 'U' ) && ( str.charAt(3) == 'e' || str.charAt(3) == 'E' );
			}
			default :
			{
				return false;
			}
		}
	}

	public static final void objcatout(Object... objs)
	{
		System.out.println(objcat(objs));
	}

	public static final String objcat(Object... objs)
	{
		if (0 == objs.length)
		{
			return "";
		}

		StringBuilder buf = new StringBuilder();
		for ( Object obj : objs )
		{
			if ( obj != null && obj.getClass().isArray() )
			{
				buf.append(objcat((Object[])obj));
			}
			else
			{
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	public static final String strcat(CharSequence... params)
	{
		if (0 == params.length)
		{
			return "";
		}
		StringBuilder buf = new StringBuilder();
		for ( CharSequence param : params )
		{
			buf.append(param);
		}
		return buf.toString();
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
};
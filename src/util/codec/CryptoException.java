/*
 * CryptoException.java
 * Copyright 2008 thomas at acrosome dot com
 */

package util.codec;

public class CryptoException extends Exception
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 812535384828648796L;

	public CryptoException()
    {}

    public CryptoException(String message)
    {
        super(message);
    }

    public CryptoException(Throwable cause)
    {
        super(cause);
    }

    public CryptoException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
;
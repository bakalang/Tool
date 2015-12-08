/*
 * AESCipther.java
 * Copyright 2008 thomas at acrosome dot com
 */

package util.codec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import util.codec.XAES.BlockSize;
import util.codec.XAES.KeySize;

public class AESCipher
{
    private static class AESKey
    {
        byte[] key;

        AESKey(byte[] key)
        {
            this.key = key;
        }

        public int hashCode()
        {
            return Arrays.hashCode(key);
        }

        public boolean equals(Object obj)
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj instanceof AESKey)
            {
                final AESKey other = (AESKey)obj;
                if ( Arrays.equals(key, other.key) )
                {
                    return true;
                }
            }
            return false;
        }
    }
    
    private static Map<AESKey, AESCipher> _map = new HashMap<AESKey, AESCipher>();

    public static AESCipher getInstance(byte[] key) throws CryptoException
    {
        AESKey aeskey = new AESKey(key);
        AESCipher cipher = _map.get(aeskey);
        if ( cipher == null )
        {
            cipher = new AESCipher(key);
            _map.put(aeskey, cipher);
        }
        return cipher;
    }

    private XAES aes;
    private byte[] key;

    private AESCipher(byte[] key) throws CryptoException
    {
        this.key = key;
        if ( key.length % 16 != 0 )
        {
            throw new CryptoException("invalid key length:" + key.length);
        }
        aes = new XAES(key, BlockSize.BlockSize128, KeySize.KeySize128);
    }

    public byte[] getKey()
    {
        return key;
    }

    public byte[] encrypt(byte[] clear) throws CryptoException
    {
        return aes.EncryptData(clear);
    }

    public byte[] decrypt(byte[] secret) throws CryptoException
    {
        return aes.DecryptData(secret);
    }
}
;
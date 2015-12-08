package util.codec;

import java.util.Arrays;

import util.ByteUtils;

class XAES
{
    private static final int m_InCo_int = 0x0e090d0b;
    private static byte[] m_InCo = new byte[ 4 ];
    private static int[] m_lOnBits = new int[ 31 ];
    private static int[] m_l2Power = new int[ 31 ];
    private static byte[] m_bytOnBits = new byte[ 8 ];
    private static byte[] m_byt2Power = new byte[ 8 ];

    private static byte[] m_fbsub = new byte[ 256 ];
    private static byte[] m_rbsub = new byte[ 256 ];
    private static byte[] m_ptab = new byte[ 256 ];
    private static byte[] m_ltab = new byte[ 256 ];
    private static int[] m_ftable = new int[ 256 ];
    private static int[] m_rtable = new int[ 256 ];
    private static int[] m_rco = new int[ 30 ];

    private final int m_Nk;
    private final int m_Nb;
    private final int m_Nr;

    private byte[] m_fi = new byte[ 24 ];
    private byte[] m_ri = new byte[ 24 ];
    private int[] m_fkey = new int[ 120 ];
    private int[] m_rkey = new int[ 120 ];

    static
    {
        m_InCo[ 0 ] = 0xB;
        m_InCo[ 1 ] = 0xD;
        m_InCo[ 2 ] = 0x9;
        m_InCo[ 3 ] = 0xE;

        for ( byte i = 0; i < m_bytOnBits.length; ++i)
        {
            m_bytOnBits[i] = (byte)( (1 << (i + 1)) - 1 );
        }

        for ( byte i = 0; i < m_byt2Power.length; ++i)
        {
            m_byt2Power[i] = (byte)( 1 << i );
        }

        for ( int i = 0; i < m_lOnBits.length; ++i)
        {
            m_lOnBits[i] = (1 << (i + 1)) - 1;
        }

        for ( int i = 0; i < m_l2Power.length; ++i)
        {
            m_l2Power[i] = 1 << i;
        }
        gentables();
    }

    static enum BlockSize 
    { 
        BlockSize128(4),
        BlockSize192(6),
        BlockSize256(8);

        private final int bit;

        BlockSize(int bit)
        {
            this.bit = bit;
        }
        int getBit()
        {
            return bit;
        }
    }
    
    static enum KeySize 
    {
        KeySize128(4),
        KeySize192(6),
        KeySize256(8);

        private final int bit;

        KeySize(int bit)
        {
            this.bit = bit;
        }
        int getBit()
        {
            return bit;
        }
    }

    private static int toUnsign(int b)
    {
        return b & 0xff;
    }

    // *******************************************************************************
    //  RShiftByte (FUNCTION)
    // *******************************************************************************
    private static byte RShiftByte( byte bytValue, int bytShiftBits )
    {
        if ( bytShiftBits == 0 )
        {
            return bytValue;
        }
        else if ( bytShiftBits == 7 )
        {
            if ( (bytValue & 0X80) != 0 )
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        else if ( bytShiftBits < 0 | bytShiftBits > 7 )
        {
            throw new IllegalArgumentException("Byte shift bits must 0 < bit < 7, given:" + bytShiftBits);
        }
        else
        {
            return (byte)( bytValue / m_byt2Power[ bytShiftBits ] );
        }
    }

    // *******************************************************************************
    //  RotateLeft (FUNCTION)
    // *******************************************************************************
    private static int RotateLeft( int lValue, int iShiftBits )
    {
        return ( lValue << iShiftBits ) | ( lValue >>> 32 - iShiftBits );
    }

    // '*******************************************************************************
    // ' RotateLeftByte (FUNCTION)
    // *******************************************************************************
    private static byte RotateLeftByte( byte bytValue, int bytShiftBits )
    {
        return (byte)(( bytValue << bytShiftBits) | RShiftByte( bytValue, 8 - bytShiftBits ));
    }

    // *******************************************************************************
    //  xtime (FUNCTION)
    // *******************************************************************************
    private static byte xtime( byte a )
    {
        if ( (a & 0x80) != 0 )
        {
            return (byte)((a << 1) ^ 0x1B);
        }
        else
        {
            return (byte)(a << 1);
        }
    }

    // *******************************************************************************
    //  bmul (FUNCTION)
    // *******************************************************************************
    private static byte bmul( byte x, byte y )
    {
        if ( x != 0 & y != 0 )
        {
            return m_ptab[ (toUnsign( m_ltab[ toUnsign(x) ]) + toUnsign(m_ltab[ toUnsign(y) ] )) % 255 ];
        }
        else
        {
            return 0;
        }
    }

    // *******************************************************************************
    //  SubByte (FUNCTION)
    // *******************************************************************************
    private static int SubByte( int a )
    {
        byte[] b = new byte[ 4 ];
        ByteUtils.getBytes(a, b, 0, false);

        b[ 0 ] = m_fbsub[ toUnsign(b[ 0 ]) ];
        b[ 1 ] = m_fbsub[ toUnsign(b[ 1 ]) ];
        b[ 2 ] = m_fbsub[ toUnsign(b[ 2 ]) ];
        b[ 3 ] = m_fbsub[ toUnsign(b[ 3 ]) ];
        
        return ByteUtils.getInt(b, 0, false);
    }

    // *******************************************************************************
    //  product (FUNCTION)
    // *******************************************************************************
    private static int product( int x, int y )
    {
        byte[] xb = ByteUtils.getBytes(x, false);
        byte[] yb = ByteUtils.getBytes(y, false);

        return toUnsign(bmul( xb[ 0 ], yb[ 0 ] )) ^
               toUnsign(bmul( xb[ 1 ], yb[ 1 ] )) ^
               toUnsign(bmul( xb[ 2 ], yb[ 2 ] )) ^
               toUnsign(bmul( xb[ 3 ], yb[ 3 ] ));
    }

    // *******************************************************************************
    //  InvMixCol (FUNCTION)
    // *******************************************************************************
    private static int InvMixCol( int x )
    {
        int m = 0;
        byte[] b = new byte[ 4 ];
        m = m_InCo_int;

        b[ 3 ] = (byte)product( m, x );
        m = RotateLeft( m, 24 );
        b[ 2 ] = (byte)product( m, x );
        m = RotateLeft( m, 24 );
        b[ 1 ] = (byte)product( m, x );
        m = RotateLeft( m, 24 );
        b[ 0 ] = (byte)product( m, x );

        return ByteUtils.getInt(b, 0, false);
    }

    // *******************************************************************************
    //  ByteSub (FUNCTION)
    // *******************************************************************************
    private static byte ByteSub( byte x )
    {
        byte y = m_ptab[ 255 - toUnsign(m_ltab[ toUnsign(x) ]) ];
        x = y;
        x = RotateLeftByte( x, 1 );
        y = (byte)(y ^ x);
        x = RotateLeftByte( x, 1 );
        y = (byte)(y ^ x);
        x = RotateLeftByte( x, 1 );
        y = (byte)(y ^ x);
        x = RotateLeftByte( x, 1 );
        y = (byte)(y ^ x);
        y = (byte)( y ^ 0x63 );

        return y;
    }

    // *******************************************************************************
    //  gentables (SUB)
    // *******************************************************************************
    private static void gentables()
    {
        int i = 0;
        byte y = 0;
        byte[] b = new byte[ 4 ];
        byte ib = 0;

        m_ltab[ 0 ] = 0;
        m_ptab[ 0 ] = 1;
        m_ltab[ 1 ] = 0;
        m_ptab[ 1 ] = 3;
        m_ltab[ 3 ] = 1;

        for ( i=2; i < 256; i++ )
        {
            m_ptab[ i ] = (byte)(m_ptab[ i - 1 ] ^ xtime( m_ptab[ i - 1 ] ));
            m_ltab[ toUnsign(m_ptab[ i ]) ] =  (byte) i ;
        }
        m_fbsub[ 0 ] = ( 0x63 );
        m_rbsub[ 0x63 ] = 0;
        for ( i=1; i < 256; i++ )
        {
            ib = (byte)i;
            y = ByteSub( ib );
            m_fbsub[ i ] = y;
            m_rbsub[ toUnsign(y) ] = (byte)i;
        }
        y = 1;
        for ( i=0; i < 30; i++ )
        {
            m_rco[ i ] = toUnsign(y);
            y = xtime( y );
        }

        for ( i=0; i < 256; i++ )
        {
            y = m_fbsub[ i ];
            b[ 3 ] = (byte)(y ^ xtime( y ));
            b[ 2 ] = y;
            b[ 1 ] = y;
            b[ 0 ] = xtime( y );
            m_ftable[ i ] = ByteUtils.getInt(b, 0, false);

            y = m_rbsub[ i ];
            b[ 3 ] = bmul( m_InCo[ 0 ], y );
            b[ 2 ] = bmul( m_InCo[ 1 ], y );
            b[ 1 ] = bmul( m_InCo[ 2 ], y );
            b[ 0 ] = bmul( m_InCo[ 3 ], y );
            m_rtable[ i ] = ByteUtils.getInt(b, 0, false);
        }
    }

    // *******************************************************************************
    //  gkey (SUB)
    // *******************************************************************************
    private void gkey(byte[] key )
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int N = 0;
        int C1 = 0;
        int C2 = 0;
        int C3 = 0;
        int[] CipherKey = new int[ 8 ];

        C1 = 1;
        if ( m_Nb < 8 )
        {
            C2 = 2;
            C3 = 3;
        }
        else
        {
            C2 = 3;
            C3 = 4;
        }

        for ( j=0; j < m_Nb; j++ )
        {
            m = j * 3;

            m_fi[ m ] = (byte)( ( j + C1 ) % m_Nb );
            m_fi[ m + 1 ] = (byte)( ( j + C2 ) % m_Nb );
            m_fi[ m + 2 ] = (byte)( ( j + C3 ) % m_Nb );
            m_ri[ m ] = (byte)( ( m_Nb + j - C1 ) % m_Nb );
            m_ri[ m + 1 ] = (byte)( ( m_Nb + j - C2 ) % m_Nb );
            m_ri[ m + 2 ] = (byte)( ( m_Nb + j - C3 ) % m_Nb );
        }

        N = m_Nb * ( m_Nr + 1 );

        for ( i=0; i < m_Nk; i++ )
        {
            j = i * 4;
            CipherKey[ i ] = ByteUtils.getInt(key, j, false);
        }

        for ( i=0; i < m_Nk; i++ )
        {
            m_fkey[ i ] = CipherKey[ i ];
        }

        j = m_Nk;
        k = 0;
        while ( j < N )
        {
            m_fkey[ j ] = m_fkey[ j - m_Nk ] ^ SubByte( RotateLeft( m_fkey[ j - 1 ], 24 ) ) ^ m_rco[ k ];
            if ( m_Nk <= 6 )
            {
                i = 1;
                while ( i < m_Nk & ( i + j ) < N )
                {
                    m_fkey[ i + j ] = m_fkey[ i + j - m_Nk ] ^ m_fkey[ i + j - 1 ];
                    i = i + 1;
                }
            }
            else
            {
                //  Problem fixed here
                i = 1;
                while ( i < 4 & ( i + j ) < N )
                {
                    m_fkey[ i + j ] = m_fkey[ i + j - m_Nk ] ^ m_fkey[ i + j - 1 ];
                    i = i + 1;
                }
                if ( j + 4 < N )
                {
                    m_fkey[ j + 4 ] = m_fkey[ j + 4 - m_Nk ] ^ SubByte( m_fkey[ j + 3 ] );
                }
                i = 5;
                while ( i < m_Nk & ( i + j ) < N )
                {
                    m_fkey[ i + j ] = m_fkey[ i + j - m_Nk ] ^ m_fkey[ i + j - 1 ];
                    i = i + 1;
                }
            }

            j = j + m_Nk;
            k = k + 1;
        }

        for ( j=0; j < m_Nb; j++ )
        {
            m_rkey[ j + N - m_Nb ] = m_fkey[ j ];
        }

        i = m_Nb;
        while ( i < N - m_Nb )
        {
            k = N - m_Nb - i;
            for ( j=0; j < m_Nb; j++ )
            {
                m_rkey[ k + j ] = InvMixCol( m_fkey[ i + j ] );
            }
            i = i + m_Nb;
        }

        j = N - m_Nb;
        while ( j < N )
        {
            m_rkey[ j - N + m_Nb ] = m_fkey[ j ];
            j = j + 1;
        }
    }


    // *******************************************************************************
    //  encrypt (SUB)
    // *******************************************************************************
    private void encryptbuffer( byte[] buff )
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int[] a = new int[ 8 ];
        int[] x = new int[ 8 ];
        int[] y = new int[ 8 ];
        int[] t = new int[ 8 ];

        for ( i=0; i < m_Nb; i++ )
        {
            j = i * 4;

            a[ i ] = ByteUtils.getInt(buff, j, false);
            a[ i ] = a[ i ] ^ m_fkey[ i ];
        }

        k = m_Nb;
        System.arraycopy( a, 0, x, 0, a.length );

        for ( i=1; i < m_Nr; i++ )
        {
            for ( j=0; j < m_Nb; j++ )
            {
                m = j * 3;
                y[ j ] = m_fkey[ k ] ^ 
                         m_ftable[ x[ j ] & m_lOnBits[ 7 ] ] ^ 
                         RotateLeft( m_ftable[ ( x[ m_fi[ m ] ] >> 8 ) & m_lOnBits[ 7 ] ], 8 ) ^ 
                         RotateLeft( m_ftable[ ( x[ m_fi[ m + 1 ] ] >> 16 ) & m_lOnBits[ 7 ] ], 16 ) ^ 
                         RotateLeft( m_ftable[ ( x[ m_fi[ m + 2 ] ] >> 24 ) & m_lOnBits[ 7 ] ], 24 );
                k = k + 1;
            }
            System.arraycopy( x, 0, t, 0, x.length );
            System.arraycopy( y, 0, x, 0, y.length );
            System.arraycopy( t, 0, y, 0, t.length );
        }
        for ( j=0; j < m_Nb; j++ )
        {
            m = j * 3;
            y[ j ] = m_fkey[ k ] ^ 
                     toUnsign(m_fbsub[ x[ j ] & m_lOnBits[ 7 ] ]) ^ 
                     RotateLeft( toUnsign(m_fbsub[ ( x[ m_fi[ m ] ] >> 8 ) & m_lOnBits[ 7 ] ]), 8 ) ^ 
                     RotateLeft( toUnsign(m_fbsub[ ( x[ m_fi[ m + 1 ] ] >> 16 ) & m_lOnBits[ 7 ] ]), 16 ) ^ 
                     RotateLeft( toUnsign(m_fbsub[ ( x[ m_fi[ m + 2 ] ] >>  24 ) & m_lOnBits[ 7 ] ]), 24 );
            k = k + 1;
        }
        for ( i=0; i < m_Nb; i++ )
        {
            j = i * 4;
            ByteUtils.getBytes(y[ i ], buff, j, false);
            x[ i ] = 0;
            y[ i ] = 0;
        }
    }

    // *******************************************************************************
    //  decrypt (SUB)
    // *******************************************************************************
    private void decryptBuffer( byte[] buff )
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int[] a = new int[ 8 ];
        int[] x = new int[ 8 ];
        int[] y = new int[ 8 ];
        int[] t = new int[ 8 ];

        for ( i=0; i < m_Nb; i++ )
        {
            j = i * 4;
            a[ i ] = ByteUtils.getInt(buff, j, false);
            a[ i ] = a[ i ] ^ m_rkey[ i ];
        }
        k = m_Nb;
        System.arraycopy( a, 0, x, 0, a.length );
        for ( i=1; i < m_Nr; i++ )
        {
            for ( j=0; j < m_Nb; j++ )
            {
                m = j * 3;
                y[ j ] = m_rkey[ k ] ^ 
                         m_rtable[ x[ j ] & m_lOnBits[ 7 ] ] ^ 
                         RotateLeft( m_rtable[ ( x[ m_ri[ m ] ] >> 8 ) & m_lOnBits[ 7 ] ], 8 ) ^ 
                         RotateLeft( m_rtable[ ( x[ m_ri[ m + 1 ] ] >> 16 ) & m_lOnBits[ 7 ] ], 16 ) ^ 
                         RotateLeft( m_rtable[ ( x[ m_ri[ m + 2 ] ] >> 24 ) & m_lOnBits[ 7 ] ], 24 );
                k = k + 1;
            }
            System.arraycopy( x, 0, t, 0, x.length );
            System.arraycopy( y, 0, x, 0, y.length );
            System.arraycopy( t, 0, y, 0, t.length );
        }
        for ( j=0; j < m_Nb; j++ )
        {
            m = j * 3;

            y[ j ] = m_rkey[ k ] ^ 
                     toUnsign(m_rbsub[ x[ j ] & m_lOnBits[ 7 ] ]) ^ 
                     RotateLeft( toUnsign(m_rbsub[ ( x[ m_ri[ m ] ] >> 8 ) & m_lOnBits[ 7 ] ]), 8 ) ^ 
                     RotateLeft( toUnsign(m_rbsub[ ( x[ m_ri[ m + 1 ] ] >> 16 ) & m_lOnBits[ 7 ] ]), 16 ) ^ 
                     RotateLeft( toUnsign(m_rbsub[ ( x[ m_ri[ m + 2 ] ] >> 24 ) & m_lOnBits[ 7 ] ]), 24 );
            k = k + 1;
        }
        for ( i=0; i < m_Nb; i++ )
        {
            j = i * 4;

            ByteUtils.getBytes(y[ i ], buff, j, false);
            x[ i ] = 0;
            y[ i ] = 0;
        }
    }

    XAES(byte[] key, BlockSize blocksize, KeySize keysize)
    {
        m_Nk = blocksize.bit;
        m_Nb = keysize.bit;
        if ( m_Nb >= m_Nk )
        {
            m_Nr = 6 + m_Nb;
        }
        else
        {
            m_Nr = 6 + m_Nk;
        }
        //  Use first 32 bytes of the password for the key
        //  Prepare the key; assume 256 bit block and key size
        byte[] byteKey = new byte[32];
        // secure all kyteKey are zero
        Arrays.fill(byteKey, (byte)0);
        System.arraycopy(key, 0, byteKey, 0, Math.min(32, key.length));
        gkey(byteKey);
    }

    // *******************************************************************************
    //  EncryptData (FUNCTION)
    //
    //  Takes the message, whatever the size, and password in one call and does
    //  everything for you to return an encoded/encrypted message
    // *******************************************************************************
    byte[] EncryptData(byte[] clear) throws CryptoException
    {

        //  We are going to put the message size on the front of the message
        //  in the first 4 bytes. If the length is more than a max int we are
        //  in trouble
        int len = clear.length;
        int encryptedLength = clear.length + 4;

        //  The encoded length includes the 4 bytes stuffed on the front
        //  and is padded out to be modulus 32
        if ( encryptedLength % 16 != 0 )
        {
            encryptedLength = encryptedLength + 16 - ( encryptedLength % 16 );
        }
        byte[] buff = new byte[ 16 ];
        byte[] inTemp = new byte[ encryptedLength + 4 ];
        byte[] byteOut = new byte[ encryptedLength ];

        try
        {
            //  Put the length on the front
            System.arraycopy( ByteUtils.getBytes(len, false), 0, inTemp, 0, 4 );
            //  Put the rest of the message after it
            System.arraycopy( clear, 0, inTemp, 4, len );
            //  Encrypt a block at a time
            for ( int i=0; i < encryptedLength; i+=16 )
            {
                System.arraycopy( inTemp, i, buff, 0, 16 );
                encryptbuffer( buff );
                System.arraycopy( buff, 0, byteOut, i, 16 );
            }
        }
        catch ( Exception e )
        {
            throw new CryptoException(e);
        }
        return byteOut;
    }

    // *******************************************************************************
    //  DecryptData (FUNCTION)
    //
    //  Opposite of Encryptdata
    // *******************************************************************************
    byte[] DecryptData( byte[] secret) throws CryptoException
    {
        int secretLength = secret.length;
        if ( secretLength == 0 || secretLength % 16 != 0 )
        {
            throw new CryptoException("bad length:" + secretLength + " in encrypted data");
        }

        //  The output array needs to be the same size as the input array
        byte[] outTemp = new byte[secretLength];
        //  Decrypt a block at a time
        byte[] buff = new byte[ 16 ];
        for ( int i=0; i < secretLength; i+=16 )
        {
            System.arraycopy( secret, i, buff, 0, 16 );
            decryptBuffer( buff );
            System.arraycopy( buff, 0, outTemp, i, 16 );
        }
        //  Get the original length of the string from the first 4 bytes
        int clearLength = ByteUtils.getInt(outTemp, false);
        //  Make sure the length is consistent with our data
        if ( clearLength < 0 || clearLength > secretLength - 4 )
        {
            throw new CryptoException("bad length in decrypted data. expect:" + (secretLength - 4) + " but " + clearLength);
        }
        //  Prepare the output message byte array
        byte[] byteClear = new byte[clearLength];
        System.arraycopy( outTemp, 4, byteClear, 0, clearLength );
        return byteClear;
    }
}
;
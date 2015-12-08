package util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import util.log.Log4JUtils;

public class IOUtils
{
	private static final Logger log = Log4JUtils.getClassnameLogger();
	public static final int DEFULT_BUFFER_LENGHT;

	static
	{
		int buflen = 8192;
		try
		{
			buflen = Integer.parseInt(System.getProperty("com.ecareme.utils.IOUtils.buffer_length"));
			buflen = buflen > 0? buflen: 8192;
		}
		catch ( Exception e )
		{
			//ignore
		}
		DEFULT_BUFFER_LENGHT = buflen;
		System.out.println("IOUtils.DEFULT_BUFFER_LENGHT=" + DEFULT_BUFFER_LENGHT);
	}

	public static long copyIO(final ReadableByteChannel in, final WritableByteChannel out) throws IOException
	{
		return copyIO(in, out, DEFULT_BUFFER_LENGHT);
	}

	public static long copyIO(final ReadableByteChannel in, final WritableByteChannel out, int buffer_size) throws IOException
	{
		final ByteBuffer buf = ByteBuffer.allocateDirect(buffer_size);
		long count = 0;
		for ( int i = in.read(buf); i > -1; i = in.read(buf) )
		{
			buf.flip();
			out.write(buf);
			buf.clear();
			count += i;
		}
		return count;
	}

	public static long conditionalCopyRange(InputStream in, OutputStream out, long start, long length, IOCopyCondition condition) throws IOException
	{
		if ( condition == null )
		{
			throw new IllegalArgumentException("condition is null.");
		}
		long skipped = in.skip(start);
		if ( start != skipped )
		{
			throw new IOException("skipped length != start, cannot skip to the start offset");
		}
		long copied = 0;
		byte[] buffer = new byte[condition.getBufferSize()];
		int n = 0;
		condition.setStartTime(System.currentTimeMillis());
		while ( length > 0 && -1 != ( n = in.read(buffer, 0, (int)Math.min(length, buffer.length)) ) )
		{
			out.write(buffer, 0, n);
			length -= n;
			copied += n;
			if ( !condition.applyCondition(copied) )
			{
				break;
			}
		}
		return copied;
	}

	public static long conditionalCopyRange(Reader in, Writer out, long start, long length, IOCopyCondition condition) throws IOException
	{
		if ( condition == null )
		{
			throw new IllegalArgumentException("condition is null.");
		}
		long skipped = in.skip(start);
		if ( start != skipped )
		{
			throw new IOException("skipped length != start, cannot skip to the start offset");
		}
		long copied = 0;
		char[] buffer = new char[condition.getBufferSize()];
		int n = 0;
		condition.setStartTime(System.currentTimeMillis());
		while ( length > 0 && -1 != ( n = in.read(buffer, 0, (int)Math.min(length, buffer.length)) ) )
		{
			out.write(buffer, 0, n);
			length -= n;
			copied += n;
			if ( !condition.applyCondition(copied) )
			{
				break;
			}
		}
		return copied;
	}

	public static long conditionalCopyIO(InputStream in, OutputStream out, IOCopyCondition condition) throws IOException
	{
		if ( condition == null )
		{
			throw new IllegalArgumentException("condition is null.");
		}
		byte[] buffer = new byte[condition.getBufferSize()];
		long read = 0;
		condition.setStartTime(System.currentTimeMillis());
		for ( int i = in.read(buffer); i != -1; i = in.read(buffer) )
		{
			out.write(buffer, 0, i);
			read += i;
			if ( !condition.applyCondition(read) )
			{
				break;
			}
		}
		return read;
	}

	public static long conditionalCopyIO(Reader in, Writer out, IOCopyCondition condition) throws IOException
	{
		if ( condition == null )
		{
			throw new IllegalArgumentException("condition is null.");
		}
		char[] buffer = new char[condition.getBufferSize()];
		long read = 0;
		condition.setStartTime(System.currentTimeMillis());
		for ( int i = in.read(buffer); i != -1; i = in.read(buffer) )
		{
			out.write(buffer, 0, i);
			read += i;
			if ( !condition.applyCondition(read) )
			{
				break;
			}
		}
		return read;
	}

	public static long limitCopyIO(InputStream in, OutputStream out, final long limitSize) throws IOException
	{
		return conditionalCopyIO(in, out, new IOCopyCondition.SpeedLimitCondition(limitSize));
	}

	public static long limitCopyIO(Reader in, Writer out, final long limitSize) throws IOException
	{
		return conditionalCopyIO(in, out, new IOCopyCondition.SpeedLimitCondition(limitSize));
	}

	public static long limitCopyRange(InputStream in, OutputStream out, long start, long length, final long limitSize) throws IOException
	{
		return conditionalCopyRange(in, out, start, length, new IOCopyCondition.SpeedLimitCondition(limitSize));
	}

	public static long limitCopyRange(Reader in, Writer out, long start, long length, final long limitSize) throws IOException
	{
		return conditionalCopyRange(in, out, start, length, new IOCopyCondition.SpeedLimitCondition(limitSize));
	}

	public static long copyRange(InputStream in, OutputStream out, long start, long length) throws IOException
	{
		return copyRange(in, out, start, length, DEFULT_BUFFER_LENGHT);
	}

	public static long copyRange(InputStream in, OutputStream out, long start, long length, int buffer_size) throws IOException
	{
		long skipped = in.skip(start);
		if ( start != skipped )
		{
			throw new IOException("skipped length != start, cannot skip to the start offset");
		}
		long count = 0;
		byte[] buffer = new byte[buffer_size];
		int n = 0;
		while ( length > 0 && -1 != ( n = in.read(buffer, 0, (int)Math.min(length, buffer.length)) ) )
		{
			out.write(buffer, 0, n);
			length -= n;
			count += n;
		}
		return count;
	}

	public static long copyRange(Reader in, Writer out, long start, long length) throws IOException
	{
		return copyRange(in, out, start, length, DEFULT_BUFFER_LENGHT);
	}

	public static long copyRange(Reader in, Writer out, long start, long length, int buffer_size) throws IOException
	{
		long skipped = in.skip(start);
		if ( start != skipped )
		{
			throw new IOException("skipped length != start, cannot skip to the start offset");
		}
		long count = 0;
		char[] buffer = new char[buffer_size];
		int n = 0;
		while ( length > 0 && -1 != ( n = in.read(buffer, 0, (int)Math.min(length, buffer.length)) ) )
		{
			out.write(buffer, 0, n);
			length -= n;
			count += n;
		}
		return count;
	}

	public static long copyIO(InputStream in, OutputStream out) throws IOException
	{
		return copyIO(in, out, DEFULT_BUFFER_LENGHT);
	}

	public static long copyIO(InputStream in, OutputStream out, int buffer_size) throws IOException
	{
		long count = 0;
		byte[] buffer = new byte[buffer_size];
		int n = 0;
		while ( -1 != ( n = in.read(buffer) ) )
		{
			out.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static long copyIO(Reader in, Writer out) throws IOException
	{
		return copyIO(in, out, DEFULT_BUFFER_LENGHT);
	}

	public static long copyIO(Reader in, Writer out, int buffer_size) throws IOException
	{
		long count = 0;
		char[] buffer = new char[buffer_size];
		int n = 0;
		while ( -1 != ( n = in.read(buffer) ) )
		{
			out.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void closeQuitely(Reader in, Writer out)
	{
		closeQuietly(in, out);
	}

	public static void closeQuitely(InputStream in, OutputStream out)
	{
		closeQuietly(in, out);
	}

	public static void closeQuitely(Reader in)
	{
		closeQuietly(in);
	}

	public static void closeQuitely(Writer out)
	{
		closeQuietly(out);
	}

	public static void closeQuitely(InputStream in)
	{
		closeQuietly(in);
	}

	public static void closeQuitely(OutputStream out)
	{
		closeQuietly(out);
	}

	public static void closeQuitely(Closeable... closeables)
	{
		closeQuietly(closeables);
	}

	public static void closeQuietly(Closeable... closeables)
	{
		for ( Closeable c : closeables )
		{
			if ( c instanceof Flushable )
			{
				Flushable f = (Flushable)c;
				try
				{
					f.flush();
				}
				catch ( Exception e )
				{
					log.warn(StringUtils.objcat("IOUtils:error on out.close() message:", e.getMessage(), " caller:", getCaller(e.getStackTrace())), e);
				}
			}
			try
			{
				c.close();
			}
			catch ( Exception e )
			{
				log.warn(StringUtils.objcat("IOUtils:error on out.close() message:", e.getMessage(), " caller:", getCaller(e.getStackTrace())), e);
			}
		}
	}

	public static void closeQuitely(ImageInputStream in)
	{
		try
		{
			in.close();
		}
		catch ( Exception e )
		{
			log.warn(StringUtils.objcat("IOUtils:error on out.close() message:", e.getMessage(), " caller:", getCaller(e.getStackTrace())), e);
		}
	}

	private static StackTraceElement getCaller(StackTraceElement[] trace)
	{
		for ( StackTraceElement s : trace )
		{
			if ( !"com.ecareme.utils.IOUtils".equals(s.getClassName()) )
			{
				return s;
			}
		}
		return null;
	}
}
;
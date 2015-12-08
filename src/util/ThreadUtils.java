package util;

import java.io.OutputStream;
import java.io.PrintStream;

public class ThreadUtils
{
	public static void sleepQuietly(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * @see java.lang.Object#notify()
	 * @param lock
	 */
	public static void objectNotify(Object lock)
	{
		synchronized ( lock )
		{
			lock.notify();
		}
	}

	/**
	 * @see java.lang.Object#notifyAll()
	 * @param lock
	 */
	public static void objectNotifyAll(Object lock)
	{
		synchronized ( lock )
		{
			lock.notifyAll();
		}
	}

	/**
	 * @see java.lang.Object#wait()
	 * @param millis
	 * @throws InterruptedException
	 */
	public static void objectWait(Object lock) throws InterruptedException
	{
		synchronized ( lock )
		{
			lock.wait();
		}
	}

	/**
	 * @see java.lang.Object#wait(long)
	 * @param millis
	 * @throws InterruptedException
	 */
	public static void threadWait(long millis) throws InterruptedException
	{
		objectWait(Thread.currentThread(), millis);
	}

	/**
	 * @see java.lang.Object#wait(long)
	 * @param millis
	 * @throws InterruptedException
	 */
	public static void threadWaitSafe(long millis)
	{
		try
		{
			objectWait(Thread.currentThread(), millis);
		}
		catch ( InterruptedException e )
		{
			//e.printStackTrace();
			throw new RuntimeException(e); // throw runtime exception to avoid thread death
		}
	}

	/**
	 * @see java.lang.Object#wait(long)
	 * @param millis
	 * @throws InterruptedException
	 */
	public static void objectWait(Object lock, long millis) throws InterruptedException
	{
		synchronized ( lock )
		{
			lock.wait(millis);
		}
	}

	/**
	 * @see java.lang.Object#wait(long)
	 * @param millis
	 * @throws InterruptedException
	 */
	public static void objectWaitSafe(Object lock, long millis)
	{
		try
		{
			objectWait(lock, millis);
		}
		catch ( InterruptedException e )
		{
			//e.printStackTrace();
			throw new RuntimeException(e); // throw runtime exception to avoid dead lock
		}
	}

	public static void objectWaitSafe(Object lock)
	{
		try
		{
			objectWait(lock);
		}
		catch ( InterruptedException e )
		{
			//e.printStackTrace();
			throw new RuntimeException(e); // throw runtime exception to avoid dead lock
		}
	}

	private static final StackTraceElement UNKNOWK_STACK = new StackTraceElement("UNKNOWN CLASS", "UNKNOWN METHOD", "UNKNOWN SOURCE", 0);

	public static void dumpStack(OutputStream out)
	{
		new Exception("Stack trace").printStackTrace(new PrintStream(out));
	}

	public static void dumpStack()
	{
		dumpStack(System.out);
	}

	public static String getCurrentMethodName()
	{
		return getStackTraceElement(3).getMethodName();
	}

	public static String getCurrentClassName()
	{
		return getStackTraceElement(3).getClassName();
	}

	public static StackTraceElement getCurrentStackElement()
	{
		return getStackTraceElement(3);
	}

	public static String getCallerClassName()
	{
		return getStackTraceElement(4).getClassName();
	}

	public static String getCallerMethodName()
	{
		return getStackTraceElement(4).getMethodName();
	}

	public static StackTraceElement getCallerStackElement()
	{
		return getStackTraceElement(4);
	}

	private static StackTraceElement getStackTraceElement(int idx)
	{
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		if ( stacks.length > idx )
		{
			return stacks[idx];
		}
		else
		{
			return UNKNOWK_STACK;
		}
	}
};

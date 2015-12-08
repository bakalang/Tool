package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassLoaderUtils
{
	/**
	 * Load a given resource. This method will try to load the resource using the following methods (in order):
	 * 1st Thread.currentThread().getContextClassLoader()
	 * 2nd ClassLoaderTools.class.getClassLoader()
	 * 3rd callerClass.getClassLoader() if provided
	 * 
	 * @param resource	the name of the resource
	 * @param callerClass	the class of the caller
	 */
	public static URL getResource(String resource, Class callerClass)
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);

		if (url == null)
		{
			url = ClassLoaderUtils.class.getClassLoader().getResource(resource);
		}

		if (url == null && callerClass != null)
		{
			url = callerClass.getClassLoader().getResource(resource);
		}

		if ( url == null && resource != null && resource.charAt(0) != '/')
		{
			return getResource('/' + resource, callerClass);
		}
		return url;
	}

	public static URL getResource(String resource)
	{
		return getResource(resource, null);
	}

	/**
	 * This is a convenience method to load a resource as a stream.
	 * The algorithm used to find the resource is given in getResource()
	 * @param resource	the name of the resource
	 * @param callerClass	the class of the caller
	 * @throws IOException 
	 */
	public static InputStream getResourceAsStream(String resource, Class callerClass) throws IOException
	{
		URL url = getResource(resource, callerClass);
		return (url != null) ? url.openStream() : null;
	}

	public static InputStream getResourceAsStream(String resource) throws IOException
	{
		return getResourceAsStream(resource, null);
	}

	/**
	 * Load a class with a given name. It will try to load the class in the following order:
	 * 
	 * 1st the Class.forName()
	 * 2nd Thread.currentThread().getContextClassLoader()
	 * 3rd ClassLoaderTools.class.getClassLoader()
	 *
	 * @param className The name of the class to load
	 * @throws ClassNotFoundException If the class cannot be found anywhere.
	 */
	public static Class loadClass(String className) throws ClassNotFoundException
	{
		try
		{
			return Class.forName(className);
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				return Thread.currentThread().getContextClassLoader().loadClass(className);
			}
			catch (ClassNotFoundException ee)
			{
				return ClassLoaderUtils.class.getClassLoader().loadClass(className);
			}
		}
	}
};
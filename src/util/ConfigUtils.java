
package util;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class ConfigUtils
{

	private static final Map configs = new HashMap();
	
	/**
	 * load an xml configuration
	 */
	public final static XMLConfiguration loadClassPathXML(String xmlFile)
	{
		return loadClassPathXML(xmlFile, null);
	}
	
	public final static XMLConfiguration loadClassPathXML(String xmlFile, ClassLoader loader)
	{
		if ( xmlFile != null )
		{
			if ( loader == null )
			{
				return loadXML(ClassLoaderUtils.getResource(xmlFile));
			}
			else
			{
				return loadXML(loader.getResource(xmlFile));
			}
		}
		else
		{
			System.out.println(xmlFile + " load failed, an empty Configuration returned.");
			new Exception().printStackTrace();
			return new XMLConfiguration();
		}
	}
	
	/**
	 * Load an XMLConfiguration from given Reader NOTICE: cache function is no available for load from Reader
	 */
	public final static XMLConfiguration loadXML(Reader in) throws ConfigurationException
	{
		XMLConfiguration conf = new XMLConfiguration();
		conf.load(in);
		return conf;
	}

	/**
	 * Load an XMLConfiguration from given InputStream NOTICE: cache function is no available for load from inputstream
	 * @throws org.apache.commons.configuration.ConfigurationException 
	 */
	public final static XMLConfiguration loadXML(InputStream in) throws ConfigurationException
	{
		XMLConfiguration conf = new XMLConfiguration();
		conf.load(in);
		return conf;
	}
	
	public final static XMLConfiguration loadXML(URL xmlFile)
	{
		if ( xmlFile == null )
		{
			System.out.println("Configuration load failed, an empty Configuration returned. error:given parameter is null");
			return new XMLConfiguration();
		}
		XMLConfiguration conf = (XMLConfiguration)configs.get(xmlFile.toString());
		if ( conf == null )
		{
			InputStream in = null;
			try
			{
				in = xmlFile.openStream();
				conf = loadXML(in);
				conf.setFile(new File(xmlFile.toURI()));
				configs.put(xmlFile.toString(), conf);
				System.out.println("ConfigUtils loaded " + xmlFile);
			}
			catch ( IOException e )
			{
				System.out.println(xmlFile + " load failed, an empty Configuration returned.");
				e.printStackTrace();
				return new XMLConfiguration();
			}
			catch ( ConfigurationException e )
			{
				System.out.println(xmlFile + " load failed, an empty Configuration returned.");
				e.printStackTrace();
				return new XMLConfiguration();
			}
			catch ( URISyntaxException e )
			{
				System.out.println(xmlFile + " load failed, an empty Configuration returned.");
				e.printStackTrace();
				return new XMLConfiguration();
			}
			finally
			{
				IOUtils.closeQuitely(in);
			}
		}
		return conf;
	}
}

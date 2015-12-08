package util.log;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;

import util.ThreadUtils;

public class Log4JUtils
{
	public static Logger getLogger(String klass)
	{
		final Logger log = Logger.getLogger(klass);
		if ( !Logger.getRootLogger().getAllAppenders().hasMoreElements() )
		{
			LogLog.debug("initailize log4j");
			BasicConfigurator.configure();
			Logger.getRootLogger().removeAllAppenders();
			Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("[%d{yyyy/MM/dd HH:mm:ss}|%-4p %c{2}] %m%n")));
			Logger.getRootLogger().setLevel(Level.ALL);
		}
		return log;
	}

	public static Logger getLogger(Class klass)
	{
		return getLogger(klass.getName());
	}

	public static Logger getClassnameLogger()
	{
		return getLogger(ThreadUtils.getCallerClassName());
	}
}
;
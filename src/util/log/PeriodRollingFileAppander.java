package util.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class PeriodRollingFileAppander extends FileAppender
{
	private String directory = "log";
	private String datePattern = "yyyy-MM-dd";
	private String prefix = "";
	private String suffix = ".log";
	private long period = 60 * 24; // default one day
	private long nextRollTime;
	private SimpleDateFormat formater;

	public PeriodRollingFileAppander()
	{
		setDatePattern(datePattern);
	}

	public PeriodRollingFileAppander(String directory, String datePattern, String prefix, String suffix, long period)
	{
		this.directory = directory;
		this.prefix = prefix;
		this.suffix = suffix;
		this.period = period; // unit in minute
		setDatePattern(datePattern);
		activateOptions();
	}

	public String getDirectory()
	{
		return directory;
	}

	public void setDirectory(String directory)
	{
		this.directory = directory;
	}

	public String getDatePattern()
	{
		return datePattern;
	}

	public void setDatePattern(String datePattern)
	{
		this.datePattern = datePattern;
		this.formater = new SimpleDateFormat(datePattern);
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public long getPeriod()
	{
		return period;
	}

	public void setPeriod(long period)
	{
		this.period = period;
	}

	public void activateOptions()
	{
		long now = System.currentTimeMillis();
		nextRollTime = now + period * 60 * 1000;
		closeFile();
		StringBuilder sb = new StringBuilder();
		sb.append(directory).append('/');
		sb.append(prefix).append(formater.format(new Date(now))).append(suffix);
		File file = new File(sb.toString());
		file.getParentFile().mkdirs();
		this.fileName = file.getAbsolutePath();
		try
		{
			FileOutputStream out = new FileOutputStream(file, true);
			setQWForFiles(new BufferedWriter(createWriter(out)));
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	public void append(LoggingEvent event)
	{
		long now = System.currentTimeMillis();
		if ( now >= nextRollTime )
		{
			activateOptions();
		}
		subAppend(event);
	}
}
;
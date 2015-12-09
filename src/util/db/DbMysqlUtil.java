
package util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

public class DbMysqlUtil extends DbBase
{
	private Logger log;
	private String className;
	private String address;
	private String port;
	private String account;
	private String passwd;
	private String databaseName;
	
	public DbMysqlUtil(Configuration conf){
		this.className =conf.getString("db.className");
		this.address = conf.getString("db.address");
		this.port = conf.getString("db.port");	
		this.account = conf.getString("db.account");	
		this.passwd = conf.getString("db.passwd");	
		this.databaseName = conf.getString("db.databaseName");
	}
	
	public DbMysqlUtil(Logger log)
	{
		this.log = log;
	}

	public Connection getConnection() throws SQLException
	{
		Connection conn = null;
		try
		{
			Class.forName(className);

			String stJDBC = "jdbc:mysql://" + address + ":" + port + "/" + databaseName+"?useUnicode=true&characterEncoding=utf8";
//			conn = DriverManager.getConnection(url)
			conn = DriverManager.getConnection(stJDBC, account, passwd);
			setConn(conn);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return conn;
	}
}

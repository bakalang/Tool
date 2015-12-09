package util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.dbutils.handlers.MapListHandler;

public class DbBase
{
	public Connection conn = null;
	
	public Connection getConn()
	{
		return conn;
	}

	public void setConn(Connection conn)
	{
		this.conn = conn;
	}

	public void doInsert(String stSQL) throws SQLException
	{
		this.insert(stSQL, null);
	}

	public void doInsert(String stSQL, Object[] params) throws SQLException
	{
		ArrayList<Object[]> arrayParams = new ArrayList<Object[]>();
		arrayParams.add(params);
		this.insert(stSQL, arrayParams);
	}

	public void doInsert(String stSQL, ArrayList<Object[]> arrayParams) throws SQLException
	{
		this.insert(stSQL, arrayParams);
	}

	public int doUpdate(String stSQL) throws SQLException
	{
		return this.update(stSQL, null);
	}

	public int doUpdate(String stSQL, Object[] params) throws SQLException
	{
		return this.update(stSQL, params);
	}

	public void doUpdates(ArrayList<String> arraySQL) throws SQLException
	{
		this.updates(arraySQL, null);
	}

	public void doUpdates(ArrayList<String> arraySQL, ArrayList<Object[]> mutiParams) throws SQLException
	{
		this.updates(arraySQL, mutiParams);
	}

	/**
	 * Execute an SQL INSERT.
	 * 
	 * @param sql The SQL to execute.
	 * @param params The insert replacement parameters.
	 * @throws SQLException if a database access error occurs
	 */
	private void insert(String stSQL, ArrayList<Object[]> arrayParams) throws SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		Object[] params = null;
		try
		{
			conn = getConn();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(stSQL);
			if ( arrayParams == null )
			{
				stmt.executeUpdate();
			}
			else
			{
				for ( int i = 0; i < arrayParams.size(); i++ )
				{
					params = arrayParams.get(i);
					this.fillStatement(stmt, params);
					stmt.executeUpdate();
				}
			}

			conn.commit();
		}
		catch ( SQLException e )
		{
			try
			{
				conn.rollback();
			}
			catch ( Exception ee )
			{
				ee.printStackTrace();
			}
			throw e;
		}
		finally
		{
			try
			{
				stmt.close();
			}
			catch ( Exception e )
			{}
			try
			{
				conn.setAutoCommit(true);
			}
			catch ( SQLException e )
			{
				e.printStackTrace();
			}
			try
			{
				conn.close();
			}
			catch ( SQLException ee )
			{
				ee.printStackTrace();
			}
		}
	}

	/**
	 * Execute an SQL UPDATE, or DELETE.
	 * 
	 * @param sql The SQL to execute.
	 * @param params The query replacement parameters.
	 * @return The number of rows updated.
	 * @throws SQLException if a database access error occurs
	 */
	private int update(String stSQL, Object[] params) throws SQLException
	{
		int rows = 0;
		Connection conn = null;
		PreparedStatement stmt = null;
		try
		{
			conn = getConn();
			stmt = conn.prepareStatement(stSQL);
			this.fillStatement(stmt, params);
			rows = stmt.executeUpdate();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
			this.rethrow(e, stSQL, params);
		}
		finally
		{
			try
			{
				stmt.close();
			}
			catch ( Exception e )
			{}
			try
			{
				conn.close();
			}
			catch ( Exception e )
			{}
		}

		return rows;
	}

	/**
	 * Execute an SQL UPDATE, or DELETE.
	 * 
	 * @param arraySQL The SQLs to execute.
	 * @param multiParams The query replacement parameters.
	 * @throws SQLException if a database access error occurs
	 */
	private void updates(ArrayList<String> arraySQL, ArrayList<Object[]> mutiParams) throws SQLException
	{
		Connection conn = null;
		try
		{
			conn = getConn();
			conn.setAutoCommit(false);

			for ( int i = 0; i < arraySQL.size(); i++ )
			{
				String stSQL = arraySQL.get(i);
				PreparedStatement stmt = conn.prepareStatement(stSQL);
				if ( mutiParams != null )
				{
					Object[] params = mutiParams.get(i);
					if ( params != null && params.length > 0 )
					{
						this.fillStatement(stmt, params);
					}
				}
				stmt.close();
			}

			conn.commit();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
			try
			{
				conn.rollback();
			}
			catch ( Exception ee )
			{
				ee.printStackTrace();
			}
			throw e;
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch ( SQLException e )
			{
				e.printStackTrace();
			}
			try
			{
				conn.close();
			}
			catch ( SQLException ee )
			{
				ee.printStackTrace();	
			}
		}
	}

	public Object doQuery(String stSQL) throws SQLException
	{
		return this.query(stSQL, null);
	}

	public Object doQuery(String stSQL, Object[] params) throws SQLException
	{
		return this.query(stSQL, params);
	}

	public ArrayList<Object[]> doQuery(String stSQL, String[] stColumns) throws SQLException
	{
		ArrayList<?> resultList = (ArrayList<?>)this.query(stSQL, null);

		ArrayList<Object[]> arrayResult = new ArrayList<Object[]>();
		for ( int i = 0; i < resultList.size(); i++ )
		{
			HashMap<?, ?> data = (HashMap<?, ?>)resultList.get(i);
			Object[] oValues = new Object[stColumns.length];
			for ( int j = 0; j < stColumns.length; j++ )
			{
				oValues[j] = data.get(stColumns[j]);
			}
			arrayResult.add(oValues);
		}

		return arrayResult;
	}

	public ArrayList<Object[]> doQuery(String stSQL, Object param, String[] stColumns) throws SQLException
	{
		ArrayList<?> resultList = (ArrayList<?>)this.query(stSQL, new Object[]{param});

		ArrayList<Object[]> arrayResult = new ArrayList<Object[]>();
		for ( int i = 0; i < resultList.size(); i++ )
		{
			HashMap<?, ?> data = (HashMap<?, ?>)resultList.get(i);
			Object[] oValues = new Object[stColumns.length];
			for ( int j = 0; j < stColumns.length; j++ )
			{
				oValues[j] = data.get(stColumns[j]);
			}
			arrayResult.add(oValues);
		}

		return arrayResult;
	}

	public ArrayList<Object[]> doQuery(String stSQL, Object[] params, String[] stColumns) throws SQLException
	{
		ArrayList<?> resultList = (ArrayList<?>)this.query(stSQL, params);

		ArrayList<Object[]> arrayResult = new ArrayList<Object[]>();
		for ( int i = 0; i < resultList.size(); i++ )
		{
			HashMap<?, ?> data = (HashMap<?, ?>)resultList.get(i);
			Object[] oValues = new Object[stColumns.length];
			for ( int j = 0; j < stColumns.length; j++ )
			{
				oValues[j] = data.get(stColumns[j]);
			}
			arrayResult.add(oValues);
		}

		return arrayResult;
	}

	private Object query(String stSQL, Object[] params) throws SQLException
	{
		Object result = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		try
		{
			conn = getConn();	
			stmt = conn.prepareStatement(stSQL);
			this.fillStatement(stmt, params);
			ResultSet rs = stmt.executeQuery();
			MapListHandler rsh = new MapListHandler();
			result = rsh.handle(rs);
			rs.close();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
			this.rethrow(e, stSQL, params);
		}
		finally
		{
			try
			{
				stmt.close();
			}
			catch ( Exception e )
			{}
			try
			{
				conn.close();
			}
			catch ( Exception e )
			{}
		}

		return result;
	}

	/**
	 * get connection
	 * 
	 * @return Connection
	 * @throws SQLException
	 */
	

	// public Connection getConnection() throws SQLException{
	// Connection conn = null;
	// try{
	// Map<String, String> env = System.getenv();
	// String stDbAddress = env.get("BATCH_DB_SERVER_ADDRESS");
	// String stDbPort = env.get("BATCH_DB_SERVER_PORT");
	// String stDbAccount = env.get("BATCH_DB_SERVER_ACCOUNT");
	// String stDbPassword = env.get("BATCH_DB_SERVER_PASSWORD");
	// String stDbName = env.get("BATCH_DB_SERVER_DATABASE_NAME");
	//
	// Class.forName("org.postgresql.Driver");
	// String stJDBC = "jdbc:postgresql://"+stDbAddress+":"+stDbPort+"/"+stDbName;
	// conn = DriverManager.getConnection(stJDBC, stDbAccount, stDbPassword);
	// }
	// catch(ClassNotFoundException e){
	// log.error("getConnection", e);
	// }
	//
	// return conn;
	// }

	/**
	 * Fill the <code>PreparedStatement</code> replacement parameters with the given objects.
	 * 
	 * @param stmt PreparedStatement to fill
	 * @param params Query replacement parameters; <code>null</code> is a valid value to pass in.
	 * @throws SQLException if a database access error occurs
	 */
	private void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException
	{
		if ( params == null || params.length == 0 )
		{
			return;
		}

		for ( int i = 0; i < params.length; i++ )
		{
			if ( params[i] == null )
			{
				// VARCHAR works with many drivers regardless
				// of the actual column type. Oddly, NULL and
				// OTHER don't work with Oracle's drivers.
				stmt.setNull(i + 1, Types.VARCHAR);
			}
			else
			{
				if ( params[i] instanceof java.lang.String )
				{
					stmt.setString(i + 1, params[i].toString());
				}
				else if ( params[i] instanceof java.sql.Timestamp )
				{
					stmt.setTimestamp(i + 1, (java.sql.Timestamp)params[i]);
				}
				else if ( params[i] instanceof java.sql.Date )
				{
					stmt.setDate(i + 1, (java.sql.Date)params[i]);
				}
				else if ( params[i] instanceof java.util.Date )
				{
					stmt.setDate(i + 1, new java.sql.Date(( (java.util.Date)( params[i] ) ).getTime()));
				}
				else if ( params[i] instanceof java.math.BigDecimal )
				{
					stmt.setBigDecimal(i + 1, (java.math.BigDecimal)params[i]);
				}
				else if ( params[i] instanceof java.lang.Integer )
				{
					stmt.setInt(i + 1, ( (java.lang.Integer)params[i] ).intValue());
				}
				else if ( params[i] instanceof java.lang.Float )
				{
					stmt.setFloat(i + 1, ( (java.lang.Float)params[i] ).floatValue());
				}
				else if ( params[i] instanceof java.lang.Long )
				{
					stmt.setLong(i + 1, ( (java.lang.Long)params[i] ).longValue());
				}
				else if ( params[i] instanceof java.lang.Double )
				{
					stmt.setDouble(i + 1, ( (java.lang.Double)params[i] ).doubleValue());
				}
				else if ( params[i] instanceof java.lang.Boolean )
				{
					stmt.setBoolean(i + 1, ( (java.lang.Boolean)params[i] ).booleanValue());
				}
				else
				{
					stmt.setObject(i + 1, params[i]);
				}
			}
		}
	}

	/**
	 * Throws a new exception with a more informative error message.
	 * 
	 * @param cause The original exception that will be chained to the new exception when it's rethrown.
	 * @param sql The query that was executing when the exception happened.
	 * @param params The query replacement parameters; <code>null</code> is a valid value to pass in.
	 * @throws SQLException if a database access error occurs
	 */
	private void rethrow(SQLException cause, String sql, Object[] params) throws SQLException
	{
		StringBuffer msg = new StringBuffer(cause.getMessage());
		msg.append("Query: ");
		msg.append(sql);
		if ( params != null && params.length > 0 )
		{
			msg.append("; Parameters: ");
			msg.append(Arrays.asList(params));
		}

		SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
		e.setNextException(cause);
		throw e;
	}

	public String checkEscapeCharacter(String string)
	{
		if ( string == null || string.length() == 0 )
		{
			return "NULL";
		}
		else
		{
			if ( string.indexOf("'") >= 0 )
			{
				string = string.replaceAll("'", "''");
			}

			return "'" + string + "'";
		}
	}
}

/**
 * 
 */
package votingSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Spenser, Casey
 *
 */
public class SystemDatabaseTable {
	@SuppressWarnings("unused")
	private String username;
	@SuppressWarnings("unused")
	private String password;
	@SuppressWarnings("unused")
	private String serverName;
	@SuppressWarnings("unused")
	private int portNumber;
	@SuppressWarnings("unused")
	private String databaseName;
	private String tableName;
	private Connection connection;
	/**
	 * Constructor of the SystemDatabaseTable.
	 * @param username The username of the system
	 * @param password The password of the system
	 * @param serverName The server name
	 * @param portNumber the port number
	 * @param databaseName The name of the database
	 * @param tableName The name of the table
	 * @param tableParams The fields contained within the table
	 */
	public SystemDatabaseTable(String username, String password, String serverName, int portNumber, String databaseName, String tableName, 
			String tableParams, Connection connection)
	{
		this.username = username;
		this.password = password;
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.databaseName = databaseName;
		this.tableName = tableName;
		
		this.connection = connection;
		
		//execute command to create table
		createTable(this.connection, tableName, tableParams);
	}
	/**
	 * Tests if the table's name matches the name provided.
	 * @param tableName The name to be tested
	 * @return True if there is a match
	 */
	public boolean equals(String tableName)
	{
		return this.tableName.equals(tableName);
	}
	/**
	 * Tests if the tables share the same name.
	 * @param table The table to be tested
	 * @return If the table names match
	 */
	public boolean equals(SystemDatabaseTable table)
	{
		String tableName = table.getName();
		return tableName.equals(this.tableName);
	}
	/**
	 * Creates the table.
	 * @param conn The connection for the table
	 * @param tableName The name of the table
	 * @param tableParams The fields the table will contain
	 * @return True if successful
	 */
	public boolean createTable(Connection conn, String tableName, String tableParams)
	{
		try {
			return executeUpdate(conn, "create table " + tableName + " " + tableParams);
		} catch (SQLException e) {
			return false;
		}
	}
	/**
	 * Drops the table.
	 * @return True if successful
	 */
	public boolean drop()
	{
		try {
		    String dropString = "DROP TABLE " + this.tableName;
			this.executeUpdate(this.connection, dropString);
			return true;
	    } catch (SQLException e) {
			return false;
		}
	}
	/**
	 * Executes a database update.
	 * @param conn The connection for the update
	 * @param command The command to be run
	 * @return True if successful
	 * @throws SQLException
	 */
	private boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	/**
	 * Method for calling commands.
	 * @param command
	 * @return
	 * @throws SQLException
	 */
	public boolean executeUpdate(String command) throws SQLException {
	    return executeUpdate(this.connection, command);
	}
	/**
	 * Gets the name of the table.
	 * @return A String containing the name of the table
	 */
	public String getName()
	{
		return tableName;
	}
	/**
	 * Gets the ResultSet of a Query.
	 * @param columnNames The name(s) of the column (ex: *, fname, etc.)
	 * @return The ResultSet of the query
	 */
	public ResultSet getResultSet(String columnNames)
	{
		Statement stmt;
		String query = "select " + columnNames + " from " + this.tableName;
		try {
			stmt = this.connection.createStatement();
			return stmt.executeQuery(query);
		} catch (SQLException e) {
		}
		return null;
	}
	/**
	 * Gets the ResultSet of a Query.
	 * Acts as a more specialized version of executeQuery
	 * @param columnNames The name(s) of the column (ex: *, fname, etc.)
	 * @param constraints The constraint(s) of a query (ex: id = 5, fname = donald, etc.)
	 * @return The ResultSet of the query
	 */
	public ResultSet getResultSet(String columnNames, String constraints)
	{
		String query = "select " + columnNames + " from " + this.tableName 
				+ " where " + constraints;
		return executeQuery(createStatement(), query);
	}
	/**
	 * Creates a result set based on a query.
	 * @param statement The statement to be used
	 * @param query The query to be used
	 * @return A ResultSet based on the query, or null if it failed
	 */
	public ResultSet executeQuery(Statement statement, String query)
	{
		try {
			return statement.executeQuery(query);
		} catch (SQLException e) {
			return null;
		}
	}
	/**
	 * Creates a statement.
	 * @return The statement created (null if failed)
	 */
	public Statement createStatement()
	{
		try {
			return this.connection.createStatement();
		} catch (SQLException e) {
			return null;
		}
	}
	/**
	 * Creates a statement.
	 * @param resultSetType The result set type
	 * @param resultSetConcurrency The result set concurrency
	 * @return The statement created (null if failed)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
	{
		try {
			return this.connection.createStatement(resultSetType, resultSetConcurrency);
		} catch (SQLException e) {
			return null;
		}
	}
	/**
	 * Creates a statement.
	 * @param resultSetType The result set type
	 * @param resultSetConcurrency The result set concurrency
	 * @param resultSetHoldability The result set holdability
	 * @return The statement created (null if failed)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, 
			int resultSetHoldability)
	{
		try {
			return this.connection.createStatement(resultSetType, resultSetConcurrency, 
					resultSetHoldability);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Changes the username.
	 * @param username The new username
	 */
	public void setUsername(String username) {
		this.username = username;	
	}
	/**
	 * Changes the password.
	 * @param password The new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Changes the database name.
	 * @param databaseName The new database name
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	/**
	 * Changes the port number.
	 * @param portNumber The new port number
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * Changes the server name.
	 * @param serverName The new server name
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * Changes the connection.
	 * @param connection The new connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}

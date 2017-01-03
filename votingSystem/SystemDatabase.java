/**
 * 
 */
package votingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Spenser, Casey
 *
 */
public class SystemDatabase {
    
	private ArrayList<SystemDatabaseTable> tables;
	//following 3 ints work under assumption that tables have been added in this order
	private static final int VOTER_TABLE_INDEX = 0;
	
	public static final int VOTER_VALID = 0x10; //found in table
	public static final int VOTER_INVALID = 0x11; //not found in table
	public static final int VOTER_HAS_VOTED = 0x12; //already voted
	public static final int VOTER_CHECK_FAILURE = 0x13; //Failed to check
	
	private static int newCandidateID = 0x100;
        private static String NO_CANDIDATES_TALLY_ERROR = "** No Candidates Found **";
        private static String TALLY_FAILURE_ERROR = "** Failed to Read Table";

	private String username;
	private String password;
	private String serverName;
	private int portNumber;
	private String databaseName;
	private boolean createDatabaseSuccess;
	private Connection connection;
        
        public static int votersThatVoted;
        public static int castedBallots;
        
        
        
    public static void main(String[] args) {
    }
	/**
	 * Creates the database. System.out.println(
	 * @param username
	 * @param password
	 * @param serverName
	 * @param portNumber
	 * @param databaseName
	 */
	public SystemDatabase(String username, String password, String serverName, int portNumber, String databaseName)
	{
		this.username = username;
		this.password = password;
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.databaseName = databaseName;
                
               votersThatVoted = 0;
               castedBallots = 0;
               
		//this.createDatabaseSuccess = createDatabase(); //database needs to be created to execute command, command creates duplicate database
		this.tables = new ArrayList<SystemDatabaseTable>();
		try {
			this.connection = createConnection();
		} catch (SQLException e) {
		}
		useDatabase();
	}
     /**
     * Checks if the number of votes equals the number of people who voted.
     * @return True if it is equal, false if it isn't
     */
    public static boolean verifyVoteCount()
	{
		SystemDatabaseTable voters = VotingSystem.database.getTable(VotingSystem.VOTER_TABLE); 
		SystemDatabaseTable candidates = VotingSystem.database.getTable(VotingSystem.CAND_TABLE);
		ResultSet voterResults = voters.getResultSet("hasVoted", "hasVoted = 1");
		ResultSet candidatesResults = candidates.getResultSet("tally");
		try {
			int voterRowCount = 0;
			int tallyCount = 0;
			//  3 candidates per Voter, absentee votes are counted
			while(voterResults.next())
				voterRowCount++;
			while(candidatesResults.next())
				tallyCount+= candidatesResults.getInt(1);
            votersThatVoted = voterRowCount;
            castedBallots = tallyCount/3;
			return voterRowCount == tallyCount/3;
		} catch (SQLException e) {
                return false;
		}
	}
    
    
     /**
     * Gets the unofficial tally.
     * @return Returns tally in following format: [first name] [last name] - [tally]
     */
    public static String getUnofficialTally()
    {
    	String unofficialTally = SystemDatabase.TALLY_FAILURE_ERROR;
    	SystemDatabaseTable candidates = VotingSystem.database.getTable(VotingSystem.CAND_TABLE);
    	ResultSet results = candidates.getResultSet("fname, lname, tally");
    	try {
			if (results.next())
				unofficialTally = results.getString(1) + " " + results.getString(2) + " - "
					+ results.getInt(3);
			else
				return SystemDatabase.NO_CANDIDATES_TALLY_ERROR;
			while (results.next())
	    		unofficialTally = unofficialTally + "\n" + results.getString(1) + " " 
	    				+ results.getString(2) + " - " + results.getInt(3);
		} catch (SQLException e) {
		}
    	return unofficialTally;
    }
	/**
	 * Adds a table to the database.
	 * @param tableName Name of table to be added
	 */
	public int addTable(String tableName, String tableParams)
	{
		for (int i = 0; i < tables.size(); i++)
		{
			SystemDatabaseTable table = tables.get(i);
			if (table.equals(tableName))
			{
				System.out.printf("Table %s already exists!", tableName);
				return tables.size();
			}
		}
		SystemDatabaseTable dbTable = new SystemDatabaseTable(this.username, this.password, this.serverName, this.portNumber, 
				this.databaseName, tableName, tableParams, this.connection);
                //add table to ArrayList<>
                    tables.add(dbTable);
                    return tables.size() - 1;
                
	}
	/**
	 * Creates a new database connection
	 * 
	 * @return The connection
	 * @throws SQLException
	 */
	private Connection createConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.username);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection("jdbc:mysql://"
				+ this.serverName + ":" + this.portNumber + "/" + this.databaseName,
				connectionProps);

		return conn;
	}
	/**
	 * Switches use to this database.
	 * @return True if successful
	 */
	public boolean useDatabase()
	{

		Properties connectionProps = new Properties();
		connectionProps.put("user", this.username);
		connectionProps.put("password", this.password);
		String command = "use " + databaseName;

		
		Statement stmt = null;
	    try 
	    {
	        stmt = this.connection.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails 
	        if (stmt != null) { stmt.close(); }
	        return true;
	    } catch (SQLException e) 
	    {
	    	return false;
	    }
	}
	/**
	 * Determines whether the database is valid.
	 * Database is valid if it was successfully created.
	 * @return True if database is valid, false otherwise
	 */
	public boolean databaseValid()
	{
		return this.createDatabaseSuccess;
	}
	/**
	 * Drops all the tables in a database.
	 * @return True if the operation was successful, false otherwise
	 */
	public boolean dropTables()
	{
		while(!tables.isEmpty())
			(tables.remove(0)).drop();
		return true;
	}
	/**
	 * Returns the list of tables.
	 * @return An ArrayList containing the tables
	 */
	public ArrayList<SystemDatabaseTable> getTableList()
	{
		return tables;
	}
	/**
	 * Returns a table based on its name.
	 * @param tableName The name of the table
	 * @return The requested table or null if the table is invalid
	 */
	public SystemDatabaseTable getTable(String tableName)
	{
		for (int i = 0; i < tables.size(); i++)
		{
			SystemDatabaseTable sdt = tables.get(i);
			if (sdt.equals(tableName))
				return sdt;
		}
		return null;
	}
	/**
	 * Gets a table based on its index.
	 * @param index The index of the table
	 * @return A table or a null pointer if the index value was invalid
	 */
	public SystemDatabaseTable getTable(int index)
	{
		if (index < 0 || index >= tables.size())
			return null;
		return tables.get(index);
	}
     /**
      * Finds if a table exists.
      * @param tableName The name of the table
      * @return True if it exists, false otherwise
      */
    public boolean exists(String tableName) {
            
           int size = tables.size();
           int i = 0;
           while(size < tables.size()) {
               
                if(tables.get(i).equals(tableName)) {
                    return false;
                }
           }
             return true;  
        }
    /**
     * Checks if a voter is valid or not. Returns an int signifying this.
     * Example check: if (checkVoterValidity(3) == SystemDatabase.VOTER_VALID) allowVoting();
     * @param id The ID value of the voter
     * @return An int signifying the results
     */
    public int checkVoterValidity(String id)
    {
    	SystemDatabaseTable voters = tables.get(SystemDatabase.VOTER_TABLE_INDEX);
    	ResultSet rs = voters.getResultSet("hasVoted", "id = \"" + id + "\"");
    	try {
			if (!rs.next())
				return SystemDatabase.VOTER_INVALID;
			if (rs.getInt(1) != 0)
	    		return SystemDatabase.VOTER_HAS_VOTED;
	    	return SystemDatabase.VOTER_VALID;
		} catch (SQLException e) {
		}
    	return SystemDatabase.VOTER_CHECK_FAILURE;
    }
    /**
     * Counts a write in candidate.
     * @param fname The candidate's first name
     * @param lname The candidate's last name
     * @param chair The office the candidate is running for
     */
    public void tallyWriteIn(String fname, String lname, String chair)
	{
            Statement statement;
            ResultSet rs;
            try {
                statement = this.connection.createStatement();
                String columns = "ID";
		String constraints = "fname = \"" + fname + "\" and lname = \"" + lname +"\"" 
                + " and chair = \"" + chair + "\"";
                String query = "select " + columns + " from " 
                        + VotingSystem.CAND_TABLE + " where " + constraints;
		rs = statement.executeQuery(query);
            } catch (SQLException e) {
                return;
            }
                
		try {
			if (!rs.next())
				addCandidate(fname, lname, chair);
			else
			{
				String id = rs.getString("ID");
				String command = "update " + VotingSystem.CAND_TABLE
						+ " set tally = tally + 1 where id = \"" + id + "\"";
				executeUpdate(command);
			}
		} catch (SQLException e) {
		}
			
	}
    /**
     * Adds candidate if he/she does not already exist.
     * @param fname The first name of the candidate
     * @param lname The last name of the candidate
     * @param chair The office the candidate is running for.
     */
    private void addCandidate(String fname, String lname, String chair)
    {
    	String id = "\"" + (newCandidateID++) + "\"";
    	String command = "INSERT INTO " + VotingSystem.CAND_TABLE
              + " (ID, fName, lName, chair, party, tally) " 
              + "VALUES "
              + "(" + id +"," + "'" + fname  + "','" 
              + lname + "','" + chair + "','" + "N/A" 
              + "','" + 1 + "')";
    	try {
			executeUpdate(command);
		} catch (SQLException e) {
		}
    }
    /**
	 * Marks a voter as having voted.
	 * @param voterID The ID of the voter
	 */
	public void markVoterAsVoted(String voterID)
	{
		String command = "update " + VotingSystem.VOTER_TABLE
				+ " set hasVoted = 1 where id = \"" + voterID + "\"";
		try {
			executeUpdate(command);
		} catch (SQLException e) {
		}
		
	}
	/**
	 * Tallies a candidate.
	 * @param id The id of the candidate.
	 */
	public void tallyCandidate(String id)
	{
		String command = "update " + VotingSystem.CAND_TABLE 
				+ " set tally = tally + 1 where id = \"" + id +"\"";
		try {
			executeUpdate(command);
		} catch (SQLException e) {
		}
	}
    /**
     * Changes the username.
     * @param username The new username
     */
    private void setUsername(String username)
    {
    	this.username = username;
    	for (int i = 0; i < tables.size(); i++)
    		(tables.get(i)).setUsername(username);
    }
    /**
     * Resests the username.
     * @param username The new username
     */
    public void resetUsername(String username)
    {
    	setUsername(username);
    	resetConnection();
    }
    /**
     * Changes the password.
     * @param password The new password
     */
    private void setPassword(String password)
    {
    	this.password = password;
    	for (int i = 0; i < tables.size(); i++)
    		(tables.get(i)).setPassword(password);
    }
    /**
     * Resets the password.
     * @param password The new password
     */
    public void resetPassword(String password)
    {
    	setPassword(password);
    	resetConnection();
    }
	/**
	 * Changes the server name.
	 * @param serverName The new server name
	 */
    private void setServerName(String serverName)
    {
    	this.serverName = serverName;
    	for (int i = 0; i < tables.size(); i++)
    		(tables.get(i)).setServerName(serverName);
    }
    /**
     * Resets the server name.
     * @param serverName The new server name
     */
    public void resetServerName(String serverName)
    {
    	setServerName(serverName);
    	resetConnection();
    }
	/**
	 * Changes the port number.
	 * @param portNumber The new port number
	 */
    private void setPortNumber(int portNumber)
    {
    	this.portNumber = portNumber;
    	for (int i = 0; i < tables.size(); i++)
    		(tables.get(i)).setPortNumber(portNumber);
    }
    /**
     * Resets the port number.
     * @param portNumber The new port number
     */
    public void resetPortNumber(int portNumber)
    {
    	setPortNumber(portNumber);
    	resetConnection();
    }
	/**
	 * Changes the database name;
	 * @param databaseName The name of the new database
	 */
    private void setDatabaseName(String databaseName)
    {
    	this.databaseName = databaseName;
    	for (int i = 0; i < tables.size(); i++)
    		(tables.get(i)).setDatabaseName(databaseName);
    }
    /**
     * Resets the database name.
     * @param databaseName The new database name
     */
    public void resetDatabaseName(String databaseName)
    {
    	setDatabaseName(databaseName);
    	resetConnection();
    }
	/**
	 * Resets the connection.
	 * Useful if information such as username or port number has changed.
	 */
	public void resetConnection()
	{
		try {
			this.connection = createConnection();
			for (int i = 0; i < tables.size(); i++)
				(tables.get(i)).setConnection(this.connection);
		} catch (SQLException e) {
			System.out.println("FAILED TO RESET CONNECTION!");
			e.printStackTrace();
		}
	}
	/**
	 * Moves to a new database.
	 * Useful for accessing backup databases.
	 * This works under the assumption that the backup has the same table names.
	 * @param username The username of the database
	 * @param password The password of the database
	 * @param serverName The server name of the database
	 * @param portNumber The port number of the database
	 * @param DatabaseName The name of the database
	 */
	public void migrateDatabase(String username, String password, String serverName, 
			int portNumber, String databaseName)
	{
		setUsername(username);
		setPassword(password);
		setServerName(serverName);
		setPortNumber(portNumber);
		setDatabaseName(databaseName);
		resetConnection();
	}
	
	/**
	 * Executes a database update.
	 * @param command The command to be run
	 * @return If the update was successful
	 * @throws SQLException
	 */
	public boolean executeUpdate(String command) throws SQLException {
	    return executeUpdate(this.connection, command);
	}
	/**
	 * Executes a database update.
	 * @param conn The database connection
	 * @param command The command to be done
	 * @return If the update was successful
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
     * Gets the unofficial tally.
     * @return Returns tally in following format: [first name] [last name] running for [chair] - [tally]
     */
    public static String getUnofficialTally2()
    {
    	String unofficialTally= "";
        SystemDatabaseTable candidates = VotingSystem.database.getTable(VotingSystem.CAND_TABLE);
    	ResultSet results = candidates.getResultSet("fname, lname, chair, tally");
    	try {
			if (results.next())
				unofficialTally = results.getString(2) + ", " + results.getString(1) + "-"
					+ results.getString(3) + ": " + results.getInt(4);
			else
				return SystemDatabase.NO_CANDIDATES_TALLY_ERROR;
			while (results.next())
	    		unofficialTally = unofficialTally + "\n" + results.getString(2) + ", " 
	    				+ results.getString(1) + "-" + results.getString(3)
	    				+ ": " + results.getInt(4); 
		} catch (SQLException e) {
		}
    	return unofficialTally;
    }
        
}

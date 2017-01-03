/**
 * 
 */
package votingSystem;

import Utils.Admin;
import Utils.CandUtil;
import java.sql.SQLException;
import Utils.Voters;
import java.util.ArrayList;
import votingGUI.*;

/**
 * @author Spenser, Casey
 *
 */
public class VotingSystem {
    public final static String dbURL = "jdbc:mysql://localhost:3306/systemdatabase";
	public final static String USERNAME = "root";
	public final static String PASSWORD = "root"; //change to something more secure later
	public final static String SERVER_NAME = "localhost";
	public final static int PORT_NUMBER = 3306;
	public final static String DATABASE_NAME = "SystemDatabase";
	public final static String VOTER_TABLE = "Voters";
    public final static String CAND_TABLE = "Candidates";
	public final static String ADMIN_TABLE = "Administrators";
	public final static String RESULTS_TABLE = "Results";
    public static final String CAND_TABLE_PARAMETERS = "(ID VARCHAR(10) NOT NULL PRIMARY KEY,"
                                                                + " fName VARCHAR(15) NOT NULL,"
                                                                + " lName VARCHAR(15) NOT NULL,"
                                                                + " chair VARCHAR(4) NOT NULL,"
                                                                + " party VARCHAR(3) NOT NULL,"
                                                                + " tally INT UNSIGNED )";
	private static final String VOTER_TABLE_PARAMETERS = "(ID VARCHAR(10) NOT NULL PRIMARY KEY,"
                                                                + " fName VARCHAR(15) NOT NULL,"
                                                                + " lName VARCHAR(15) NOT NULL,"
                                                                + " hasVoted INTEGER NOT NULL)";
	private static final String ADMIN_TABLE_PARAMETERS = "(ID VARCHAR(10) NOT NULL PRIMARY KEY,"
                                                                + " fName VARCHAR(15) NOT NULL,"
                                                                + " lName VARCHAR(15) NOT NULL )";
	private static final String RESULTS_TABLE_PARAMETERS = "(ID VARCHAR(10) NOT NULL, PRIMARY KEY(ID))";
	
	public static SystemDatabase database;
	/**
	 * Constructor for the voting system.
	 * @param username The username of the database
	 * @param password The password of the database
	 * @param serverName The server name of the database
	 * @param portNumber The port number of the database
	 * @param databaseName The name of the database
	 */
	public VotingSystem(String username, String password, String serverName, int portNumber, String databaseName)
	{
		database = new SystemDatabase(username, password, serverName, portNumber, databaseName);
	}
	/**
	 * Gets the database.
	 * @return The database being used
	 */
	public SystemDatabase getDatabase()
	{
		return database;
	}
    /**
     * Populates the voter table.    
     * @param sdt The table being populated
     * @param voterTableIndex The index of the voter table
     */
    public void populateVoterDB(SystemDatabaseTable sdt, int voterTableIndex)
        {
            ArrayList<String> voters = null;
        
            try {
            
                voters = Voters.readFile();
            } catch (Exception e) {
            	return;
            }
            
           sdt = database.getTable(voterTableIndex);
                
            for (int i = 0; i <= voters.size() - 1; i++) {
                    
                try {
                    sdt.executeUpdate(voters.get(i).toString());
                } catch (SQLException e) {
                }
            }
        }
    /**
     * 	Populates the candidate table.
     * @param sdt The table to be populated
     * @param candTableIndex The index of the table
     */
    public void populateCandDB(SystemDatabaseTable sdt, int candTableIndex)
    {
        
        ArrayList<String> cands = null;                
        try {
                    
            cands = CandUtil.readFile();
        } catch (Exception e) {
        }
               
       sdt = database.getTable(candTableIndex);
        for (int i = 0; i <= cands.size() - 1; i++) {
                    
            try {
				sdt.executeUpdate(cands.get(i).toString());
			} catch (SQLException e) {
			}
        }
    }
    /**
     * Populates the administrator table.    
     * @param sdt The table to be populated
     * @param adminTableIndex The index of the table
     */
    public void populateAdminDB(SystemDatabaseTable sdt, int adminTableIndex)
    {
            
        ArrayList<String> admins = null;
        try {
           admins = Admin.readFile();
        } catch (Exception e) {
        }
       sdt = database.getTable(adminTableIndex);
                
        for (int i = 0; i <= admins.size() - 1; i++) {
                    
            try {
                sdt.executeUpdate(admins.get(i).toString());
            } catch (SQLException e) {
			}
        }
    }

	/**
	 * The main method.
	 * @param args Arguments (none will be used)
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		VotingSystem sys = new VotingSystem(USERNAME, PASSWORD, SERVER_NAME, PORT_NUMBER, DATABASE_NAME);
		SystemDatabase database = sys.getDatabase();
               int voterTableIndex = database.addTable(VOTER_TABLE, VOTER_TABLE_PARAMETERS);
               int adminTableIndex = database.addTable(ADMIN_TABLE, ADMIN_TABLE_PARAMETERS);
               int candTableIndex = database.addTable(CAND_TABLE, CAND_TABLE_PARAMETERS);
               database.addTable(RESULTS_TABLE, RESULTS_TABLE_PARAMETERS);
               //populate db with voter, cands, etc. 
               SystemDatabaseTable sdt = null;
               sys.populateAdminDB(sdt, adminTableIndex);
               sys.populateVoterDB(sdt, voterTableIndex);
               sys.populateCandDB(sdt, candTableIndex);
               AdminLogin vg = new AdminLogin(); 
               vg.setVisible(true);
               //database.dropTables(); //Maybe use this when system is closed
	}
}

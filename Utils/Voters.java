/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;
import votingSystem.VotingSystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Casey
 */
public class Voters {

   public static ArrayList<String> readFile() throws Exception {
       //find absolute path to file
       URL rsc = Voters.class.getResource("Voters.txt");
       Paths.get(rsc.toURI()).toFile();
       String filepath = Paths.get(rsc.toURI()).toFile().toString();
       
       //connect buffer to read file
       File file = new File(filepath);
       BufferedReader in = new BufferedReader(new FileReader(file));
       String line = in.readLine();
 
       ArrayList<String> qrys = new ArrayList<String>();
       //read in txt file --> add sqlQuery to query array
       while (line != null) {
           StringTokenizer t = new StringTokenizer(line, ",");
           if (t.countTokens() >= 3) {
                    String ID = t.nextToken();
                    String fName = t.nextToken();
                    String lName = t.nextToken();
                    
                     String query = "INSERT INTO " + VotingSystem.VOTER_TABLE
                             + " (ID, fName, lName, hasVoted) " + "VALUES "
                             + "('" + ID +"'," + "'" + fName 
                             + "','" + lName + "','0'" + ");";
                    qrys.add(query);
   
             }
           
           line = in.readLine();
       }
       in.close();
       return qrys;
   }
   /**
    * Creates a connection to the database.
    * @return A Connection object (null if failed)
    */
   public static Connection connectToDB() {
        String dbURL = VotingSystem.dbURL;
        String username = VotingSystem.USERNAME;
        String password = VotingSystem.PASSWORD;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
        } catch (SQLException e) {
        	conn = null;
        }
        return conn;
        
    }
}

    
    
        



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import votingSystem.VotingSystem;

/**
 *
 * @author Casey
 */
public class CandUtil {


   public static ArrayList<String> readFile() throws Exception {
       //find absolute path to file
       URL rsc = CandUtil.class.getResource("candidates.txt");
       Paths.get(rsc.toURI()).toFile();
       String filepath = Paths.get(rsc.toURI()).toFile().toString();
       
       //connect buffer to read file
       File file = new File(filepath);
       BufferedReader in = new BufferedReader(new FileReader(file));
       String line = in.readLine();
 
       ArrayList<String> qrys = new ArrayList<String>();
       //read in file
       while (line != null) {
           StringTokenizer t = new StringTokenizer(line, ",");
           if (t.countTokens() >= 5) {
                    String ID = t.nextToken();
                    String fName = t.nextToken();
                    String lName = t.nextToken();
                    String chair = t.nextToken();
                    String party = t.nextToken();
                    String tally = t.nextToken();
                    
                     String query = "INSERT INTO " + VotingSystem.CAND_TABLE
                             + " (ID, fName, lName, chair, party, tally) " 
                             + "VALUES "
                             + "('" + ID +"'," + "'" + fName  + "','" 
                             + lName + "','" + chair + "','" + party 
                             + "','" + tally + "');";
                    qrys.add(query);
   
             }
           
           line = in.readLine();
       }
       in.close();
       return qrys;
   }
}

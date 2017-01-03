/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package votingGUI;

import Utils.Voters; 
import java.awt.Color;
import java.awt.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JPanel;
import votingSystem.SystemDatabase;
import votingSystem.VotingSystem;



/**
 *
 * @author Casey
 */
public class VoterForm extends javax.swing.JFrame {
 
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ballotCount;        //tracks # of votes user has selected
    private String president;       //holds id of President selcd by voter
    private String senator;         //holds id of Senator selcd by voter
    private String houseRep;        //holds id of HouseRep. selcd by voter
    private String ballots[];       //holds candidates id's selcd by voter
    private static boolean liveResult_Created = false; 
    private String voterID; 
    
    //Write-in Name Holders
    private static int writeIn_ID;  //tracks write in candidates
    private boolean sentWriteIn;
    private String sentWRT_firstN = "";
    private String sentWRT_lastN = "";
    private boolean houseWriteIn;
    private String housWRT_firstN = "";
    private String housWRT_lastN = "";
    private boolean presWriteIn;
    private String presWRT_firstN = "";
    private String presWRT_lastN = "";
    
    String dbURL;
    String username;
    String password;
    String dbName;
    SystemDatabase database;

    /**
     * Creates new form VoterForm
     */
    public VoterForm() {
        
        initComponents();
        
        // id: 999 signifies no selection
        president = "000";
        senator = "000";
        houseRep = "000";
        writeIn_ID = 900;
        
        ballots = new String[3];
        ballots[0] = "000";
        ballots[1] = "000";
        ballots[2] = "000";
        
        liveResult_Created = false;
        presWriteIn = false;
        houseWriteIn = false;
        sentWriteIn = false;
        
        
        dbURL = "jdbc:mysql://localhost:3306/systemdatabase";
        username = VotingSystem.USERNAME;
        password = VotingSystem.PASSWORD;    
        dbName =   VotingSystem.DATABASE_NAME;
       
        jButton_Confirmation.setBackground(Color.GREEN);
        jButton_Cancel.setBackground(Color.RED);
        
        //disable voting until valid user logs in
        disablePanel(jPanel_President);
        disablePanel(jPanel_Senate);
        disablePanel(jPanel_House);
        disablePanel(jPanel_Confirmation);
        disablePanel(jPanel_Confirmation2);
  
    }
    
    /**
     * disables  all Voting Panels to disallow candidate selections.
     * @param panel 
     */   
    public void disablePanel(JPanel panel) {
        
        Component[] comp = panel.getComponents();            
        for (int i = 0; i < comp.length; i++) {
            comp[i].setEnabled(false);
        }  
       
    }
    
    
    /**
     * enables all Voting Panels to allow candidate selections,
     * @param panel 
     */ 
    public void enablePanel(JPanel panel) {
        
        Component[] comp = panel.getComponents();            
        for (int i = 0; i < comp.length; i++) {
            comp[i].setEnabled(true);
        }    
        
    }

    
    /**
     * sets all voter candidate selections to default values.
     */
    public void resetVoterSelections() {
        
        ballotCount = 0; 
        president = "000";
        senator = "000";
        houseRep = "000";
      
        buttonGroup1.clearSelection();
        buttonGroup2.clearSelection();
        buttonGroup3.clearSelection();
        
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        
        for(int i = 0 ; i < ballots.length; i++) {
            ballots[i] = "000";
        }    
        
        
    }
   
    /**
     * 
     * @return number of candidates that chosen/submitted
     */
    public int getVoterCastCount(){
        return ballotCount;
    }
    
    /**
     * 
     * @return id of candidates selected by voter
     */
    public String[] getBallots() {
        
        return ballots;
        
    }
    
    /**
     * Increments tally count in SystemDatabase DB for specified candidate;
     * @param cand 
     */
    public void castBallot(String cand) {
        
        try {         
            String query = "UPDATE Candidates "
                    + "SET tally = tally + 1 WHERE ID = "
                    + cand;
            
            Connection conn = Voters.connectToDB();
            PreparedStatement ppdStmt = conn.prepareStatement(query);
            ppdStmt.executeUpdate();
            conn.close();        
        }
        catch(SQLException e) {   
        }
    }

/**
 * updates LiveResults.txt file with candidate selections by their ID#
 * @param ballots voters candidate selections
 */
private static void updateLiveResults(String[] ballots) {
     try {
        // open/create file --> append new data
        FileWriter writer = new FileWriter("LiveResults_ENCRYPTED.txt", true);
        FileWriter writer2 = new FileWriter("LiveResuts_DeCRYPTED.txt", true);
        String header = "pres\t" + "sent\t" + "hous\t" + "timestamp\n";
        Calendar cal = new GregorianCalendar();
       
        //header for newly created file
        if (!liveResult_Created) {  
            liveResult_Created = true;
            
            try {
                
                KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
                SecretKey myDesKey = keygenerator.generateKey();
                Cipher desCipher;
                desCipher = Cipher.getInstance("DES");
                byte[] text = header.toUpperCase().toString().getBytes("UTF8");
                desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
                byte[] textEncrypted = desCipher.doFinal(text);
                writer.write(textEncrypted.toString());
                writer2.write(header.toUpperCase().toString());
                
            } catch (Exception e) {
            }
        }
        
        //formating for liveresults file
        for (int i = 0; i < ballots.length; i++) { 
            
            if (i == 2) {
                
                try {
                    
                    writer2.write(ballots[i] + "\t");
                    KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
                    SecretKey myDesKey = keygenerator.generateKey();
                    Cipher desCipher;
                    desCipher = Cipher.getInstance("DES");
                    byte[] text = ballots[i].getBytes("UTF8");
                    desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
                    byte[] textEncrypted = desCipher.doFinal(text);
                    writer.write(textEncrypted.toString());
                
                } catch (Exception e) {
                }
                
            }
            else {   
                try{
                    
                    
                    KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
                    SecretKey myDesKey = keygenerator.generateKey();
                    Cipher desCipher;
                    desCipher = Cipher.getInstance("DES");
                    byte[] text = ballots[i].getBytes("UTF8");
                    desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
                    byte[] textEncrypted = desCipher.doFinal(text);
                    writer.write(textEncrypted.toString());
                    writer2.write(ballots[i] + "\t");
                    
                            
                } catch(Exception e){
                }
            }          
        }
        
        try {
            
            String timeStamp = cal.getTime().toString() + "\n";
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            SecretKey myDesKey = keygenerator.generateKey();
            Cipher desCipher;
            desCipher = Cipher.getInstance("DES");
            byte[] timeStampByte = timeStamp.getBytes("UTF8");
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            byte[] stampEncrypted = desCipher.doFinal(timeStampByte);
            writer.write(stampEncrypted + "\n");
            writer2.write(timeStamp);
            
        } catch (Exception e) {
        }
        
        writer.close();
        writer2.close();
        
    } catch (IOException e) {
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jMenu1 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jPanel_President = new javax.swing.JPanel();
        jRadioB_RobertBanner = new javax.swing.JRadioButton();
        jRadioB_JamesHowlett = new javax.swing.JRadioButton();
        jRadioB_StevenRogers = new javax.swing.JRadioButton();
        jRadioB_BruceWayne = new javax.swing.JRadioButton();
        jRadioB_President_WriteIn = new javax.swing.JRadioButton();
        submitB_President = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel_Senate = new javax.swing.JPanel();
        jRadioB_ClarkKent = new javax.swing.JRadioButton();
        jRadioB_PeterParker = new javax.swing.JRadioButton();
        jRadioB_TonyStarks = new javax.swing.JRadioButton();
        jRadioB_RavenDarkholme = new javax.swing.JRadioButton();
        jRadioB_Senate_WriteIn = new javax.swing.JRadioButton();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        submitB_Senate = new javax.swing.JButton();
        jPanel_House = new javax.swing.JPanel();
        jRadioB_StevenSegal = new javax.swing.JRadioButton();
        jRadioB_CharlesXavier = new javax.swing.JRadioButton();
        jRadioB_HarveyDent = new javax.swing.JRadioButton();
        jRadioB_HansSolo = new javax.swing.JRadioButton();
        jRadioB_House_WriteIn = new javax.swing.JRadioButton();
        submitB_House = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jPanel_VoterLogin = new javax.swing.JPanel();
        jLabel_VoterFname = new javax.swing.JLabel();
        jTextField_VoterFname = new javax.swing.JTextField();
        jLabel_VoterLname = new javax.swing.JLabel();
        jTextField_VoterLname = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton_VoterLogin = new javax.swing.JButton();
        jPassword = new javax.swing.JPasswordField();
        jLabel_login = new javax.swing.JLabel();
        jPanel_Confirmation = new javax.swing.JPanel();
        jPanel_Confirmation2 = new javax.swing.JPanel();
        jLabel_PresidentChoice = new javax.swing.JLabel();
        jLabel_SenateChoice = new javax.swing.JLabel();
        jLabel_HouseChoice = new javax.swing.JLabel();
        jButton_Confirmation = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        jButton1.setText("jButton1");

        jLabel3.setText("jLabel3");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 0, 102));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));

        jPanel_President.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_President.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select a President:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 3, 18))); // NOI18N

        buttonGroup1.add(jRadioB_RobertBanner);
        jRadioB_RobertBanner.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_RobertBanner.setText("Robert Banner");
        jRadioB_RobertBanner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_RobertBannerActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioB_JamesHowlett);
        jRadioB_JamesHowlett.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_JamesHowlett.setText("James Howlett");
        jRadioB_JamesHowlett.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_JamesHowlettActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioB_StevenRogers);
        jRadioB_StevenRogers.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_StevenRogers.setText("Steven Rogers");
        jRadioB_StevenRogers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_StevenRogersActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioB_BruceWayne);
        jRadioB_BruceWayne.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_BruceWayne.setText("Bruce Wayne");
        jRadioB_BruceWayne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_BruceWayneActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioB_President_WriteIn);
        jRadioB_President_WriteIn.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_President_WriteIn.setText("Write-in");
        jRadioB_President_WriteIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_President_WriteInActionPerformed(evt);
            }
        });

        submitB_President.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        submitB_President.setText("Submit");
        submitB_President.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitB_PresidentActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        javax.swing.GroupLayout jPanel_PresidentLayout = new javax.swing.GroupLayout(jPanel_President);
        jPanel_President.setLayout(jPanel_PresidentLayout);
        jPanel_PresidentLayout.setHorizontalGroup(
            jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PresidentLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_PresidentLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_PresidentLayout.createSequentialGroup()
                                .addComponent(jRadioB_President_WriteIn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jRadioB_BruceWayne)))
                    .addGroup(jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jRadioB_StevenRogers)
                        .addComponent(jRadioB_JamesHowlett)
                        .addComponent(jRadioB_RobertBanner, javax.swing.GroupLayout.Alignment.LEADING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitB_President, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel_PresidentLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField1, jTextField2});

        jPanel_PresidentLayout.setVerticalGroup(
            jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PresidentLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(submitB_President, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_PresidentLayout.createSequentialGroup()
                        .addComponent(jRadioB_RobertBanner)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioB_JamesHowlett)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioB_StevenRogers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioB_BruceWayne)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_PresidentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioB_President_WriteIn)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanel_Senate.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Senate.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select a Senator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 3, 18))); // NOI18N

        buttonGroup2.add(jRadioB_ClarkKent);
        jRadioB_ClarkKent.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_ClarkKent.setText("Clark Kent");
        jRadioB_ClarkKent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_ClarkKentActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioB_PeterParker);
        jRadioB_PeterParker.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_PeterParker.setText("Peter Parker");
        jRadioB_PeterParker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_PeterParkerActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioB_TonyStarks);
        jRadioB_TonyStarks.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_TonyStarks.setText("Tony Starks");
        jRadioB_TonyStarks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_TonyStarksActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioB_RavenDarkholme);
        jRadioB_RavenDarkholme.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_RavenDarkholme.setText("Raven Darkholme");
        jRadioB_RavenDarkholme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_RavenDarkholmeActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioB_Senate_WriteIn);
        jRadioB_Senate_WriteIn.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_Senate_WriteIn.setText("Write-in");
        jRadioB_Senate_WriteIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_Senate_WriteInActionPerformed(evt);
            }
        });

        jTextField3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        submitB_Senate.setBackground(new java.awt.Color(0, 0, 0));
        submitB_Senate.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        submitB_Senate.setText("Submit");
        submitB_Senate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitB_SenateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_SenateLayout = new javax.swing.GroupLayout(jPanel_Senate);
        jPanel_Senate.setLayout(jPanel_SenateLayout);
        jPanel_SenateLayout.setHorizontalGroup(
            jPanel_SenateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SenateLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(jPanel_SenateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioB_ClarkKent)
                    .addGroup(jPanel_SenateLayout.createSequentialGroup()
                        .addComponent(jRadioB_Senate_WriteIn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4))
                    .addComponent(jRadioB_RavenDarkholme)
                    .addComponent(jRadioB_TonyStarks)
                    .addComponent(jRadioB_PeterParker))
                .addGap(135, 135, 135)
                .addComponent(submitB_Senate, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_SenateLayout.setVerticalGroup(
            jPanel_SenateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_SenateLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jRadioB_ClarkKent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioB_PeterParker)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioB_TonyStarks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioB_RavenDarkholme)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_SenateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioB_Senate_WriteIn)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4))
                .addGap(24, 24, 24))
            .addGroup(jPanel_SenateLayout.createSequentialGroup()
                .addComponent(submitB_Senate, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel_House.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_House.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select a House Representative", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 3, 18))); // NOI18N

        buttonGroup3.add(jRadioB_StevenSegal);
        jRadioB_StevenSegal.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_StevenSegal.setText("Steven Segal");
        jRadioB_StevenSegal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jRadioB_StevenSegal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_StevenSegalActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioB_CharlesXavier);
        jRadioB_CharlesXavier.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_CharlesXavier.setText("Charles Xavier");
        jRadioB_CharlesXavier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_CharlesXavierActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioB_HarveyDent);
        jRadioB_HarveyDent.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_HarveyDent.setText("Harvey Dent");
        jRadioB_HarveyDent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_HarveyDentActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioB_HansSolo);
        jRadioB_HansSolo.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_HansSolo.setText("Hans Solo");
        jRadioB_HansSolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_HansSoloActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioB_House_WriteIn);
        jRadioB_House_WriteIn.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jRadioB_House_WriteIn.setText("Write-in: ");
        jRadioB_House_WriteIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioB_House_WriteInActionPerformed(evt);
            }
        });

        submitB_House.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        submitB_House.setText("Submit");
        submitB_House.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitB_HouseActionPerformed(evt);
            }
        });

        jTextField5.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jTextField6.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        javax.swing.GroupLayout jPanel_HouseLayout = new javax.swing.GroupLayout(jPanel_House);
        jPanel_House.setLayout(jPanel_HouseLayout);
        jPanel_HouseLayout.setHorizontalGroup(
            jPanel_HouseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_HouseLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(jPanel_HouseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_HouseLayout.createSequentialGroup()
                        .addComponent(jRadioB_House_WriteIn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioB_HansSolo)
                    .addComponent(jRadioB_StevenSegal)
                    .addComponent(jRadioB_HarveyDent)
                    .addComponent(jRadioB_CharlesXavier))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitB_House, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel_HouseLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField5, jTextField6});

        jPanel_HouseLayout.setVerticalGroup(
            jPanel_HouseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_HouseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_HouseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(submitB_House, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_HouseLayout.createSequentialGroup()
                        .addComponent(jRadioB_StevenSegal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioB_CharlesXavier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioB_HarveyDent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioB_HansSolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_HouseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioB_House_WriteIn)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_HouseLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jTextField5, jTextField6});

        jPanel_VoterLogin.setBackground(new java.awt.Color(0, 0, 102));

        jLabel_VoterFname.setBackground(new java.awt.Color(204, 204, 204));
        jLabel_VoterFname.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel_VoterFname.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_VoterFname.setText("First Name:");

        jTextField_VoterFname.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField_VoterFname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_VoterFnameActionPerformed(evt);
            }
        });

        jLabel_VoterLname.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel_VoterLname.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_VoterLname.setText("Last Name:");

        jTextField_VoterLname.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jTextField_VoterLname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_VoterLnameActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ID:"); // NOI18N

        jButton_VoterLogin.setText("Login");
        jButton_VoterLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_VoterLoginActionPerformed(evt);
            }
        });

        jPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordActionPerformed(evt);
            }
        });

        jLabel_login.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel_login.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_login.setText("Enter Login Credentials"); // NOI18N

        javax.swing.GroupLayout jPanel_VoterLoginLayout = new javax.swing.GroupLayout(jPanel_VoterLogin);
        jPanel_VoterLogin.setLayout(jPanel_VoterLoginLayout);
        jPanel_VoterLoginLayout.setHorizontalGroup(
            jPanel_VoterLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_VoterLoginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_VoterLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_VoterLoginLayout.createSequentialGroup()
                        .addComponent(jLabel_VoterFname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_VoterFname, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel_VoterLname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_VoterLname, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_VoterLoginLayout.createSequentialGroup()
                        .addComponent(jLabel_login, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(178, 178, 178)))
                .addComponent(jButton_VoterLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        jPanel_VoterLoginLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField_VoterFname, jTextField_VoterLname});

        jPanel_VoterLoginLayout.setVerticalGroup(
            jPanel_VoterLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_VoterLoginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_login)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_VoterLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_VoterFname)
                    .addComponent(jTextField_VoterFname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_VoterLname)
                    .addComponent(jTextField_VoterLname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jButton_VoterLogin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Senate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel_House, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel_President, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_VoterLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_VoterLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_President, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel_Senate, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jPanel_House, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Confirmation.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Confirmation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CONFIRM BALLOTS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Marlett", 0, 24), new java.awt.Color(0, 204, 0))); // NOI18N

        jPanel_Confirmation2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_PresidentChoice.setBackground(new java.awt.Color(0, 0, 0));
        jLabel_PresidentChoice.setFont(new java.awt.Font("Lucida Grande", 3, 24)); // NOI18N
        jLabel_PresidentChoice.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_PresidentChoice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "President", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 24))); // NOI18N

        jLabel_SenateChoice.setBackground(new java.awt.Color(0, 0, 0));
        jLabel_SenateChoice.setFont(new java.awt.Font("Lucida Grande", 3, 24)); // NOI18N
        jLabel_SenateChoice.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_SenateChoice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Senator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 24))); // NOI18N

        jLabel_HouseChoice.setBackground(new java.awt.Color(0, 0, 0));
        jLabel_HouseChoice.setFont(new java.awt.Font("Lucida Grande", 3, 24)); // NOI18N
        jLabel_HouseChoice.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_HouseChoice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "House of Rep.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 24))); // NOI18N

        javax.swing.GroupLayout jPanel_Confirmation2Layout = new javax.swing.GroupLayout(jPanel_Confirmation2);
        jPanel_Confirmation2.setLayout(jPanel_Confirmation2Layout);
        jPanel_Confirmation2Layout.setHorizontalGroup(
            jPanel_Confirmation2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Confirmation2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Confirmation2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel_PresidentChoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel_HouseChoice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel_SenateChoice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_Confirmation2Layout.setVerticalGroup(
            jPanel_Confirmation2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Confirmation2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel_PresidentChoice, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_SenateChoice, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_HouseChoice, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton_Confirmation.setBackground(new java.awt.Color(0, 102, 0));
        jButton_Confirmation.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jButton_Confirmation.setForeground(new java.awt.Color(0, 102, 0));
        jButton_Confirmation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/votingGUI/confirm.jpeg"))); // NOI18N
        jButton_Confirmation.setText("CONFIRM BALLOTS");
        jButton_Confirmation.setBorder(null);
        jButton_Confirmation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ConfirmationActionPerformed(evt);
            }
        });

        jButton_Cancel.setBackground(new java.awt.Color(0, 0, 0));
        jButton_Cancel.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jButton_Cancel.setForeground(new java.awt.Color(255, 255, 255));
        jButton_Cancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/votingGUI/cancel.png"))); // NOI18N
        jButton_Cancel.setText("CANCEL");
        jButton_Cancel.setBorder(null);
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_ConfirmationLayout = new javax.swing.GroupLayout(jPanel_Confirmation);
        jPanel_Confirmation.setLayout(jPanel_ConfirmationLayout);
        jPanel_ConfirmationLayout.setHorizontalGroup(
            jPanel_ConfirmationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ConfirmationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ConfirmationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_Confirmation2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ConfirmationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 244, Short.MAX_VALUE)
                    .addComponent(jButton_Confirmation, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel_ConfirmationLayout.setVerticalGroup(
            jPanel_ConfirmationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ConfirmationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel_Confirmation2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Confirmation, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_ConfirmationLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton_Cancel, jButton_Confirmation});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Confirmation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Confirmation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    //********* ButtonGroup1 - Presidents *************
    
    private void jRadioB_BruceWayneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_BruceWayneActionPerformed
        
        //identify by candidate ID
        //id: 001
        president = "001";
        jRadioB_BruceWayne.setActionCommand("Bruce Wayne");
    }//GEN-LAST:event_jRadioB_BruceWayneActionPerformed

    private void jRadioB_StevenRogersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_StevenRogersActionPerformed

         //identify by candidate ID
         //id: 002;
         president = "002";
         jRadioB_StevenRogers.setActionCommand("Steven Rogers");
         
    }//GEN-LAST:event_jRadioB_StevenRogersActionPerformed

    private void submitB_PresidentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitB_PresidentActionPerformed
         

        if(presWriteIn && jRadioB_President_WriteIn.isSelected()) {
            
             String name = jTextField1.getText().trim() + " " + jTextField2.getText().trim();
             jLabel_PresidentChoice.setText(name);
        }
        else {
            
            jLabel_PresidentChoice.setText(buttonGroup1.getSelection().getActionCommand());
        
        }
        
        ballotCount++;   

        //disable panel aftr voter selection
        disablePanel(jPanel_President);
        enablePanel(jPanel_Senate);
                        
                                                     
    }//GEN-LAST:event_submitB_PresidentActionPerformed

    private void jRadioB_RobertBannerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_RobertBannerActionPerformed
        //id: 004
        president = "004";
        jRadioB_RobertBanner.setActionCommand("Robert Banner");
    }//GEN-LAST:event_jRadioB_RobertBannerActionPerformed

    private void jRadioB_JamesHowlettActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_JamesHowlettActionPerformed
       
        //id: 003
        president = "003";
        jRadioB_JamesHowlett.setActionCommand("James Howlett");
    }//GEN-LAST:event_jRadioB_JamesHowlettActionPerformed

    private void jRadioB_President_WriteInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_President_WriteInActionPerformed
        
        writeIn_ID++;
        presWriteIn = true;
        president = Integer.toString(writeIn_ID);
        
        presWRT_firstN = jTextField1.getText().trim();
        presWRT_lastN = jTextField2.getText().trim();
        
        jRadioB_President_WriteIn.setActionCommand(presWRT_firstN + " " + presWRT_lastN);
        
    }//GEN-LAST:event_jRadioB_President_WriteInActionPerformed

    // ********* ButtonGroup2 - Senators ***************
    
    private void jRadioB_ClarkKentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_ClarkKentActionPerformed
        //id: 005
        senator = "005";
        jRadioB_ClarkKent.setActionCommand("Clark Kent");
    }//GEN-LAST:event_jRadioB_ClarkKentActionPerformed

    private void jRadioB_PeterParkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_PeterParkerActionPerformed
        //id: 006
        senator = "006";
        jRadioB_PeterParker.setActionCommand("Peter Parker");
    }//GEN-LAST:event_jRadioB_PeterParkerActionPerformed

    private void jRadioB_TonyStarksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_TonyStarksActionPerformed
        //id: 007
        senator = "007";
        jRadioB_TonyStarks.setActionCommand("Tony Starks");
    }//GEN-LAST:event_jRadioB_TonyStarksActionPerformed

    private void jRadioB_RavenDarkholmeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_RavenDarkholmeActionPerformed
        //id: 011
        senator = "011";
        jRadioB_RavenDarkholme.setActionCommand("Raven Darkholmes");
    }//GEN-LAST:event_jRadioB_RavenDarkholmeActionPerformed

    private void jRadioB_Senate_WriteInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_Senate_WriteInActionPerformed
        
        sentWriteIn = true;
        
        senator = Integer.toString(writeIn_ID);

        sentWRT_firstN = jTextField3.getText().trim();
        sentWRT_lastN = jTextField4.getText().trim();
        
        jRadioB_Senate_WriteIn.setActionCommand(sentWRT_firstN + " " + sentWRT_lastN);
        
    }//GEN-LAST:event_jRadioB_Senate_WriteInActionPerformed

    private void submitB_SenateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitB_SenateActionPerformed

        if(sentWriteIn && jRadioB_Senate_WriteIn.isSelected()) {
            
             String name = jTextField3.getText().trim() + " " + jTextField4.getText().trim();
             jLabel_SenateChoice.setText(name);
        }
        else {
            
            jLabel_SenateChoice.setText(buttonGroup2.getSelection().getActionCommand());
        
        }
        
        ballotCount++;
        
       
        //disable panel aftr voter selection
        disablePanel(jPanel_Senate);
        enablePanel(jPanel_House);
        
    }//GEN-LAST:event_submitB_SenateActionPerformed

    //*********** Button Group 3 - House of Reps ***************
    
    private void jRadioB_StevenSegalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_StevenSegalActionPerformed
        //id: 012eee
        houseRep = "012";
        jRadioB_StevenSegal.setActionCommand("Steven Segal");
    }//GEN-LAST:event_jRadioB_StevenSegalActionPerformed

    private void jRadioB_CharlesXavierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_CharlesXavierActionPerformed
        //id: 008
         houseRep = "008";
        jRadioB_CharlesXavier.setActionCommand("Charles Xavier");
    }//GEN-LAST:event_jRadioB_CharlesXavierActionPerformed

    private void jRadioB_HarveyDentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_HarveyDentActionPerformed
        //id: 009
         houseRep = "009";
        jRadioB_HarveyDent.setActionCommand("Harvey Dent");
        
    }//GEN-LAST:event_jRadioB_HarveyDentActionPerformed

    private void jRadioB_HansSoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_HansSoloActionPerformed
        //id: 010
         houseRep = "010";
     
        jRadioB_HansSolo.setActionCommand("Hans Solo");
    }//GEN-LAST:event_jRadioB_HansSoloActionPerformed

    private void jRadioB_House_WriteInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioB_House_WriteInActionPerformed
        
        houseWriteIn = true;
        
        houseRep = Integer.toString(writeIn_ID);

        housWRT_firstN = jTextField5.getText().trim();
        housWRT_lastN = jTextField6.getText().trim();
        
        jRadioB_House_WriteIn.setActionCommand(housWRT_firstN + " " + housWRT_lastN);
       
    }//GEN-LAST:event_jRadioB_House_WriteInActionPerformed

    private void submitB_HouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitB_HouseActionPerformed
        
        if(houseWriteIn && jRadioB_House_WriteIn.isSelected()) {
            
             String name = jTextField5.getText().trim() + " " + jTextField6.getText().trim();
             jLabel_HouseChoice.setText(name);
        }
        else {
            
            jLabel_HouseChoice.setText(buttonGroup3.getSelection().getActionCommand());
        
        }
        
        ballotCount++;
       
        //disable panel after voter selection
        disablePanel(jPanel_House);
        enablePanel(jPanel_Confirmation);
        enablePanel(jPanel_Confirmation2);
        
        
    }//GEN-LAST:event_submitB_HouseActionPerformed

    private void jButton_ConfirmationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ConfirmationActionPerformed
    
        
        SystemDatabase sd =  new SystemDatabase(VotingSystem.USERNAME,
                VotingSystem.PASSWORD,VotingSystem.SERVER_NAME, 
                VotingSystem.PORT_NUMBER, VotingSystem.DATABASE_NAME);
        
        ballots[0] = president; 
        ballots[1] = senator;
        ballots[2] = houseRep;
      
        
        if (presWriteIn && jRadioB_President_WriteIn.isSelected()) {
            sd.tallyWriteIn(jTextField1.getText().trim(), jTextField2.getText().trim(),"PRES");
            presWriteIn = false;
        }
        
        if (sentWriteIn && jRadioB_Senate_WriteIn.isSelected()) {
            sd.tallyWriteIn(jTextField3.getText().trim(),jTextField4.getText().trim(),"SENT");
            sentWriteIn = false;
        }
        
        if (houseWriteIn && jRadioB_House_WriteIn.isSelected()) {
            sd.tallyWriteIn(jTextField5.getText().trim(),jTextField6.getText().trim(),"HOSR");
            houseWriteIn = false;
        }
        
        updateLiveResults(ballots);
       
        for (int i = 0; i < ballots.length; i++) {
           
            castBallot(ballots[i]);
        }
        
        
        disablePanel(jPanel_President);
        disablePanel(jPanel_Senate);
        disablePanel(jPanel_House);
        jLabel_SenateChoice.setText("");
        jLabel_HouseChoice.setText("");
        jLabel_PresidentChoice.setText("");
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jPanel_VoterLogin.setBackground(Color.blue);
        jLabel_login.setText("Enter Login Credentials");
        
        
        if (voterID != null) {
            
            sd.markVoterAsVoted(voterID);
        }
        
        enablePanel(jPanel_VoterLogin);
        resetVoterSelections();
        disablePanel(jPanel_Confirmation);
        disablePanel(jPanel_Confirmation2);
        
       
       ThankYou thanks = new ThankYou();
       thanks.setVisible(true);
       
    }//GEN-LAST:event_jButton_ConfirmationActionPerformed

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        
        //reset cands id to defaults
        
        ballots[0] = "000"; 
        ballots[1] = "000";
        ballots[2] = "000";
        
        resetVoterSelections();
        enablePanel(jPanel_House);
        enablePanel(jPanel_Senate);
        enablePanel(jPanel_President);
  
        //reset confirmation Panel
        jLabel_PresidentChoice.setText("");
        jLabel_SenateChoice.setText("");
        jLabel_HouseChoice.setText("");
        
        disablePanel(jPanel_Confirmation);

        for (int i = 0; i < ballots.length; i++) {
            
        }
        
        
    }//GEN-LAST:event_jButton_CancelActionPerformed

    /**
     * clear all user selections
     */
    public void resetCandOpts(){
        buttonGroup1.clearSelection();
        
        jLabel_PresidentChoice.setText("");
        jLabel_SenateChoice.setText("");
        jLabel_HouseChoice.setText("");
        
        jLabel_PresidentChoice.setBackground(Color.WHITE);
        jLabel_HouseChoice.setBackground(Color.WHITE);
        jLabel_SenateChoice.setBackground(Color.WHITE);
        
        
    }
    
    
    
    
    private void jTextField_VoterLnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_VoterLnameActionPerformed
       
        
    }//GEN-LAST:event_jTextField_VoterLnameActionPerformed

    private void jButton_VoterLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_VoterLoginActionPerformed
        
        adminVerify();
        
        //Connect to DB 
        Connection conn = null;
        ResultSet rs = null;

        try {
            
            conn = DriverManager.getConnection(dbURL, username, password);
                    } catch (SQLException e) {
        }

         String firstN_entry = jTextField_VoterFname.getText().trim();
         String lastN_entry = jTextField_VoterLname.getText().trim();
         char[] id_entry = jPassword.getPassword();
         String password = new String(id_entry);
         jLabel_login.setText("");
         
        // Validate Voter Login
        try {
            
            String query = "SELECT * FROM " + VotingSystem.VOTER_TABLE;                  
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next()){
                String dbID = rs.getString(1).trim();
                String db_voter_firstName = rs.getString(2).trim();
                String db_voter_lastName = rs.getString(3).trim();
            
                //Check Voter Login credntials
                if( dbID.equals(password)
                        && db_voter_firstName.equalsIgnoreCase(firstN_entry)
                        && db_voter_lastName.equalsIgnoreCase(lastN_entry)
                        && rs.getInt(4) == 0){
                    
                        jPanel_VoterLogin.revalidate();  
                        jLabel_login.setText("Login Success");
                        voterID = dbID; //setting global ID var
                        jPanel_VoterLogin.setBackground(Color.green);
                        disablePanel(jPanel_VoterLogin);
                        enablePanel(jPanel_President);
                        jButton_Confirmation.setBackground(Color.GREEN);
                        jButton_Cancel.setBackground(Color.RED);
                        
                        break;
                  }
                   else {
                       jLabel_login.setText("Invalid Registrant");
                       jPanel_VoterLogin.setBackground(Color.red);
                       jTextField_VoterFname.setText("");
                       jTextField_VoterLname.setText("");  
                       jPassword.setText("");             
                  } 
                
            }
    
        } catch (SQLException e) {
        }
        
        

    }//GEN-LAST:event_jButton_VoterLoginActionPerformed

    private void jTextField_VoterFnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_VoterFnameActionPerformed
    }//GEN-LAST:event_jTextField_VoterFnameActionPerformed

    private void jPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordActionPerformed
    }//GEN-LAST:event_jPasswordActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
    }//GEN-LAST:event_jTextField5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
       /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VoterForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VoterForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VoterForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VoterForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VoterForm().setVisible(true);
            }
        });
    }
    
     public boolean adminVerify() {                                         
       
        Connection conn = null;
        ResultSet rs = null;

        try {
            
            conn = DriverManager.getConnection(dbURL, username, password);
        } catch (SQLException e) {
        }
        
        try {
            
            String query = "SELECT * FROM " + VotingSystem.ADMIN_TABLE;                  
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            
            String adminFirstN_entry =  jTextField_VoterFname.getText().trim();
            String adminLastN_entry = jTextField_VoterLname.getText().trim();
            char[] adminID_entry = jPassword.getPassword();
            String password = new String(adminID_entry);
            
            while (rs.next()){
                String dbID = rs.getString(1).trim();
                String db_admin_firstName = rs.getString(2).trim();
                String db_admin_lastName = rs.getString(3).trim();
                
                //Check Admin Login credntials
                if( dbID.equals(password)
                        && db_admin_firstName.equalsIgnoreCase(adminFirstN_entry)
                        && db_admin_lastName.equalsIgnoreCase(adminLastN_entry)){
                    
                    jPanel_VoterLogin.setBackground(Color.BLACK);
                    //open Admin options
                    AdminOpts ad = new AdminOpts();
                    ad.setResizable(false);
                    this.setVisible(false);
                    ad.setVisible(true);
                    return true;
                }                
           }
    
        } catch (SQLException e) {
        }
        
        
        return false;
    } 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Confirmation;
    private javax.swing.JButton jButton_VoterLogin;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_HouseChoice;
    private javax.swing.JLabel jLabel_PresidentChoice;
    private javax.swing.JLabel jLabel_SenateChoice;
    private javax.swing.JLabel jLabel_VoterFname;
    private javax.swing.JLabel jLabel_VoterLname;
    private javax.swing.JLabel jLabel_login;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_Confirmation;
    private javax.swing.JPanel jPanel_Confirmation2;
    private javax.swing.JPanel jPanel_House;
    private javax.swing.JPanel jPanel_President;
    private javax.swing.JPanel jPanel_Senate;
    private javax.swing.JPanel jPanel_VoterLogin;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JRadioButton jRadioB_BruceWayne;
    private javax.swing.JRadioButton jRadioB_CharlesXavier;
    private javax.swing.JRadioButton jRadioB_ClarkKent;
    private javax.swing.JRadioButton jRadioB_HansSolo;
    private javax.swing.JRadioButton jRadioB_HarveyDent;
    private javax.swing.JRadioButton jRadioB_House_WriteIn;
    private javax.swing.JRadioButton jRadioB_JamesHowlett;
    private javax.swing.JRadioButton jRadioB_PeterParker;
    private javax.swing.JRadioButton jRadioB_President_WriteIn;
    private javax.swing.JRadioButton jRadioB_RavenDarkholme;
    private javax.swing.JRadioButton jRadioB_RobertBanner;
    private javax.swing.JRadioButton jRadioB_Senate_WriteIn;
    private javax.swing.JRadioButton jRadioB_StevenRogers;
    private javax.swing.JRadioButton jRadioB_StevenSegal;
    private javax.swing.JRadioButton jRadioB_TonyStarks;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField_VoterFname;
    private javax.swing.JTextField jTextField_VoterLname;
    private javax.swing.JButton submitB_House;
    private javax.swing.JButton submitB_President;
    private javax.swing.JButton submitB_Senate;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package votingGUI;

import votingSystem.SystemDatabase;


/**
 *
 * @author Casey
 */
public class AdminOpts extends javax.swing.JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	VoterForm vf;
    /**
     * Creates new form adminOpts
     */
    public AdminOpts() {
        initComponents();
        vf = new VoterForm();
        jTextArea1.setEditable(false);
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jButton_Actv = new javax.swing.JButton();
        jButton_DeAct = new javax.swing.JButton();
        jButton_Recount = new javax.swing.JButton();
        jButton_uTally = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 51, 153));

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Adminstrative Options", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 24))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(51, 51, 51));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tst(evt);
            }
        });

        jButton_Actv.setBackground(new java.awt.Color(0, 102, 0));
        jButton_Actv.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jButton_Actv.setText("Activate EVS");
        jButton_Actv.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_Actv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ActvActionPerformed(evt);
            }
        });

        jButton_DeAct.setBackground(new java.awt.Color(153, 0, 0));
        jButton_DeAct.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jButton_DeAct.setText("DeActivate EVS");
        jButton_DeAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_DeActActionPerformed(evt);
            }
        });

        jButton_Recount.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jButton_Recount.setText("Recount Ballots");
        jButton_Recount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_RecountActionPerformed(evt);
            }
        });

        jButton_uTally.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jButton_uTally.setText("Unoffical Tally");
        jButton_uTally.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_uTallyActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_Recount, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_uTally, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Actv, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_DeAct, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton_Actv, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_DeAct, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_Recount, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_uTally, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton_Actv, jButton_DeAct, jButton_Recount, jButton_uTally});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_DeActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DeActActionPerformed
        vf.dispose();
        this.dispose();
    }//GEN-LAST:event_jButton_DeActActionPerformed

    private void jButton_RecountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_RecountActionPerformed
        jTextArea1.setText("");
        if(SystemDatabase.verifyVoteCount()) {
            jTextArea1.setText("VOTES CERTIFIED "
                    + "\nNumber of Casted Ballots: "
                    + SystemDatabase.castedBallots
                    + "\nNumber of Voters: "
                    + SystemDatabase.votersThatVoted);
           
        }
        else {
            jTextArea1.setText("VOTE Verification Fail " 
                    + "\nNumber of Casted Ballots: "
                    + SystemDatabase.castedBallots
                    + "\nNumber of Voters: "
                    + SystemDatabase.votersThatVoted);
        }
    }//GEN-LAST:event_jButton_RecountActionPerformed

    private void jButton_ActvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ActvActionPerformed
       
    
       this.setVisible(false);
       vf.setTitle("Voter Form");
       vf.setResizable(false);
       vf.setVisible(true);
    }//GEN-LAST:event_jButton_ActvActionPerformed

    private void jButton_uTallyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_uTallyActionPerformed
      
        jTextArea1.setText(SystemDatabase.getUnofficialTally2());
        
    }//GEN-LAST:event_jButton_uTallyActionPerformed

    private void tst(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tst
    }//GEN-LAST:event_tst

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
            java.util.logging.Logger.getLogger(AdminOpts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminOpts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminOpts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminOpts.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminOpts().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Actv;
    private javax.swing.JButton jButton_DeAct;
    private javax.swing.JButton jButton_Recount;
    private javax.swing.JButton jButton_uTally;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea4;
    // End of variables declaration//GEN-END:variables
}
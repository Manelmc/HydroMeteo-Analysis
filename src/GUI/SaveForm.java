/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import controllers.GUIcontroller;
import hydrometeo_analysis.Table;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author Manel
 */
public class SaveForm extends javax.swing.JFrame {

   
    private ArrayList model = new ArrayList();
    private HashMap<String,ArrayList> modelCollection = new HashMap();
    private boolean flagModelCollection = false;
    private JFrame main;
    /**
     * Creates new form SaveForm
     * @param model
     * @param main
     */
    public SaveForm(ArrayList model, final JFrame main) {
        this.main = main;
        this.main.setEnabled(false);
        this.model = model;
        initComponents();
        initialize();

    }
    
    public SaveForm(HashMap<String,ArrayList> modelCollection, final JFrame main) {
        this.main = main;
        this.main.setEnabled(false);
        this.modelCollection = modelCollection;
        flagModelCollection = true;
        initComponents();
        initialize();

    }
    
    
    private void initialize(){
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jButton1.addActionListener(new Browse());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            main.setEnabled(true);
        }});
        
        jTextField2.setText(initName());
        /*if(flagModelCollection){
            //jTextField2.setVisible(false);
            jTextField2.setEnabled(false);
            jLabel2.setEnabled(false);
        }*/
    }
    private String initName(){
        String name = "EMPTY";
        ArrayList row = new ArrayList();
        String date;
        String tab ; String coach = "nameCoach";
        boolean empty = true;
        if(flagModelCollection && modelCollection.size() > 0){
            row = (ArrayList) ((ArrayList) modelCollection.get(modelCollection.keySet().iterator().next())).get(0);
            empty = false;
        }else if(!flagModelCollection && model.size() > 0){
           row = (ArrayList) model.get(0);
           empty = false;
        }
        if(!empty){
            if(row.size() != 13){
                tab = "Survey";
            }else{
                tab = "Wind";
                coach = (String) row.get(12);
            }
            date = (String) row.get(0);
            return tab + "_data_" + coach +"_" + date;
        }else return name;
    }
    
    public SaveForm() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jButton1.addActionListener(new Browse());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Browse...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Local Path:");

        jButton2.setText("Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setText("File Name:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                            .addComponent(jTextField1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private String generateName(String H){
        String name = jTextField2.getText();
        if(flagModelCollection){
            return name + "_"  + H;          
        }else return name;
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String Path = jTextField1.getText();
        if(flagModelCollection) for(String s: modelCollection.keySet()) createFile(modelCollection.get(s), Path, generateName(s));
        else createFile(model, Path, generateName(""));
        main.setEnabled(true);
        dispose();
        
    }//GEN-LAST:event_jButton2ActionPerformed
    private void createFile(ArrayList dataToSave, String Path, String Name){
        if(!"".equals(Path)){
            try {
                File f = new File(Path + "\\" +  Name + ".txt");
                f.getParentFile().mkdirs();
                int count = 1;
                while(f.exists()){
                    f = new File(Path + "\\" +  Name + "(" + count + ")" + ".txt");
                    f.getParentFile().mkdirs();
                    ++count;
                }
                f.createNewFile();
                PrintStream out = new PrintStream(new FileOutputStream(f));
                System.setOut(out);
                if(dataToSave.size() > 0){
                    ArrayList row = (ArrayList) dataToSave.get(0);
                    if(row.size() == 13){                    
                        System.out.println("DATE\t;HOUR_LT\t;LAT\t\t;LON\t\t;TWD_MAG\t\t;TWS\t\t;TWD_TRUE\t;BOATS;HEADMAG;PRESS.;TEMP.;HUMID.;COACH".replace(";", "\t"));
                    }else{
                        System.out.println("date\t;time\t;lat\t\t;lon\t\t;fromTR;headTR;fromMAG;headMAG;magdev;speed;timzone;distH;DT;DH".replace(";", "\t"));
                    }
                }
                for(int i = 0; i < dataToSave.size(); ++i){
                    ArrayList row = (ArrayList) dataToSave.get(i);
                    for(int j = 0; j < row.size(); ++j){
                        String data = (String) row.get(j);
                        if((j != 2 && j != 3) || data.length() > 16){
                            System.out.print(data + "\t");
                        }else{
                            System.out.print(data + "\t\t");
                        }
                    }
                    System.out.println();
                }
                PrintStream STDOUT = new PrintStream(new FileOutputStream(FileDescriptor.out));
                System.setOut(STDOUT);
            } catch (IOException ex) {
                Logger.getLogger(SaveForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            showMessageDialog(null, "Please, browse a valid Path");
        }
    }
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
         main.setEnabled(true);
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
        class Browse implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser();
            c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rVal = c.showSaveDialog(SaveForm.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
              //filename.setText(c.getSelectedFile().getName());

             jTextField1.setText(c.getSelectedFile().getAbsolutePath());
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
              //filename.setText("You pressed cancel");
              
              jTextField1.setText("");
            }
        }
    }
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
            java.util.logging.Logger.getLogger(SaveForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SaveForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SaveForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SaveForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SaveForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
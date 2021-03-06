/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import hydrometeo_analysis.ANN;
import hydrometeo_analysis.Calculus;
import hydrometeo_analysis.Serialize;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Manel
 */
public class PanelANN extends javax.swing.JFrame {
    private HashMap<String,ANN> networkMAP = new HashMap();
    private ANN network;
    private Image tick;
    private Image cross;
    private String path = ".\\ANNs";
    private String name = "ANNs";
    private Menu menu;
    private Object[] tableHeader = new Object[7];
    private boolean flagRemove = false;
    private boolean LoadedANN = false;
    double[][] outputLastConsult = new double[0][3];
    /**
     * Creates new form PanelANN
     */
    public PanelANN() {
        Initialize();
    }
    public PanelANN(Menu menu) {
        this.menu = menu;
        Initialize();
    }
    private String getNetworkName(){
        if(network == null) return "No ANN";
        else return network.name;
    }
    public TitledBorder createTitledBorder(){
        return javax.swing.BorderFactory.createTitledBorder(null, getNetworkName(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                           javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18), java.awt.Color.black);
    }
    public final void Initialize() {
        initComponents();
        ClassLoader cl = this.getClass().getClassLoader();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Serialize.SaveObject(networkMAP, path, name);
                menu.ANNopened = false;
                dispose();
            }
        });
        try {
            tick = ImageIO.read(cl.getResource("Images/TICK.png")).getScaledInstance(38, 38, Image.SCALE_DEFAULT);
            cross = ImageIO.read(cl.getResource("Images/CROSS.png")).getScaledInstance(38, 38, Image.SCALE_DEFAULT);
        } catch (IOException ex) {
            Logger.getLogger(PanelANN.class.getName()).log(Level.SEVERE, null, ex);
        }
        jLabel6.setIcon(new ImageIcon(cross));  jLabel8.setIcon(new ImageIcon(cross));
        try {
            networkMAP = (HashMap<String,ANN>) Serialize.loadObject(path + "\\" + name);
        } catch (ClassNotFoundException | IOException ex) {
            networkMAP = new HashMap();
        }
        for(String n: networkMAP.keySet()) jComboBox1.addItem(n);
            jComboBox1.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!flagRemove){
                    network = networkMAP.get((String) jComboBox1.getSelectedItem());
                    network.errorDiagnostic(network.SC_INPUT, network.SC_OUTPUT);
                    jPanel1.setBorder(createTitledBorder());
                    jLabel12.setText(network.error);
                    jLabel6.setIcon(new ImageIcon(tick));
                    LoadedANN = true;
                    jLabel8.setIcon(new ImageIcon(tick));
                }
            }
        });
        tableHeader[0] = "DistH"; tableHeader[1] = "DT"; tableHeader[2] = "DH"; 
        tableHeader[3] = "IDEALDirection";tableHeader[4] = "IDEALSpeed"; 
        tableHeader[5] = "Direction";tableHeader[6] = "Speed";
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "No ANN", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18), java.awt.Color.black)); // NOI18N

        jLabel3.setText("New ANN name: ");

        jButton2.setText("Create");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Save");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Loaded:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Saved:");

        jButton5.setText("Delete");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel9.setText("Create and train new Artificial Neural Network with last Database query: ");

        jLabel10.setText("Load existing Artificial Neural Network:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("Infinity");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Error:");

        jLabel1.setText("Magnetic Deviation:");

        jTextField1.setText("22.0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGap(0, 23, Short.MAX_VALUE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(9, 9, 9)
                .addComponent(jLabel9)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton5)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Data Manually:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel11.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        jLabel11.setText("E.g. -02:17,+05:40,1.17    OR     -137,340,1.17");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        jLabel2.setText("In each line there must be 3 values separated by comas, representing: distH, DT, DH");

        jCheckBox1.setText("Input last Database query");

        jButton4.setText("OK");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addGap(285, 285, 285)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(1, 1, 1)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        ArrayList<ArrayList> model = menu.getModel();
        int size = 0;
        if(model.size() > 0) size = model.get(0).size();
        if(network != null){
            if(size == 14){
                outputLastConsult = new double[0][3];
                Object[] result = network.inputArrayToANN(getDataToInput());
                int j = result.length -1;
                double errorNumDir = 0;
                double errorNumSpd = 0;
                if(jCheckBox1.isSelected()){
                    for(int i = outputLastConsult.length-1; i >= 0; --i){
                        ((Object[]) result[j])[3] = (outputLastConsult[i][0] + Calculus.HoursToMinutes(jTextField1.getText()))%360;
                        ((Object[]) result[j])[4] = outputLastConsult[i][1];
                        double dif = +((Float)((Object[]) result[j])[5]) - outputLastConsult[i][0];
                        ((Object[]) result[j])[5] = (((Float)((Object[]) result[j])[5]) + (float)Calculus.HoursToMinutes(jTextField1.getText()))%360;
                        dif = (Math.abs(dif) + 180)%360 -180;
                        errorNumDir += Math.pow(dif, 2);                 
                        errorNumSpd += Math.pow(-((Float)((Object[]) result[j])[6]) + outputLastConsult[i][1], 2);
                        --j;
                        

                    }
                }
                double errorDir = Double.MAX_VALUE; double errorSpd = Double.MAX_VALUE;
                if(outputLastConsult.length > 0 ){
                    errorSpd = Math.sqrt(errorNumSpd/outputLastConsult.length);
                    errorDir = Math.sqrt(errorNumDir/outputLastConsult.length);
                }       
                createAndDisplay(result);
                showMessageDialog(null, "Direction Error: " + (float)errorDir  +" , Speed Error: " + (float)errorSpd );
            }else showMessageDialog(null, "The last consult is either empty or the wrong data type "
                                     + "\nRemember, the ANN panel works exclusively with the sea current data type");
        }else showMessageDialog(null, "Please, Load an ANN");
    }//GEN-LAST:event_jButton4ActionPerformed
    private void createAndDisplay(Object[] dataTable){
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], tableHeader);
        JTable tab = new JTable();
        tab.setModel(model);
        tab.setAutoCreateColumnsFromModel(true);
        for(int i = 0; i < dataTable.length; ++i) model.insertRow(tab.getRowCount(), (Object[]) dataTable[i]);
        JFrame frame = new JFrame();
        JScrollPane panel = new JScrollPane(tab);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel);
        frame.add(mainPanel);
        frame.setLocationRelativeTo(this);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
    private ArrayList<double[]> getDataToInput(){
        String lines[] = jTextArea1.getText().split("\\r?\\n");
        ArrayList<String> inputAreaAux = new ArrayList<>(Arrays.asList(lines)) ;
        ArrayList<double[]> inputArea = new ArrayList();
        for(String s: inputAreaAux){
            String[] values = s.split(",");
            if(values.length == 3)inputArea.add(new double[]{Calculus.HoursToMinutes(values[0]),Calculus.HoursToMinutes(values[1]),Double.parseDouble(values[2])});
        }
        //System.out.println(inputArea);
        ArrayList<double[]> inputANN = new ArrayList(inputArea);
        ArrayList<ArrayList<String> >  data = menu.getModel();
        if(jCheckBox1.isSelected() && !data.isEmpty() && data.get(0).size() > 13){
            ANN temp = new ANN("");
            temp.parseTrainingArray(data);
            double[][] inputLastConsult = temp.SC_INPUT.clone();
            outputLastConsult = temp.SC_OUTPUT.clone();
            inputANN.addAll(Arrays.asList(inputLastConsult));
        }
        return inputANN;
    }
        
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(LoadedANN){
            if(!networkMAP.containsKey(network.name)){
                networkMAP.put(network.name,network);
                jComboBox1.addItem(network.name);
                jLabel8.setIcon(new ImageIcon(tick));
            }else showMessageDialog(null, "There is already an existing ANN with this name");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ArrayList<ArrayList> model = menu.getModel();
        int size = 0;
        if(model.size() > 0) size = model.get(0).size();
        if(!jTextField2.getText().equals("")){
            if(size == 14){
                if(!networkMAP.containsKey(jTextField2.getText())){
                    network = new ANN(jTextField2.getText());
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    double error = network.trainANN(menu.getModel());
                    setCursor(Cursor.getDefaultCursor());
                    if(error != Double.MAX_VALUE){
                        String err = Double.toString(error*100f);
                        if(err.length() > 6)err = err.substring(0,6);
                        network.error = err + "%";
                    }else network.error = "Infinity";
                    jLabel12.setText(network.error);
                    showMessageDialog(null, "ANN trained!");
                    jPanel1.setBorder(createTitledBorder());
                    jLabel6.setIcon(new ImageIcon(tick));
                    LoadedANN = true;
                    jLabel8.setIcon(new ImageIcon(cross));
                }else showMessageDialog(null, "There is already an existing ANN with this name");
            }else showMessageDialog(null, "The last consult is either empty or the wrong data type "
                                     + "\nRemember, the ANN panel works exclusively with the sea current data type");
        }else showMessageDialog(null, "Please, name the ANN");
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        networkMAP.remove(network.name);
        flagRemove = true;
        jComboBox1.removeItem(network.name);
        flagRemove = false;
        jLabel8.setIcon(new ImageIcon(cross));
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(PanelANN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PanelANN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PanelANN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PanelANN.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PanelANN().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}

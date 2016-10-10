/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

/**
 *
 * @author Manel
 */
public class Menu {

    public JMenuItem menuUpdateDB;
    public JMenuItem menuEmptyDB;
    public JMenuItem menuSave;
    public JMenuItem menuPanelANN;
    public JMenuItem menuView;
    public JMenuItem menuHelp;
    public JMenuItem menuAbout;
    private ArrayList model = new ArrayList();
    private HashMap<String,ArrayList> modelCollection = new HashMap();
    private boolean flagModelCollection = false;
    private final JFrame main;
    public boolean ANNopened;
    
    public Menu(JFrame frame){
        this.main = frame;
        createMenuUpdateDB();
        createMenuEmptyDB();
        createMenuSave();
        createMenuPanelANN();
        createMenuView();
        createMenuHelp();
        createMenuAbout();
    }
    public void setFlagModelCollection(boolean flag_Model_Collection){
        this.flagModelCollection = flag_Model_Collection;
        if(this.flagModelCollection) modelCollection = new HashMap();
    }
    public void setModel(ArrayList model, String name){
        if(!flagModelCollection) this.model = model;
        else if(model.size() > 0) modelCollection.put(name, (ArrayList) model.clone());
    }
    public ArrayList getModel(){
        if(flagModelCollection){
            ArrayList modelJoin = new ArrayList();
            for(ArrayList A: modelCollection.values()){for(Object a: A){ modelJoin.add((ArrayList) a);}}
            return modelJoin;
        }else return model;
    }
    private void viewPDF(){
        ClassLoader cl = this.getClass().getClassLoader();

        // build a component controller
        SwingController controller = new SwingController();

        SwingViewBuilder factory = new SwingViewBuilder(controller);

        JPanel viewerComponentPanel = factory.buildViewerPanel();

        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));

        JFrame applicationFrame = new JFrame();
        applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        applicationFrame.getContentPane().add(viewerComponentPanel);

        // Now that the GUI is all in place, we can try openning a PDF
        controller.openDocument(cl.getResource("User_Guide.pdf"));

        // show the component
        applicationFrame.pack();
        applicationFrame.setVisible(true);
    }
    private void createSeaCurrentPlot(){
        PlotCreator study;
        PlotCreator study2;
        if(!flagModelCollection || this.modelCollection.keySet().iterator().next().contains("T")){
            study = new PlotCreator("Sea Current Plot", getModel(), 0);
            study2 = new PlotCreator("Sea Current Plot", getModel(), 1);
        }else{
            study = new PlotCreator("Sea Current Plot", modelCollection, 0);
            study2 = new PlotCreator("Sea Current Plot", modelCollection, 1);
        }
        study.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        study.pack();
        study.setLocation(main.getX(),main.getY());
        if(study.isValid) study.setVisible(true);
        else study.dispose();
        study2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        study2.pack();
        study2.setLocation(study.getX()+study.getWidth()/2, study.getY() + study.getHeight()/2);
        if(study2.isValid) study2.setVisible(true);
        else study2.dispose();
    }
    private void createMenuAbout(){
        menuAbout = new JMenuItem("About...");//, KeyEvent.VK_S
        /*menuSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));*/
        menuAbout.getAccessibleContext().setAccessibleDescription(
                "About..");
        menuAbout.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent me) {}
            @Override
            public void mousePressed(MouseEvent me) {
                showMessageDialog(null, 
                  "HydroMeteo Analysis it’s a handy, packed and portable application "
                + "\nthat enhances the meteorological study of winds, currents and tides."
                + "\nIt was designed and implemented by Manel Maragall in 2015 under the "
                + "\nexpert watch of Alicia Ageno and in collaboration with Elena Cristofori."
                + "\n\nContact me in:  manelmaragallcambra@gmail.com");
            }
            @Override
            public void mouseReleased(MouseEvent me) {}      
            @Override
            public void mouseEntered(MouseEvent me) {}
            @Override
            public void mouseExited(MouseEvent me) {}       
        });
    }
    private void createMenuHelp(){
        menuHelp = new JMenuItem("Show User Guide...");//, KeyEvent.VK_S
        /*menuSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));*/
        menuHelp.getAccessibleContext().setAccessibleDescription(
                "Show Help..");
        menuHelp.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent me) {}
            @Override
            public void mousePressed(MouseEvent me) {
                viewPDF();
            }
            @Override
            public void mouseReleased(MouseEvent me) {}      
            @Override
            public void mouseEntered(MouseEvent me) {}
            @Override
            public void mouseExited(MouseEvent me) {}       
        });
    }
    private void createMenuView(){
        menuView = new JMenuItem("Last consult Plots...");//, KeyEvent.VK_S
        /*menuSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));*/
        menuView.getAccessibleContext().setAccessibleDescription(
                "View last consult (Sea current or wind) Plot (Speed and Direction) by (H or Time)..");
        menuView.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent me) {}
            @Override
            public void mousePressed(MouseEvent me) {
                createSeaCurrentPlot();
            }
            @Override
            public void mouseReleased(MouseEvent me) {}      
            @Override
            public void mouseEntered(MouseEvent me) {}
            @Override
            public void mouseExited(MouseEvent me) {}       
        });
    }
    
    private void createMenuPanelANN(){
        menuPanelANN = new JMenuItem("Open ANN Manager...");//, KeyEvent.VK_S
        /*menuSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));*/
        menuPanelANN.getAccessibleContext().setAccessibleDescription(
                "Save last consult");
        final Menu menu = this;
        menuPanelANN.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent me) {}
            @Override
            public void mousePressed(MouseEvent me) {
                if(!ANNopened){
                    PanelANN FormANN = new PanelANN(menu);
                    ANNopened = true;
                    FormANN.setLocationRelativeTo(main);
                    FormANN.setVisible(true);  
                }else showMessageDialog(null, "The Panel is already opened!");
            }
            @Override
            public void mouseReleased(MouseEvent me) {}      
            @Override
            public void mouseEntered(MouseEvent me) {}
            @Override
            public void mouseExited(MouseEvent me) {}       
        });
    }
    private void createMenuSave(){
        menuSave = new JMenuItem("Save last consult...");//, KeyEvent.VK_S
        /*menuSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.ALT_MASK));*/
        menuSave.getAccessibleContext().setAccessibleDescription(
                "Save last consult");
        menuSave.addMouseListener(new MouseListener(){
                @Override
        public void mouseClicked(MouseEvent me) {}
        
        @Override
        public void mousePressed(MouseEvent me) {
            SaveForm SaveForm ;
            if(flagModelCollection)SaveForm = new SaveForm(modelCollection,main);
            else SaveForm = new SaveForm(model,main);
            SaveForm.setLocationRelativeTo(main);
            SaveForm.setVisible(true);  
        }
        @Override
        public void mouseReleased(MouseEvent me) {}
        @Override
        public void mouseEntered(MouseEvent me) {}
        @Override
        public void mouseExited(MouseEvent me) { }    });
    }
    private void createMenuUpdateDB(){
        menuUpdateDB = new JMenuItem("Update Database...");//, KeyEvent.VK_U
        /*menuUpdateDB.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.ALT_MASK));*/
        menuUpdateDB.getAccessibleContext().setAccessibleDescription(
                "Update Database");
        menuUpdateDB.addMouseListener(new MouseListener(){
        @Override
        public void mouseClicked(MouseEvent me) {}
        @Override
        public void mousePressed(MouseEvent me) {
            UpdateDBForm UPDBForm = new UpdateDBForm(main);
            UPDBForm.setLocationRelativeTo(main);
            UPDBForm.setVisible(true);  
        }
        @Override
        public void mouseReleased(MouseEvent me) {}      
        @Override
        public void mouseEntered(MouseEvent me) {}
        @Override
        public void mouseExited(MouseEvent me) {}       });
    }

    private void createMenuEmptyDB() {
        menuEmptyDB = new JMenuItem("Empty Database...");//, KeyEvent.VK_E
        /*menuEmptyDB.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));*/
        menuEmptyDB.getAccessibleContext().setAccessibleDescription(
                "Empty Database");
        menuEmptyDB.addMouseListener(new MouseListener(){
        @Override
        public void mouseClicked(MouseEvent me) {}

        @Override
        public void mousePressed(MouseEvent me) {
            EmptyDBForm EmptyForm = new EmptyDBForm(main);
            EmptyForm.setLocationRelativeTo(main);
            EmptyForm.setVisible(true);  
        }

        @Override
        public void mouseReleased(MouseEvent me) {}
        @Override
        public void mouseEntered(MouseEvent me) {}
        @Override
        public void mouseExited(MouseEvent me) {}  });
    }

}

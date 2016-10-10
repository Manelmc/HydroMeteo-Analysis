/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import hydrometeo_analysis.Calculus;
import java.awt.BorderLayout;
import java.awt.Color;
import static java.lang.Double.NaN;
import java.util.*;
import javax.swing.JFrame;
import static javax.swing.JOptionPane.showMessageDialog;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Manel
 */
public class PlotCreator extends JFrame {

    private static final int N = 13;
    private static String title = "Sea Current Speed Plot";
    private static final Random rand = new Random();
    private HashMap<String, ArrayList> xReference;
    private ArrayList<ArrayList<String>> model;
    public boolean isValid = true;
    
    public PlotCreator(String s, HashMap<String, ArrayList> xReference, int Method) {
        super("");
        this.xReference = xReference;
        if(this.xReference.size() > 0){
                if(Method == 0){
                    title = "Sea Current Speed Plot";
                    final ChartPanel chartPanel = createSeaCurrentSpeedByHPanel();
                    this.add(chartPanel, BorderLayout.CENTER);
                }else{
                    title = "Sea Current Direction Plot";
                    final ChartPanel chartPanel = createSeaCurrentDirectionByHPanel();
                    this.add(chartPanel, BorderLayout.CENTER);
                }
            
        }else if( Method == 0){
            showMessageDialog(null, "Last consult is empty!");
            isValid = false;
            dispose();
        }else{
             isValid = false;
            dispose();
        }
        
        /*JPanel control = new JPanel();
        control.add(new JButton(new AbstractAction("Add") {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < N; i++) {
                    added.add(rand.nextGaussian(), rand.nextGaussian());
                }
            }
        }));
        this.add(control, BorderLayout.SOUTH);*/
    }
    
    public PlotCreator(String s, ArrayList model, int Method) {
        super("");
        this.model = model;
        if(this.model.size() > 0){
            ArrayList<String> firstElem = (ArrayList) this.model.get(0);
            if(firstElem.size() > 13){
                if(Method == 0){
                    title = "Sea Current Speed Plot";
                    final ChartPanel chartPanel = createSeaCurrentSpeedByTimePanel();
                    this.add(chartPanel, BorderLayout.CENTER);
                }else{
                    title = "Sea Current Direction Plot";
                    final ChartPanel chartPanel = createSeaCurrentDirectionByTimePanel();
                    this.add(chartPanel, BorderLayout.CENTER);
                }
            }else{
                if(Method == 0){
                    title = "Wind Intensity Plot";
                    final ChartPanel chartPanel = createWindIntensityByTimePanel();
                    this.add(chartPanel, BorderLayout.CENTER);
                }else{
                    title = "Wind Direction Plot";
                    final ChartPanel chartPanel = createWindDirectionByTimePanel();
                    this.add(chartPanel, BorderLayout.CENTER);
                }               
            }
        }else if( Method == 0){
            showMessageDialog(null, "Last consult is empty!");
            isValid = false;
            dispose();
        }else{
            isValid = false;
            dispose();
        }
        
    }
    
       ////////////////////////////////////////////////////////////////////////////////
     private ChartPanel createWindIntensityByTimePanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title, "Time(Hours)", "Wind Instensity", createWindIntensityByTime(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        //domain.setRange(0,24);
        domain.setTickUnit(new NumberTickUnit(1.0));
        domain.setVerticalTickLabels(true);
        
        return new ChartPanel(jfreechart);
    }

    private XYDataset createWindIntensityByTime() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries speed = new XYSeries("Intensity");
        for (ArrayList<String> A : model){
            double x = Calculus.HoursToMinutes(A.get(1));
            double y = Math.abs(Double.parseDouble(A.get(5)));
            System.out.println(x+ "," + y);
            speed.add(x/60d, y);
        }
        xySeriesCollection.addSeries(speed);
        return xySeriesCollection;
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
     private ChartPanel createWindDirectionByTimePanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title, "Time(Hours)", "Wind Direction", createWindDirectionByTime(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLACK);
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
         domain.setTickUnit(new NumberTickUnit(1.0));
        domain.setVerticalTickLabels(true);
        
        return new ChartPanel(jfreechart);
    }

    private XYDataset createWindDirectionByTime() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries speed = new XYSeries("Direction");
        for (ArrayList<String> A : model){
            double x = Calculus.HoursToMinutes(A.get(1));
            double y = Math.abs(Double.parseDouble(A.get(6))%360);
            System.out.println(x+ "," + y);
            speed.add(x/60d, y);
        }
        xySeriesCollection.addSeries(speed);
        return xySeriesCollection;
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    private ChartPanel createSeaCurrentSpeedByTimePanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title, "Time(Hours)", "Sea Current Speed", createSeaCurrentSpeedDataByTime(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
         domain.setTickUnit(new NumberTickUnit(1.0));
        domain.setVerticalTickLabels(true);
        
        return new ChartPanel(jfreechart);
    }

    private XYDataset createSeaCurrentSpeedDataByTime() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries speed = new XYSeries("Speed");
        for (ArrayList<String> A : model){
            double x = Calculus.HoursToMinutes(A.get(1));
            double y = Math.abs(Double.parseDouble(A.get(9)));
            System.out.println(x+ "," + y);
            speed.add(x/60d, y);
        }
        xySeriesCollection.addSeries(speed);
        return xySeriesCollection;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////77
    
    private ChartPanel createSeaCurrentDirectionByTimePanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title, "Time(Hours)", "Sea Current Direction", createSeaCurrentDirectionByTime(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        //XYItemRenderer renderer = xyPlot.getRenderer();
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
         domain.setTickUnit(new NumberTickUnit(1.0));
        domain.setVerticalTickLabels(true);
        
        return new ChartPanel(jfreechart);
    }

    private XYDataset createSeaCurrentDirectionByTime() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries speed = new XYSeries("Speed");
        for (ArrayList<String> A : model){
            double x = Calculus.HoursToMinutes(A.get(1));
            double y = Math.abs(Double.parseDouble(A.get(5))%360);
            System.out.println(x+ "," + y);
            speed.add(x/60d, y);
        }
        xySeriesCollection.addSeries(speed);
        return xySeriesCollection;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    
    private ChartPanel createSeaCurrentSpeedByHPanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title, "H", "Sea Current Speed", createSeaCurrentSpeedDataByH(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        //XYItemRenderer renderer = xyPlot.getRenderer();
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setVerticalTickLabels(true);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);
        xyPlot.setRenderer(renderer);
        return new ChartPanel(jfreechart);
    }

    private XYDataset createSeaCurrentSpeedDataByH() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries speed = new XYSeries("Speed");
        XYSeries mean = new XYSeries("Speed Mean");
        for (String s: xReference.keySet()) {
            String aux = s.replace("H", ""); aux = aux.replace(" ", "");
            double x = Double.parseDouble(aux);
            ArrayList<ArrayList<String>> data = (ArrayList) xReference.get(s);
            double acc = 0; double count = 0;
            for(ArrayList<String> d: data){
                if(!d.get(9).equals("NaN")){
                    double y = Math.abs(Double.parseDouble(d.get(9))); 
                    acc += y; ++count;
                    speed.add(x,y);
                } 
            }
            acc = acc / count;
            System.out.println("Mean: " + acc);
            mean.add(x, acc);
        }
        xySeriesCollection.addSeries(speed);
        xySeriesCollection.addSeries(mean);
        return xySeriesCollection;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    private ChartPanel createSeaCurrentDirectionByHPanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            "Sea Current Direction Plot", "H", "Sea Current Direction", createSeaCurrentDirectionDataByH(),
            PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        //XYItemRenderer renderer = xyPlot.getRenderer();
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setVerticalTickLabels(true);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);
        xyPlot.setRenderer(renderer);
        return new ChartPanel(jfreechart);
    }

    private XYDataset createSeaCurrentDirectionDataByH() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries direction = new XYSeries("Direction");
        XYSeries mean = new XYSeries("Direction Mean");
        for (String s: xReference.keySet()) {
            String aux = s.replace("H", ""); aux = aux.replace(" ", "");
            double x = Double.parseDouble(aux);
            ArrayList<ArrayList<String>> data = (ArrayList) xReference.get(s);
            ArrayList<double[]> acc = new ArrayList();
            for(ArrayList<String> d: data){
                double y = Double.parseDouble(d.get(5))%360;
                acc.add(new double[]{0d,0d,y,0d});
                direction.add(x, y);// speeds < 0 ???
            }
            mean.add(x, Calculus.mergePoints(acc)[2]);
        }
        xySeriesCollection.addSeries(direction);
        xySeriesCollection.addSeries(mean);
        return xySeriesCollection;
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    public PlotCreator(String s, ArrayList<double[]> dataToDisplay){
        super(s);
        final ChartPanel chartPanel = createEquirectangularErrorPanel(dataToDisplay);
        this.add(chartPanel, BorderLayout.CENTER);
    }
    private ChartPanel createEquirectangularErrorPanel(ArrayList<double[]> dataToDisplay) {
        title = "Equirectangular vs Haversine - Error Plot";
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
        title, "Distance Between Points (meters)", "Error", createEquirectangularErrorData(dataToDisplay),
        PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        //XYItemRenderer renderer = xyPlot.getRenderer();
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setVerticalTickLabels(true);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);
        xyPlot.setRenderer(renderer);
        return new ChartPanel(jfreechart);
    }

    private XYDataset createEquirectangularErrorData(ArrayList<double[]> dataToDisplay) {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries speed = new XYSeries("Error");
        for(double[] d1: dataToDisplay){
            for(double[] d2: dataToDisplay){
               double haversine = Calculus.HaversineDistance(d1, d2);
               double Equi = 1000*Calculus.equirectangularDistanceApproximation(d1, d2);
               System.out.println(haversine + " , " + Math.abs(haversine-Equi));
               speed.add(haversine, Math.abs(haversine-Equi));
            }
            
        }
        xySeriesCollection.addSeries(speed);
        return xySeriesCollection;
    }
}
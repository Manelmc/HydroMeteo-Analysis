/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import hydrometeo_analysis.DataBase;
import hydrometeo_analysis.FileParser;
import hydrometeo_analysis.MapImage;
import hydrometeo_analysis.Table;
import java.awt.Image;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author Manel
 */
public class GUIcontroller {
    private final DataBase DB;
    private final MapImage MAPImage;
    private final FileParser FP;
    private ArrayList<double[]> dataToDisplay = new ArrayList();
    private Object[] dataTable = new Object[]{};
    private double[] selectedItem = new double[]{};
    
    public double[] getCoordCenter(ResultSet rs, Table table){
        try {
            ArrayList dataTableAux = new ArrayList();
            dataToDisplay = new ArrayList();
            //double maxlat = -90.0; double minlat = 90.0; double maxlon = -180.0; double minlon = 180.0;
            double ACClat = 0; double ACClon = 0;
            while(rs.next()){
                dataTableAux.add(getData(rs, table));
                double lat = rs.getDouble("latitude"); double lon = rs.getDouble("longitude");
                double[] rowToDisplay = null;
                if(table == Table.SEACURRENT){
                    double direction = rs.getDouble("headingTRUE");double speed = rs.getDouble("speed");
                    rowToDisplay = new double[]{lat,lon, direction, speed};//MODIFY
                }else if(table == Table.WIND){
                    double direction = rs.getDouble("direction"); double intensity = rs.getDouble("intensity");
                    rowToDisplay = new double[]{lat,lon,direction,intensity};
                }
                dataToDisplay.add(rowToDisplay);
                //if(lat > maxlat) maxlat = lat;   if(lat < minlat) minlat = lat;
                //if(lon > maxlon) maxlon = lon;   if(lon < minlon) minlon = lon;
                ACClat += lat; ACClon += lon;
            }
            dataTable =dataTableAux.toArray(new Object[dataTableAux.size()]);
            //double defLat = (minlat + (maxlat-minlat)/2.0); double defLon = (minlon + (maxlon-minlon)/2.0);
            double defLat = 0, defLon = 0;
            if(dataToDisplay.size() > 0){ defLat = ACClat/dataToDisplay.size(); defLon = ACClon/dataToDisplay.size();}
            System.out.println("Boyas= " +  dataToDisplay.size());
            return new double[]{defLat,defLon};
        } catch (SQLException ex) {
            Logger.getLogger(GUIcontroller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new double[]{};
        
    }
      
    public GUIcontroller(){ 
        DB = new DataBase();
        MAPImage = new MapImage();
        FP = new FileParser();
    }
   
    public ResultSet consultDB(String SQL){
        //DB.displayResult(Table.SEACURRENT, rs);
        return  DB.consult(SQL);
        
    }
    
    public String consultByDate(Table table, String[] date, String[] time, String[] lat, String[] lon, String[] DTDHorCoach){
        return DB.consultByDate(table, date, time, lat, lon, DTDHorCoach);
    }
    
    public void deleteDBTable(Table table){
        DB.deleteAll(table);
    }
    
    public double[] getCenterInPixels(){
        return MAPImage.getCenterInPixels();
    }
    public ArrayList getColumns(Table table){
        return DB.getColumns(table);
    }
    
    private Object[] getData(ResultSet rs, Table table){
        return DB.getData(rs, table);
    }
    
    public Object[] getDataTable(){
        return dataTable;
    }
    
    public ArrayList getDataToDisplay(){
        return dataToDisplay;
    }
    
    
    public HashMap<String,ResultSet> getHreference(float HInterval, String[] date, String[] time, String[] lat, String[] lon, String[] DTDHorCoach){
        return DB.getHreference(HInterval, date, time, lat, lon, DTDHorCoach);
    }
    
    public HashMap<String,ResultSet> getTreference(Table table, String T, float HInterval, String[] date, String[] time, String[] lat, String[] lon, String[] DTDHorCoach){
        return DB.getTreference(table, T, HInterval, date, time, lat, lon, DTDHorCoach);
    }
    public Image getMapImage(ResultSet rs, Table table, int minDistance){
        selectedItem = new double[]{};
        MAPImage.getMapImage(getCoordCenter(rs, table));
        MAPImage.setTable(table);
        MAPImage.synthesizeData(dataToDisplay, minDistance);
        return MAPImage.displayData(selectedItem);

    }
    public Image getMapImage(Object[] data, Table table, int minDistance){
        selectedItem = new double[]{};
        dataToDisplay = (ArrayList) data[1];
        double[] coordCenter = (double[]) data[0];
        MAPImage.getMapImage(coordCenter);
        MAPImage.setTable(table);
        MAPImage.synthesizeData(dataToDisplay, minDistance);
        return MAPImage.displayData(selectedItem);

    }
    public int getMapZoom(){
        return MAPImage.getZoom();
    }
    public Image moveImageCenterBlank(int incrX, int incrY){
         MAPImage.moveImageCenter(incrX, incrY);
         return MAPImage.getMapImage(new double[]{});
    }
    public Image moveImageCenter(){
         return MAPImage.displayData(selectedItem);
    }
    public Image selectItem(double[] selectedItem){
        this.selectedItem = selectedItem;
        return MAPImage.displayData(this.selectedItem);
    }
    
    public Image setMapSize(int sizeX, int sizeY){
        MAPImage.setMapSize(sizeX, sizeY);
        MAPImage.getMapImage(new double[]{});
        return MAPImage.displayData(selectedItem);
    }
    
    public Image setZoom(boolean increase){
        MAPImage.setZoom(increase);
        MAPImage.getMapImage(new double[]{});
        return MAPImage.displayData(selectedItem);
    }
    
    public int updateDB(String Path, Table table){
        ArrayList data = FP.parseFile(Path, table);
        return DB.insertNewData(data, table);
    }
    public void adminMode(String SQL){
        DB.update(SQL);
    }
}

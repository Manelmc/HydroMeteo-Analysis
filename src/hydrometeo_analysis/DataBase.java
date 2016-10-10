/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.JOptionPane.showMessageDialog;


/**
 *
 * @author Manel
 */
public class DataBase {
    private final String DBname = "`main`"; //testing
    private Connection con;
    private ArrayList columnsSeaCurrent;
    private ArrayList columnsWind;
    private ArrayList columnsTides;
    
    public String update(String SQL){
        String error = "none";
        System.out.println(SQL);
        try {
            Statement stmt = con.createStatement( );//TYPE_FORWARD_ONLY
            stmt.executeUpdate( SQL );
        } catch (SQLException ex) {
            error = ex.getMessage();
            if(error.contains("database is locked")) showMessageDialog(null,error);
            System.out.println(ex.getMessage());
            //Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return error;
    }
    
    public DataBase(){
        //init columnsSeaCurrent
        columnsSeaCurrent = new ArrayList();
        columnsSeaCurrent.add("date"); columnsSeaCurrent.add("time"); columnsSeaCurrent.add("latitude"); 
        columnsSeaCurrent.add("longitude"); columnsSeaCurrent.add("fromTRUE"); columnsSeaCurrent.add("headingTRUE");
        columnsSeaCurrent.add("fromMAG"); columnsSeaCurrent.add("headingMAG"); columnsSeaCurrent.add("MAGdev"); 
        columnsSeaCurrent.add("speed"); columnsSeaCurrent.add("timezone");  columnsSeaCurrent.add("distH");
         columnsSeaCurrent.add("DT"); columnsSeaCurrent.add("DH");
        //init columns Wind
        columnsWind = new ArrayList();
        columnsWind.add("date"); columnsWind.add("time"); columnsWind.add("latitude"); 
        columnsWind.add("longitude"); columnsWind.add("magnetic_direction"); columnsWind.add("intensity");
        columnsWind.add("direction"); columnsWind.add("ship's_speed"); columnsWind.add("ship's_course");
        columnsWind.add("atmospheric_pressure"); columnsWind.add("temperature"); columnsWind.add("humidity");
        columnsWind.add("coach");
        //init columns Tides
        columnsTides = new ArrayList();
        columnsTides.add("date"); columnsTides.add("first_low_h"); columnsTides.add("first_low_m"); columnsTides.add("first_high_h"); 
        columnsTides.add("first_high_m"); columnsTides.add("second_low_h"); columnsTides.add("second_low_m"); columnsTides.add("second_high_h");
        columnsTides.add("second_high_m");
        //dataBaseServerStart();
            //Establish connection
            /*
            String host = "jdbc:mysql://localhost:3306/testing?zeroDateTimeBehavior=convertToNull";
            String uName = "root";C:\Users\Manel\Documents\NetBeansProjects\HydroMeteo_Analysis\src\hydrometeo_analysis
            String uPass= "";
            con = DriverManager.getConnection( host, uName, uPass );
            */
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite::resource:database1.sqlite");
        } catch (SQLException | ClassNotFoundException ex) {
            showMessageDialog(null,ex.getMessage());
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void dataBaseServerStart(){
        //Start MySQL server
        try {
            Process p = new ProcessBuilder("C:\\xampp\\mysql\\bin\\mysqld", "").start();
        } catch (IOException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void dataBaseServerShutDown(){
        try {
            Process p = new ProcessBuilder("C:\\xampp\\mysql\\bin\\mysqladmin", "-u root shutdown").start();
        } catch (IOException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void deleteAll(Table table){
        String tab = null;
        if(table == Table.WIND){
            tab = " `wind` ";
        }else if(table == Table.SEACURRENT){
            tab = " `seacurrent` ";
        }else if(table == Table.TIDES){
            tab = " `tides` ";
        }
        String SQL = "DELETE FROM"+ tab + "WHERE 1";
        update(SQL);
    }
    public ResultSet consult(String SQL){
        System.out.println(SQL);
        try {
            Statement stmt = con.createStatement( );//TYPE_FORWARD_ONLY       
            ResultSet rs = stmt.executeQuery( SQL );
            return rs;
        } catch (SQLException ex) {
            String error = ex.getMessage();
            if(error.contains("database is locked")) error = error +  " . Close the application and re-open it";
            showMessageDialog(null,  error);
        }
        return null;
    }
    public HashMap<String,ResultSet> getHreference(float HInterval, String[] date, String[] time, String[] lat, String[] lon, String[] DTDHorCoach){
        HashMap<String,ResultSet> Href = new HashMap();
        int rango = (int) (6/HInterval);
        String SQL = consultByDate(Table.SEACURRENT, date,time, lat, lon,  DTDHorCoach);
        float H = -6;
        update("BEGIN TRANSACTION");
        for(float i = -rango; i <= rango; ++i){//0.25,0.5,1.0//15,30,60
           String sign = " + "; if(H < 0) sign = " - ";
           Href.put("H"+ sign + Float.toString(Math.abs(H)),consult(TidesAndTimeManager.getHInterval(SQL,HInterval,i)));
           System.out.println("H + " + H);
           H += HInterval;
        }
        update("COMMIT TRANSACTION");
        return Href;
    }
    public HashMap<String,ResultSet> getTreference(Table table, String T, float HInterval, String[] date, String[] time, String[] lat, String[] lon, String[] DTDHorCoach){
        HashMap<String,ResultSet> Href = new HashMap();
        int rango = (int) (6/HInterval); 
        String SQL = consultByDate(table, date , time, lat, lon,  DTDHorCoach);
        float TT = -6;
        update("BEGIN TRANSACTION");
        for(float i = -rango; i <= rango; ++i){//0.25,0.5,1.0//15,30,60
           String sign = " + "; if(TT < 0) sign = " - ";
           Href.put("T"+ sign + Float.toString(Math.abs(TT)),consult(TidesAndTimeManager.getTInterval(SQL, T,HInterval,i)));
           System.out.println("T + " + TT);
           TT += HInterval;
        }
        update("COMMIT TRANSACTION");
        return Href;
    }
    public String consultByDate(Table table, String[] date, String[] time, String[] lat, String[] lon, String[] DTDHorCoach){
        String tab = null;
        if(table == Table.WIND){
            tab = " `wind` ";
        }else if(table == Table.SEACURRENT){
            tab = " `seacurrent` ";
        }else if(table == Table.TIDES){
            tab = " `tides` ";
        }
        String SQL = "SELECT * FROM" + tab + "WHERE 1";
        if(date.length == 4){
            String DATE = "'" + date[3] + "-" + date[2] + "-" + date[1] + "'" ;
            String sign = " >= ";
            if(date[0].equals("end")) sign = " <= ";
            SQL = SQL + " AND `date`"+ sign + "date(" + DATE + ")";
        }else if(date.length == 6){  
            String initDATE = "'" + date[2] + "-" + date[1]+ "-" + date[0] + "'";
            String endDATE = "'" + date[5] + "-" + date[4]+ "-" + date[3] + "'";
            SQL = SQL + " AND `date` BETWEEN " + "date(" + initDATE + ")" + " AND " + "date(" + endDATE + ")";
        }
        if(time.length == 4){
            String TIME = "'" + time[1] + ":" + time[2] + ":" + time[3] + "'";
            String sign = " >= ";
            if(time[0].equals("end")) sign = " <= ";
            SQL = SQL + " AND `time`" + sign + "time(" +TIME+ ")";
        }else if(time.length == 6){
            String initTIME ="'" + time[0] + ":" + time[1] + ":" + time[2] + "'";
            String endTIME ="'" + time[3] + ":" + time[4] + ":" + time[5] + "'";
            SQL = SQL + " AND `time` BETWEEN " + "time(" + initTIME + ")" + " AND " + "time(" + endTIME + ")";
        }
        if(lat.length == 2){
            switch (lat[0]) {
                case "end":
                    SQL = SQL + " AND `latitude` <= " + lat[1];
                    break;
                case "init":
                    SQL = SQL + " AND `latitude` >= " + lat[1];
                    break;
                default:
                    SQL = SQL + " AND `latitude` BETWEEN " + lat[0] + " AND " + lat[1];
                    break;
            }
        }
        if(lon.length == 2){
            switch (lon[0]) {
                case "end":
                    SQL = SQL + " AND `longitude` <= " + lon[1];
                    break;
                case "init":
                    SQL = SQL + " AND `longitude` >= " + lon[1];
                    break;
                default:
                    SQL = SQL + " AND `longitude` BETWEEN " + lon[0] + " AND " + lon[1];
                    break;
            }
        }
        if(DTDHorCoach.length == 1 && !DTDHorCoach[0].equals("")){//Coach
            SQL = SQL + " AND `coach` = " + "'" + DTDHorCoach[0] + "'" ;
        }else if(DTDHorCoach.length == 2){//DTDH
            if(!DTDHorCoach[0].equals("")){//DT
                SQL = SQL + " AND `DT` = " + DTDHorCoach[0];
            }
            if(!DTDHorCoach[1].equals("")){//DH
                SQL = SQL + " AND `DH` = " + DTDHorCoach[1];
            }
        }
        
        //String SQL = "SELECT * FROM" + tab + "WHERE `date` = " + DATE;
        return SQL;
    }
    
    public ArrayList getColumns(Table table){
        if(table == Table.SEACURRENT){
            return columnsSeaCurrent;
        }else  if(table == Table.WIND){
            return columnsWind;
        }
        return null;
    }
    
    public  Object[] getData(ResultSet rs, Table table){
        try {
            ArrayList data = new ArrayList();
            if(table == Table.SEACURRENT){
                data = columnsSeaCurrent;
            }else if(table == Table.WIND){
                data = columnsWind;
            }else if(table == Table.TIDES){
                data = columnsTides;
            }
            Object[] result = new Object[data.size()];
            String date = rs.getString((String) data.get(0));
            result[0] = date.substring(8) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4);
            for(int i = 1; i < result.length;++i){
                result[i] = rs.getString((String) data.get(i));
            }
            if(table == Table.SEACURRENT){
                if(result[result.length-2] != null) result[result.length-2] = Calculus.minutesToHours((String) result[result.length-2]);
                if(result[result.length-3] != null) result[result.length-3] = Calculus.minutesToHours((String) result[result.length-3]);
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public int insertNewData(ArrayList data, Table table){
        String columns = null;
        String tab = null;
        if(table == Table.SEACURRENT){
            tab = ".`seacurrent` ";
            columns = "(" + "`" + (String) columnsSeaCurrent.get(0) + "`" ;
            for(int j = 1; j < columnsSeaCurrent.size(); ++j){
                columns = columns + ", " + "`" + (String) columnsSeaCurrent.get(j) + "`";
            }
            columns = columns + ")";
            
        }else if(table == Table.WIND){
            tab = ".`wind` ";
            columns = "(" + "`" + (String) columnsWind.get(0) + "`" ;
            for(int j = 1; j < columnsWind.size(); ++j){
                columns = columns + ", " + "`" + (String) columnsWind.get(j) + "`";
            }
            columns = columns + ")";
        }else if(table == Table.TIDES){
            tab = ".`tides` ";
            columns = "(" + "`" + (String) columnsTides.get(0) + "`" ;
            for(int j = 1; j < columnsTides.size(); ++j){
                columns = columns + ", " + "`" + (String) columnsTides.get(j) + "`";
            }
            columns = columns + ")";
        }
        int errors = 0;
        TidesAndTimeManager TM = new TidesAndTimeManager();
        if(table == Table.SEACURRENT) TM.initTidesForSeaCurrent(data);
        update("BEGIN;");
        for(int i = 0; i < data.size(); ++i){
            ArrayList line = (ArrayList) data.get(i); 
            if(line.size() > 1){
                String date = (String) line.get(0);
                date = date.substring(4) + "-"+ date.substring(2, 4) + "-"+ date.substring(0, 2);
                String values = "(" + "date( " + "'" + date + "' )" ;
                if( table != Table.TIDES){
                    String time = (String) line.get(1);
                    time = time.substring(0,2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
                    values = values + ", " + "time( " + "'" + time + "' )" ;
                    for(int j = 2; j < line.size(); ++j){
                        values = values + ", " + "'" + (String) line.get(j) + "'";
                    }
                    if(table == Table.SEACURRENT) values = values + TM.calculateTideRelatedFields(date, time);
                }else{
                    for(int j = 1; j < line.size(); j += 2){                  
                        String time = (String) line.get(j);
                        if(time.equals("NULL")){
                            values = values + ", " + "NULL,NULL";
                        }else{
                            time = time.substring(0,2) + ":" + time.substring(2, 4) + ":" + "00";
                            values = values + ", " + "time( " + "'" + time + "' )" ;
                            values = values + ", " + "'" + (String) line.get(j+1) + "'";
                        }
                    }
                }
                
                values = values + ")";
                String cmd = "INSERT INTO " + DBname + tab + columns + " VALUES " + values;
                String error = update(cmd);
                if(!error.equals("none")) ++errors;
            }
        }
        update("COMMIT;");
        return errors;
    }
}

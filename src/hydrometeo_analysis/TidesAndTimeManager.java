/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;


/**
 *
 * @author Manel
 */
public class TidesAndTimeManager extends DataBase {
    private static final HashMap<String, String[]> tides = new HashMap();
    
    public TidesAndTimeManager(){
       
    }

     
    public static String getTInterval(String SQL, String High, float HInterval, float i){
        SQL = SQL + " AND " + createStringSQL_T( HInterval, i, createLocalTime(High));
        
        return SQL;       
    }
    private static String createStringSQL_T(float HInterval, float i, LocalTime T){
        //i = i - HInterval;
        int aux = (int) (60*Math.abs(HInterval));
        int aux2 = (aux/2)-1; if(HInterval == 0.25) aux2 = aux2 +1;
        aux2 += aux*i;
        LocalTime endT = T.plusMinutes(aux2);
        String h = Integer.toString(endT.getHourOfDay());
        String m = Integer.toString(endT.getMinuteOfHour());
        if(h.length() == 1) h = "0" + h; if(m.length() == 1) m = "0" + m;
        String endTime = "'" + h+ ":" + m + ":00" + "'";
        LocalTime startT = T.plusMinutes((aux2+1) - aux);
        h = Integer.toString(startT.getHourOfDay());
        m = Integer.toString(startT.getMinuteOfHour());
        if(h.length() == 1) h = "0" + h; if(m.length() == 1) m = "0" + m;
        String startTime = "'" + h+ ":" + m + ":00" + "'";

        if(i >= 0 && !T.isBefore(endT)){
            endTime = "'23:59:59'";
            int minutes = Minutes.minutesBetween(startT, createLocalTime("23:59:59")).getMinutes();
            if(minutes > 60*Math.abs(HInterval)) startTime = "'23:59:59'";
        }else if(i <= 0 && T.isBefore(startT)){
            startTime = "'00:00:00'";
            int minutes = Minutes.minutesBetween(createLocalTime("00:00:00"), endT).getMinutes();
            if(minutes > 60*Math.abs(HInterval)) endTime = "'00:00:00'";
        }
        
        return "`time` BETWEEN " + "time(" + startTime + ")" + " AND " + "time(" + endTime + ")";
    }
    
    public static String getHInterval(String SQL, float HInterval, float i){
        int aux = (int) (60*Math.abs(HInterval));
        int endH = (aux/2)-1; if(HInterval == 0.25) endH = endH +1;
        endH += aux*i;
        int startH = (endH+1) - aux;        
        return SQL + " AND `distH` BETWEEN " + Integer.toString(startH)+ " AND " + Integer.toString(endH);
    }
    public void initTidesForSeaCurrent(ArrayList data){
        for(int i = 0; i < data.size(); ++i){
            String date = (String) ((ArrayList) data.get(i)).get(0); 
            String[] exactDate = new String[]{date.substring(0, 2),date.substring(2, 4),date.substring(4),
                                              date.substring(0, 2),date.substring(2, 4),date.substring(4)};//BETWEEN
            date = date.substring(4) + "-"+ date.substring(2, 4) + "-"+ date.substring(0, 2);
            if(!tides.containsKey(date)){
                try {
                    ResultSet rs = consult(consultByDate(Table.TIDES, exactDate, new String[]{""},new String[]{""},new String[]{""},new String[]{"","",""}));
                    
                    while(rs.next()){
                        Object[] dat = getData(rs, Table.TIDES);
                        tides.put(date, Arrays.copyOf(dat, dat.length, String[].class));
                        for(String s : tides.get(date)){
                            System.out.print(s + "  ");
                        }
                        System.out.println();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TidesAndTimeManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }         
        }
    }
    public String calculateTideRelatedFields(String date, String time){
        if(!tides.containsKey(date))  return "";//", NULL , NULL , NULL";
        String[] row = tides.get(date);
        LocalTime tim = createLocalTime(time);
        LocalTime H_Second_High = createLocalTime(row[7]);
        LocalTime H_First_High = createLocalTime(row[3]);
        Object[] DISTH = getDistH(tim,H_First_High,H_Second_High);
        boolean isFirstHigh = (boolean) DISTH[0];
        String distH = (String) DISTH[1];
        String DT, DH;
        String[] DTDH;
        if(isFirstHigh) DTDH = getDHandDT( row, H_First_High, Integer.parseInt(distH), 3);
        else DTDH = getDHandDT( row, H_Second_High, Integer.parseInt(distH), 7);
        DT = DTDH[1]; DH = DTDH[0];
        return ", '" + distH + "'," + DT + "," + DH ;
         
    }
    
    private String[] getDHandDT( String[] row, LocalTime H_High, int minutes, int index){
        if(minutes >= 0 ){//after high
            if(index >= 7 || row[index+2] == null) return new String[]{"NULL","NULL"};
            String DT = "NULL", DH = "NULL";
            if(index+1 < row.length || index+3 < row.length) DT = "'" + Float.toString(Float.parseFloat(row[index+1]) - Float.parseFloat(row[index+3])) + "'" ; //HIGH - LOW
            if(index+2 < row.length ) DH = "'" + Integer.toString(Minutes.minutesBetween(H_High, createLocalTime(row[index+2])).getMinutes()) + "'";
            return new String[]{DT,DH};
        }else{//before high
            if(row[index-2] == null) return new String[]{"NULL","NULL"};
            String DT = "NULL";
            if(index+1 < row.length) DT = "'" + Float.toString(Float.parseFloat(row[index+1]) - Float.parseFloat(row[index-1])) + "'"; //HIGH - LOW
            String DH = "'" + Integer.toString(Minutes.minutesBetween(createLocalTime(row[index-2]), H_High).getMinutes()) + "'";
            return new String[]{DT,DH};
        }
      
        
    }
    
    private Object[] getDistH(LocalTime time, LocalTime H_First_High, LocalTime H_Second_High){
        if(H_First_High != null && H_Second_High == null){
            int minutes = (-1) * Minutes.minutesBetween(time, H_First_High).getMinutes(); //(+ o -)
            return new Object[]{true, Integer.toString(minutes)};//true = first High, false = Second High
            //System.out.println("Start: " + time.toString() + " End: " + H_First_High.toString() + " " + minutes );
        }else if(H_First_High == null && H_Second_High != null){
            int minutes = (-1) * Minutes.minutesBetween(time, H_Second_High).getMinutes(); //(+ o -)
            return new Object[]{false, Integer.toString(minutes)};
        }else if(H_First_High != null && H_Second_High != null){
            int minutesFirst = (-1) * Minutes.minutesBetween(time, H_First_High).getMinutes(); //(+ o -)
            int minutesSecond = (-1) * Minutes.minutesBetween(time, H_Second_High).getMinutes();
            if(Math.abs(minutesFirst) < Math.abs(minutesSecond)){
                return new Object[]{true, Integer.toString(minutesFirst)};
            }else {
                return new Object[]{false, Integer.toString(minutesSecond)};
            }
        }else{//BOTH null
            return null;
        }
    }

    private static LocalTime createLocalTime(String time){
        if(time != null){
            //System.out.println(time);
            return new LocalTime(Integer.parseInt(time.substring(0, 2)),
                                 Integer.parseInt(time.substring(3, 5)),Integer.parseInt(time.substring(6)));
        }else{
            return null;
        }
    }
}

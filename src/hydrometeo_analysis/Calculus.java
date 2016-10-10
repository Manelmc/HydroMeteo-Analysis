/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;



/**
 *
 * @author Manel
 */
public class Calculus {
        
    private static int tileSize(){return 256;};
    
    public Calculus(){
        
    }
    private static class resultCPOP{
        public double distance;
        public double[] PointA;
        public double[] PointB;
        public List<double[]> S;
        
        public resultCPOP(double distance, double[] PointA, double[] PointB, List<double[]> S){
            this.distance = distance; this.PointA = PointA; this.PointB = PointB; this.S = S;
        }
        
        public int size(){ return S.size();};
        
        public resultCPOP subList(int i, int j){return new resultCPOP(distance, PointA, PointB, S.subList(i, j));}
        
        public resultCPOP join(List<double[]> L, List<double[]> R){
            List<double[]> N = new ArrayList(L);
            N.addAll(R);
            return new resultCPOP(distance, PointA, PointB, N);
        }
    }
    //returns the minimum-distance pair of points
    public static List<double[]> getClosestPairOfPoints(List<double[]> S){
        ArrayList<double[]> result;
        if(S.size() > 1){
            result = new ArrayList();
            resultCPOP R = getClosestPairOfPoints(new resultCPOP(0,null,null,S));
            result.add(R.PointA); result.add(R.PointB);
        }else result = new ArrayList(S);
        return result;
    }
    private static resultCPOP getClosestPairOfPoints(resultCPOP RES){
        if(RES.size() > 3){
            resultCPOP L = getClosestPairOfPoints(RES.subList(0, RES.size()/2));
            resultCPOP R = getClosestPairOfPoints(RES.subList(RES.size()/2, RES.size()));
            double LRdistance = Double.MAX_VALUE;         
            double[] LR_L = null, LR_R = null;
            double dist = Math.min(L.distance, R.distance);
            List<double[]> Rectangle_by_Y = computeRectangle(L.S,R.S,(L.S.get(L.size()-1)[0] + R.S.get(0)[0])/2, dist);
            for(int i = 0; i < Rectangle_by_Y.size(); ++i){
                double[] l = Rectangle_by_Y.get(i);
                for(int j = i+1; j <= i+16; ++j){
                    if(j >= 0 && j < Rectangle_by_Y.size()){
                        double[] r = Rectangle_by_Y.get(j);
                        double AUXdistance = equirectangularDistanceApproximation(l,r);
                        if(AUXdistance != 0 && AUXdistance < LRdistance){ LRdistance = AUXdistance; LR_L = l; LR_R = r;}
                    }
                }
            }
            resultCPOP LR = new resultCPOP(LRdistance,LR_L,LR_R,L.S); 
            if(L.distance < R.distance){
                if(L.distance < LRdistance) return L.join(L.S, R.S);
                else return LR.join(L.S, R.S);
            }else{
                if(R.distance < LRdistance) return R.join(L.S, R.S);
                else return LR.join(L.S, R.S);
            }

        }else {
            double distance1 = equirectangularDistanceApproximation(RES.S.get(0),RES.S.get(1));
            double distance2 = Double.MAX_VALUE; double distance3 = Double.MAX_VALUE;
            if(RES.size() > 2){
                distance2 = equirectangularDistanceApproximation(RES.S.get(0),RES.S.get(2));
                distance3 = equirectangularDistanceApproximation(RES.S.get(1),RES.S.get(2));
            }
            if(distance1 < distance2 && distance1 < distance3){
                RES.PointA = RES.S.get(0);  RES.PointB = RES.S.get(1);
                RES.distance = distance1;
            }else if(distance2 < distance3){
                RES.PointA = RES.S.get(0);  RES.PointB = RES.S.get(2);
                RES.distance = distance2;    
            }else{
                RES.PointA = RES.S.get(1);  RES.PointB = RES.S.get(2);
                RES.distance = distance3;            
            }
            
            //if(RES.distance == 0) RES.distance = Double.MAX_VALUE;
            return RES; 
        }
    }
    private static List<double[]> computeRectangle(List<double[]> L, List<double[]> R, double dividingLine, double dist){
        int i;
        for(i = L.size()-1; i >= 0; --i)if(Math.abs(dividingLine - L.get(i)[0]) > dist) break;
        if(i < 0) i = 0;
        List<double[]> rectangle =  new ArrayList(L.subList(i, L.size()));
        for(i = 0; i < R.size(); ++i)if(Math.abs(dividingLine - R.get(i)[0]) > dist) break;
        rectangle.addAll(R.subList(0, i));
        sortByIndex(rectangle,0);
        return rectangle;
    }
    
    public static void sortByIndex(List<double[]> L, final int index){
        Collections.sort(L, new Comparator<double[]>() {
            @Override
            public int compare(double[]  C1, double[]  C2) {
                if(C1[index] > C2[index]) return 1;
                else if(C1[index] < C2[index]) return -1; 
                else return 0;
            }
        });
    }
    public static double HaversineDistance(double[] X, double[] Y) {
        if(X[0] == Y[0] && X[1] == Y[1]) return 0;
        final int R = 6371; // Radious of the earth
        double latDistance = toRad(Y[0]-X[0]);
        double lonDistance = toRad(Y[1]-X[1]);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(toRad(X[0])) * Math.cos(toRad(Y[0])) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c;  
        return distance*1000;
    }
    public static double equirectangularDistanceApproximation(double[] A, double[] B){
        final int R = 6371; // Radious of the earth
        double x = toRad(A[0]) - toRad(B[0]); double y = (toRad(A[1]) - toRad(B[1]))*Math.cos((toRad(A[0])+toRad(B[0]))*0.5);
        return R * Math.sqrt(Math.pow(x,2) +  Math.pow(y,2));
    }
     
    public static double toRad(double value) {
        return value * Math.PI / 180;
    }
    public static double toDegrees(double value) {
        return (value * 180) /Math.PI;
    }
    public static double[] latLonToMeters(double lat, double lon){
        //"Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913"
        double mx = tileSize()*(0.5+lon/360);
        double siny = Math.sin(lat * Math.PI / 180);
        siny = Math.min(Math.max(siny,-0.9999), 0.9999);
        double my = tileSize()*(0.5 - Math.log((1+siny)/(1-siny))/(4*Math.PI));
        return new double[]{mx, my};
    }
    
    public static double[] mergePoints(double[]A, double[]B){
        double lat = (A[0] + B[0])/2; double lon = (A[1] + B[1])/2;
        double direction = toDegrees(Math.atan2((Math.sin(toRad(A[2])) + Math.sin(toRad(B[2])))/2,
                                      (Math.cos(toRad(A[2])) + Math.cos(toRad(B[2])))/2));
        if(direction < 0) direction = 360 + direction;
        double speed = (A[3] + B[3])/2;
        return new double[]{lat,lon,direction,speed};
    }
    public static double[] mergePoints(ArrayList<double[]> A){
        double latACC, lonACC,  sinACC, cosACC, speedACC; 
        latACC= lonACC = sinACC= cosACC= speedACC =0;
        for(double[] d: A){
            latACC += d[0]; lonACC += d[1]; speedACC += d[3];
            sinACC += Math.sin(toRad(d[2])); cosACC += Math.cos(toRad(d[2]));
        }
        double lat = latACC/A.size(); double lon = lonACC/A.size();
        double direction = toDegrees(Math.atan2(sinACC/A.size(),cosACC/A.size()));
        if(direction < 0) direction = 360 + direction;
        double speed = speedACC/A.size();
        return new double[]{lat,lon,direction,speed};
    }
    public static String minutesToHours(String minutes){
        int min = (int) Float.parseFloat(minutes);
        String sign;
        if(min >= 0) sign = "+";
        else sign = "-";
        min = Math.abs(min);
        int hours = min / 60; //since both are ints, you get an int
        int minut = min % 60;
        String h = Integer.toString(hours); String m = Integer.toString(minut);
        if(hours < 10) h = "0" + h; if(minut < 10) m = "0" + m;
        return sign+h+":"+m;
    }
    
    public static double HoursToMinutes(String hour){
        if(hour != null && hour.length() == 6){
            double h = Double.parseDouble(hour.substring(1, 3))*60d + Double.parseDouble(hour.substring(4));
            if(hour.substring(0, 1).equals("+")) return h;
            else return -h;
        }if(hour != null && hour.length() == 8){
            return Double.parseDouble(hour.substring(0, 2))*60d + Double.parseDouble(hour.substring(3,5));
        }else if(hour != null && !hour.contains(":") && isDouble(hour) ){
            return Double.parseDouble(hour);
        }
        return 0;
    }
     
    public static double[] metersToPixels(double mx, double my, int zoom){
        //"Converts EPSG:900913 to pyramid pixel coordinates in given zoom level"
        double res = Resolution( zoom );
        double px = Math.floor(mx * res);
        double py = Math.floor(my * res);
        return new double[]{px,py};
    }
    
    public static double[] pixelsToMetersToLatLon(double px, double py, int zoom){
        double res = Resolution( zoom );
        double mx = px/res; double my = py/res;
        double lon = (mx/tileSize() - 0.5); lon = lon*360;
        double aux = (-my/tileSize() + 0.5)*4*Math.PI; 
        double aux2 = Math.pow(Math.E,aux); double siny = (aux2-1)/(aux2+1);
        double lat = Math.asin(siny)*180/(Math.PI);
        return new double[]{lat,lon};
    }
   
    
    public static double Resolution(int zoom){    return 1 << zoom;}
    
    public static boolean isDouble(String hour){return Pattern.matches(fpRegex, hour);}
    
    final static String Digits     = "(\\p{Digit}+)";
    final static String HexDigits  = "(\\p{XDigit}+)";

    // an exponent is 'e' or 'E' followed by an optionally 
    // signed decimal integer.
    final static String Exp        = "[eE][+-]?"+Digits;
    final static String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
             "[+-]?(" + // Optional sign character
             "NaN|" +           // "NaN" string
             "Infinity|" +      // "Infinity" string

             // A decimal floating-point string representing a finite positive
             // number without a leading sign has at most five basic pieces:
             // Digits . Digits ExponentPart FloatTypeSuffix
             // 
             // Since this method allows integer-only strings as input
             // in addition to strings of floating-point literals, the
             // two sub-patterns below are simplifications of the grammar
             // productions from the Java Language Specification, 2nd 
             // edition, section 3.10.2.

             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

             // . Digits ExponentPart_opt FloatTypeSuffix_opt
             "(\\.("+Digits+")("+Exp+")?)|"+

       // Hexadecimal strings
       "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +

        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

        ")[pP][+-]?" + Digits + "))" +
             "[fFdD]?))" +
             "[\\x00-\\x20]*");// Optional trailing "whitespace"

}

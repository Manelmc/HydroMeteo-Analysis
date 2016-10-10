/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manel
 */
public class FileParser {
    File file = new File("C:\\Users\\Manel\\Documents\\TFG\\survey_2015_05_16.txt");
    Scanner s;
    
    private void setFile(String file){
        try {
            this.file = new File(file);
            s = new Scanner(this.file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public FileParser() {

        try {
            this.s = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public FileParser(String file) {
        try {
            this.file = new File(file);
            this.s = new Scanner(this.file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList parseFile(String Path, Table table){
        setFile(Path);
        if(table == Table.TIDES) return parseTides();
        else{
            //s.nextLine();//needed?
            String f;
            ArrayList data = new ArrayList();
            while(s.hasNext()){
                f = s.nextLine();
                ArrayList<String> line = new ArrayList();
                while(f.contains(";")){
                    String aux = f.substring(0, f.indexOf(";"));
                    f = f.substring(f.indexOf(";")+1);
                    aux = aux.replace("/",""); aux = aux.replace(":",""); aux = aux.replace(",","."); 
                    line.add(aux);
                }
                if(table != Table.WIND){ 
                    line.add(f);
                    if(line.size() == 6){
                        ArrayList aux = new ArrayList();
                        int i = 0;
                        for(String dd : line){
                            if(i == 6){
                                aux.add("NaN"); aux.add("NaN"); aux.add("NaN"); i+=3;
                            }else if (i == 4){
                                aux.add("NaN"); ++i;
                            }
                            aux.add(dd);
                            ++i;
                        }
                        aux.add("NaN");
                        line = aux;
                    }
                }else{
                    String Coach = "Unknown";
                    if(Path.split("_").length > 3) Coach = Path.split("_")[2];
                    line.add(Coach);
                }
                if(!line.contains("dat")) data.add(line);
            }

            return data;
        }
    }
    private ArrayList parseTides(){
        s.nextLine();
        String f;
        ArrayList data = new ArrayList();
        while(s.hasNext()){
            f = s.nextLine();    
            ArrayList line = new ArrayList();
           //System.out.println( f.contains("\\t") + "   " + f.contains("\t"));
            while(f.contains("\t")){
                String aux = f.substring(0, f.indexOf("\t"));
                if(aux.isEmpty()) aux = "NULL";
                f = f.substring(f.indexOf("\t")+1);
                aux = aux.replace("/","");aux = aux.replace(":","");
                if(!aux.isEmpty())line.add(aux);
            }
            line.add(f);
            data.add(line);
        }
        return data;
        
    }
    
}
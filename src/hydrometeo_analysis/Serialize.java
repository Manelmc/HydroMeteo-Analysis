/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* 
* Aquesta classe es la encarregada de gestionar el guardat y càrrega d'objetes serialitzables.
* Els objectes a guardar han de implementar serializable.
* @author Albert Cerezo Llavero
*  
*/

public class Serialize {
	
	
	public static Object loadObject(String Path) throws FileNotFoundException, IOException, ClassNotFoundException{
            FileInputStream fis = null;
            fis = new FileInputStream(Path);
            ObjectInputStream in = new ObjectInputStream(fis);
            Object o = in.readObject();
            in.close();
            fis.close();
            return o;       
	}
	
	public static void SaveObject(Serializable objecte, String Path, String nomObjecte){
            FileOutputStream fos = null;
            try {
                File f = new File(Path);
                if(!f.exists()) {
                    String s1 = Path;
                    String s2 = "";
                    createFolder(s1, s2);
		}
                fos = new FileOutputStream(Path + "\\" + nomObjecte);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(objecte);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Serialize.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Serialize.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Serialize.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
	}
	
	public void deleteObject(String Path){
		File fichero = new File(Path);
		if(!fichero.delete()) {
                    try {
                        throw new IOException();
                    } catch (IOException ex) {
                        Logger.getLogger(Serialize.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
	}
	
	
	public static boolean createFolder(String Path, String nameFolder) {
		File folder = new File(Path);
		folder.setExecutable(true, false);
		folder.setReadable(true, false);
		folder.setWritable(true, false);
		return folder.mkdirs();
	}
}
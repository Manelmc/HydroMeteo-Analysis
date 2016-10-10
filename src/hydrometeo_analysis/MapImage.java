/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Manel
 */
public class MapImage {
    
    private URL url;
    private Image image;
    private Table table;
    private final GoogleMAP MAP = new GoogleMAP();
    private double[] centerInPixels;
    private ArrayList dataToDisplaySynthesized = new ArrayList();
    
    private class GoogleMAP{
        private final String head = "http://maps.googleapis.com/maps/api/staticmap?";
        private int zoom = 12;
        private double centerLAT = -22.914886666666668;
        private double centerLON = -43.098818333333334;
        private int sizeX = 600;
        private int sizeY = 600;
        protected String key = "AIzaSyB7fvJCzc1AIR_rIMxlWd7tM0gnlyQqR3I";
        
        public void setCoords(double centerLAT, double centerLON){
            this.centerLAT = centerLAT;
            this.centerLON = centerLON;
        }
        
        public void setSize(int sizeX, int sizeY){
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }
        
        public void setZoom(int zoom){
            this.zoom = zoom;
        }
    }
    
    private class BookMark{
        private final int xpix, ypix;
        private final Image Image;
        public BookMark(int xpix, int ypix, Image Image){
            this.xpix = xpix; this.ypix = ypix; this.Image = Image;
        }
    }
    private BufferedImage copyBufferedImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }  
    
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
    private ArrayList<BookMark> createSeaCurrentBookMark(ArrayList<double[]> data){
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = env.getDefaultScreenDevice();
    GraphicsConfiguration config = device.getDefaultConfiguration();
        ArrayList<BookMark> result = new ArrayList();
        ClassLoader cl = this.getClass().getClassLoader();
        Image bookMark;
        Image BII = config.createCompatibleImage(1,1,BufferedImage.TYPE_INT_ARGB);
        int sizeBookMarkX = 20; int sizeBookMarkY = 40;
        try {
            bookMark = ImageIO.read(cl.getResource("Images/SeaCurrent-Arrow.png"));
            BII = bookMark.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
        } catch (IOException ex) {
            Logger.getLogger(MapImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(double[] d: data){
            try {
                double[] p = fixDataToDisplay(d);
                double speed = p[3]; double direction = p[2];
                int xpix = (int) ((MAP.sizeX/2)-(sizeBookMarkX/2) + (p[0] - centerInPixels[0]));
                int ypix = (int) ((MAP.sizeY/2)-(sizeBookMarkY/2) + (p[1] - centerInPixels[1]));
                if((0 <= xpix && xpix < MAP.sizeX-(sizeBookMarkX/2)) && (0 <= ypix && ypix < MAP.sizeY-(sizeBookMarkY/2))){
                    Image combined = config.createCompatibleImage(sizeBookMarkX, sizeBookMarkY, BufferedImage.TYPE_INT_ARGB);
                    Image BI = rotate(BII, direction, config);
                    Graphics G = combined.getGraphics();
                    G.drawImage(BI, 0, 0, null);
                    String spd = Double.toString(speed);
                    int pixel = 0;
                    for(int i = 0; i < spd.length(); ++i){
                        String number = Character.toString(spd.charAt(i));
                        if(!number.equals("-")){
                            if(".".equals(number)) number = "dot";
                            Image BM =  ImageIO.read(cl.getResource("Images/" +number + ".png"));
                            BI = BM.getScaledInstance(5, 9, Image.SCALE_DEFAULT);
                            G.drawImage(BI, pixel, 20, null);
                            pixel += 5;
                        }
                    }
                    result.add(new BookMark(xpix,ypix,combined));
                }
            } catch (IOException ex) {
                Logger.getLogger(MapImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    private ArrayList<BookMark> createWindBookMark(ArrayList<double[]> data){
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        ArrayList<BookMark> result = new ArrayList();
        Image BI; int sizeBookMarkX = 30; int sizeBookMarkY = 20;
        ClassLoader cl = this.getClass().getClassLoader();
        Image bookMark1  = config.createCompatibleImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Image Knot5 = config.createCompatibleImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Image Knot10 = config.createCompatibleImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Image bookMark;
        Image Knot50 = config.createCompatibleImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Image Arrow = config.createCompatibleImage(1,1,BufferedImage.TYPE_INT_ARGB);
        try {
            bookMark1 = ImageIO.read(cl.getResource("Images/Calm.png"));
            Knot5 = ImageIO.read(cl.getResource("Images/5_knots.png"));
            Knot10 = ImageIO.read(cl.getResource("Images/10_knots.png"));
            bookMark =  ImageIO.read(cl.getResource("Images/50_knots.png"));
            Knot50 = bookMark.getScaledInstance(5, 8, Image.SCALE_DEFAULT);
            BufferedImage aux =  ImageIO.read(cl.getResource("Images/Basic-Arrow.png"));
            Arrow = aux.getSubimage(10, 0, 30, 20);
        } catch (IOException ex) {
            Logger.getLogger(MapImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(double[] d: data){
            double[] p = fixDataToDisplay(d);
            int knots = (int)Math.floor(p[3] + 0.5d); //knots = ThreadLocalRandom.current().nextInt(0, 101);
            double direction = p[2]; if(knots > 0) sizeBookMarkY = 2;
            int xpix = (int) ((MAP.sizeX/2)-(sizeBookMarkX/2) + (p[0] - centerInPixels[0]));
            int ypix = (int) ((MAP.sizeY/2)-(sizeBookMarkY/2) + (p[1] - centerInPixels[1]));
            if((0 <= xpix && xpix < MAP.sizeX-(sizeBookMarkX/2)) && (0 <= ypix && ypix < MAP.sizeY-(sizeBookMarkY/2))){
                sizeBookMarkY = 20;
                if(knots == 0){
                    BI = bookMark1.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
                }else{
                    Image combined = config.createCompatibleImage(sizeBookMarkX, sizeBookMarkY, BufferedImage.TYPE_INT_ARGB);
                    Graphics G = combined.getGraphics();
                    G.drawImage(Arrow, 0, 0, null);
                    int x = 0; int y = 4;
                    while(knots > 2){
                        if(knots - 50 >= -2){
                            knots -= 50;
                            G.drawImage(Knot50, x, y, null);
                            x += 5;
                        }else if(knots - 10 >= -2){
                            knots -= 10;
                            G.drawImage(Knot10, x, y, null);
                            x += 2;
                        }else if(knots - 5 >= -2){
                            if(x == 0) x += 4;
                            knots -= 5;
                            G.drawImage(Knot5, x, y, null);
                            x += 2;

                        }
                        x += 2;
                    }
                    BI = combined;
                }
            result.add(new BookMark(xpix, ypix,rotate(BI,direction+180d, config)));
            }
        }
        return result;
    }
    
    private String constructURL(){
      return MAP.head + "center=" + Double.toString(MAP.centerLAT) +","+ Double.toString(MAP.centerLON) 
                   + "&size=" + Integer.toString(MAP.sizeX) +"x"+ Integer.toString(MAP.sizeY) + "&sensor=true" 
                   + "&zoom=" + Integer.toString(MAP.zoom) + "&key=" + MAP.key;
    }
    
    private BufferedImage paintSelectedItem(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = 255;
                pixels[1] = 0;
                pixels[2] = 0;
                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }
        
        /**
    * Rotates an image. Actually rotates a new copy of the image.
    * 
    * @param img The image to be rotated
    * @param angle The angle in degrees
    * @return The rotated image
    */
    private BufferedImage rotate(Image img, double angle, GraphicsConfiguration config){
        double sin = Math.abs(Math.sin(Math.toRadians(angle))),
               cos = Math.abs(Math.cos(Math.toRadians(angle)));

        int w = img.getWidth(null), h = img.getHeight(null);

        int neww = (int) Math.floor(w*cos + h*sin),
            newh = (int) Math.floor(h*cos + w*sin);

        BufferedImage bimg =  config.createCompatibleImage(neww, newh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bimg.createGraphics();

        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(Math.toRadians(angle), w/2, h/2);
        g.drawImage(img,0,0, null);
        g.dispose();

        return bimg;
    }
    private void setURL(String Map_Url){
        try {
            url = new URL(Map_Url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MapImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public void display(JPanel mainPanel){
        JFrame frame = new JFrame();
        JLabel lblimage = new JLabel(new ImageIcon(image));
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(lblimage);
        frame.add(mainPanel);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
    
    public double[] getCenterInPixels(){
        return centerInPixels;
    }
    public int getZoom(){
        return MAP.zoom;
    }
    public Image getMapImage(double[] center){
        try {
            double lat = MAP.centerLAT; double lon = MAP.centerLON;
            if(center.length > 0){
                lat = center[0]; lon = center[1];
                MAP.setCoords(lat, lon);
            }
            double[] m = Calculus.latLonToMeters(lat,lon);
            double[] p = Calculus.metersToPixels(m[0],m[1], MAP.zoom);
            centerInPixels = p;
            setURL(constructURL());
            image = ImageIO.read(url); 
        } catch (IOException ex) {
            Logger.getLogger(MapImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    public void moveImageCenter(int incrX, int incrY){
        System.out.println("incremento: " + incrX + "," + incrY);
        centerInPixels[0] = centerInPixels[0] - incrX;
        centerInPixels[1] = centerInPixels[1] - incrY; 
        double[] p = Calculus.pixelsToMetersToLatLon(centerInPixels[0],centerInPixels[1],MAP.zoom);
        MAP.setCoords(p[0], p[1]);
    }
    
    public void synthesizeData(ArrayList<double[]> dataToDisplay, double minDistance){
        ArrayList<double[]> result = (ArrayList) dataToDisplay.clone(); 
        if(minDistance > 0 && result.size() > 1){
            /*
            for(int i = 0; i < result.size(); ++i){
                for(int j = 0; j < result.size(); ++j){
                    double[] x = (double[]) result.get(i);  double[] y = (double[]) result.get(j);
                    double distance = Calculus.HaversineDistance(x,y);
                    if(distance != 0  && distance < minDistance){
                        System.out.println(distance);
                        result.add(Calculus.mergePoints(x,y));
                        result.remove(x);
                        result.remove(y);
                        i = 0;
                        j = -1;
                    }
                }
            }*/
            boolean stop = false;
            while(!stop){
                Calculus.sortByIndex(result, 1);
                List<double[]> L = Calculus.getClosestPairOfPoints(result);
                if(L.size() < 2) stop = true;
                else{
                    double[] X = L.get(0),Y = L.get(1);
                    double auxDistance = Calculus.HaversineDistance(X, Y); 
                    //System.out.println(auxDistance);
                    if(auxDistance < minDistance){
                        result.add(Calculus.mergePoints(X,Y));
                        result.remove(X);
                        result.remove(Y);
                    }else stop = true;
                }
            }/*
            boolean stop = false;
            while(!stop){
                double auxDistance = minDistance; double[] X = null,Y = null;
                for(int i = 0; i < result.size(); ++i){
                    double[] x = (double[]) result.get(i);
                    for(int j = 0; j < result.size(); ++j){
                        double[] y = (double[]) result.get(j);
                        double distance = Calculus.HaversineDistance(x,y);
                        if(i != j && distance < auxDistance){X = x; Y = y; auxDistance = distance;}                      
                    }
                }
                if(auxDistance < minDistance){
                    System.out.println(auxDistance);
                    result.add(Calculus.mergePoints(X,Y));
                    result.remove(X);
                    result.remove(Y);
                }else{
                    stop = true;
                }
            }*/
        }
        dataToDisplaySynthesized = result;
    }
    private double[] fixDataToDisplay(double[] l){
        double[] m = Calculus.latLonToMeters(l[0],l[1]);
        double[] p = Calculus.metersToPixels(m[0],m[1], MAP.zoom);
        double direction = l[2] - 90;
        if(direction < 0) direction = 360 + direction;
        return new double[]{p[0],p[1],direction,l[3]};
    }
    public Image displayData(double[] selectedItem) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = env.getDefaultScreenDevice();
    GraphicsConfiguration config = device.getDefaultConfiguration();
        BufferedImage combined = config.createCompatibleImage(MAP.sizeX, MAP.sizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics G = combined.getGraphics();
        G.drawImage(image, 0, 0, null);
        ArrayList<BookMark> AllBookMarks = new ArrayList();
        if(table == Table.SEACURRENT) AllBookMarks = createSeaCurrentBookMark(dataToDisplaySynthesized);
        else if(table == Table.WIND) AllBookMarks = createWindBookMark(dataToDisplaySynthesized);
        for(BookMark b: AllBookMarks) G.drawImage(b.Image, b.xpix, b.ypix, null);
        return combined;
    }
    
    
    public void setMapSize(int sizeX, int sizeY){
        MAP.setSize(sizeX, sizeY);
        System.out.println(sizeX + ","+ sizeY);
    }
    
    public void setTable(Table table){
        this.table = table;
    }
    
    public void setZoom(boolean increase){
        if(increase && MAP.zoom < 20){
            MAP.setZoom(MAP.zoom+1);
        }else if(MAP.zoom > 0){
            MAP.setZoom(MAP.zoom-1);
        }
    }
}

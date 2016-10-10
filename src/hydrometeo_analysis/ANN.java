/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hydrometeo_analysis;

import java.io.Serializable;
import java.util.ArrayList;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 *
 * @author Manel
 */
public class ANN implements Serializable{
    private BasicNetwork network;
    private BasicNetwork networkDirection;
    private BasicNetwork networkSpeed;
    public String name;
    public String error;
    public double SC_INPUT[][];
    public double SC_OUTPUT[][];
    public double DIR_OUTPUT[][];
    public double SPD_OUTPUT[][];
    private boolean twoNets = true;
   
    public ANN(String name){
        this.name = name;
        this.network = new BasicNetwork ( ) ;
        this.network.addLayer (new BasicLayer ( null , true , 3 ) ) ;
        this.network.addLayer (new BasicLayer (new ActivationSigmoid ( ) , true , 6 ) ) ;
        this.network.addLayer (new BasicLayer (new ActivationSigmoid ( ) , false , 2 ) ) ;
        this.network.getStructure().finalizeStructure();
        this.network.reset();
        ///////////////////////////
        
        this.networkDirection = new BasicNetwork ( ) ;
        this.networkDirection.addLayer (new BasicLayer ( null , true , 3 ) ) ;
        this.networkDirection.addLayer (new BasicLayer (new ActivationSigmoid ( ) , true , 6 ) ) ;
        this.networkDirection.addLayer (new BasicLayer (new ActivationSigmoid ( ) , false , 1 ) ) ;
        this.networkDirection.getStructure().finalizeStructure();
        this.networkDirection.reset();
        
        this.networkSpeed = new BasicNetwork ( ) ;
        this.networkSpeed.addLayer (new BasicLayer ( null , true , 3 ) ) ;
        this.networkSpeed.addLayer (new BasicLayer (new ActivationSigmoid ( ) , true , 6 ) ) ;
        this.networkSpeed.addLayer (new BasicLayer (new ActivationSigmoid ( ) , false , 1 ) ) ;
        this.networkSpeed.getStructure().finalizeStructure();
        this.networkSpeed.reset();
    }
    public static double XOR_INPUT [ ] [ ] = {
        { 0.0 , 0.0 } ,
        { 1.0 , 0.0 } ,
        { 0.0 , 1.0 } ,
        { 1.0 , 1.0 } } ;
    public static double XOR_IDEAL [ ] [ ] = {
        { 0.0 } ,
        { 1.0} ,
        { 1.0} ,
        { 0.0 } } ;
    
    private double MINdirection = Double.MAX_VALUE; private double MAXdirection = Double.MIN_VALUE; 
    private double MINspeed = Double.MAX_VALUE; private double MAXspeed = Double.MIN_VALUE; 
    private double MINdistH = Double.MAX_VALUE; private double MAXdistH = Double.MIN_VALUE; 
    private double MINdt = Double.MAX_VALUE; private double MAXdt = Double.MIN_VALUE; 
    private double MINdh = Double.MAX_VALUE; private double MAXdh = Double.MIN_VALUE; 
    
    static double GroupDT1 = 0; static double GroupDT2 = 5d; static double GroupDT3 = 7d;
    static double GroupDH1 = 0; static double GroupDH2 = 0.5d; static double GroupDH3 = 0.8d;
    
    static double nH = 1; static double nL = 0;
    
    public void initializeRange(double[][] data){
        for(double[] d: data){
             if(d[0] > MAXdistH) MAXdistH = d[0]; if(d[0] < MINdistH) MINdistH = d[0];
             if(d[1] > MAXdt) MAXdt = d[1]; if(d[1] < MINdt) MINdt = d[1]; 
             if(d[2] > MAXdh) MAXdh = d[2]; if(d[2] < MINdh) MINdh = d[2];
        }
    }
    public void normalizeInput(double[][] data, boolean groupBy){
        //if(groupBy) groupBy(data);
        
        for(double[] d: data){
            double aux0 = d[0],aux1 = d[1],aux2 = d[2];
            if(aux0 > MAXdistH) aux0 = MAXdistH;  if(aux0 < MINdistH) aux0 = MINdistH;
            if(aux1 > MAXdt) aux1 = MAXdt;  if(aux1 < MINdt) aux1 = MINdt;
            if(aux2 > MAXdh) aux2 = MAXdh;  if(aux2 < MINdh) aux2 = MINdh;
            
            d[0] = normalizeEquation(aux0, MAXdistH, MINdistH ); 
            d[1] = normalizeEquation(aux1, MAXdt, MINdt ); 
            d[2] = normalizeEquation(aux2, MAXdh, MINdh ); 
        } 
    }
    public void normalizeOutput(double[][] data){
        for(double[] d: data){
             if(d[0] > MAXdirection) MAXdirection = d[0]; if(d[0] < MINdirection) MINdirection = d[0];
             if(d[1] > MAXspeed) MAXspeed = d[1]; if(d[1] < MINspeed) MINspeed = d[1];
        }
        int i = 0;
        for(double[] d: data){
            d[0] = normalizeEquation(d[0], MAXdirection, MINdirection ); 
            d[1] = normalizeEquation(d[1], MAXspeed, MINspeed ); 
            
            if(twoNets){
                DIR_OUTPUT[i][0] = d[0];
                SPD_OUTPUT[i][0] = d[1];
                ++i;
            }
            
        }
        
    }
    public static double normalizeEquation(double x, double dH, double dL){ return ((x-dL)*(nH-nL))/(dH-dL) +nL; }
    
    public static double deNormalizeEquation(double x, double dH, double dL){ return ((dL-dH)*x -nH*dL +dH*nL )/ (nL-nH);}
    
    public void groupBy(double[][] input){
        for(double[] d: input){
            if(d[1] > GroupDT3*60) d[1] = MAXdt;
            else if(d[1] > GroupDT2*60) d[1] = (MAXdt-MINdt)/2d + MINdt;
            else d[1] = MINdt;
            
            if(d[2] > GroupDH3) d[2] = MAXdh;
            else if(d[2] > GroupDH2) d[2] = (MAXdh-MINdh)/2d + MINdh;
            else d[2] = MINdh;
        }
    }
    
    public void parseTrainingArray(ArrayList<ArrayList<String> >  data){
        SC_INPUT = new double[data.size()][3]; SC_OUTPUT = new double[data.size()][2];
        DIR_OUTPUT = new double[data.size()][1];SPD_OUTPUT = new double[data.size()][1];
        int i = 0;
        for(ArrayList<String> A: data){
            SC_OUTPUT[i][0] = Double.parseDouble(A.get(5));
            SC_OUTPUT[i][1] = 0d;
            if(!A.get(9).equals("NaN")) SC_OUTPUT[i][1] = Math.abs(Double.parseDouble(A.get(9)));
            
            SC_INPUT[i][0] = Calculus.HoursToMinutes(A.get(11));
            SC_INPUT[i][1] = Calculus.HoursToMinutes(A.get(12));
            if( A.get(13)!= null )SC_INPUT[i][2] = Double.parseDouble(A.get(13));
            else SC_INPUT[i][2] = 0;
            ++i;
        }
    }
    
    public void errorDiagnostic(double[][] in, double[][] out) {
        printDATA(in,out);
       MLDataSet dataSet = new BasicMLDataSet(in, out);
       errorDiagnostic(dataSet);
    }
    public void errorDiagnostic(MLDataSet dataSet) {
        int count = 0;
        double totalError = 0;

        System.out.println("Network error: " + network.calculateError(dataSet));


        for(MLDataPair pair : dataSet) {
            MLData actual = network.compute(pair.getInput());
            System.out.println("Evaluating element " + count + " : " + pair.getInput().toString());

            for(int i=0;i<pair.getIdeal().size();i++) {
                double dH , dL ,delta;
                if(i == 0){
                    dH = MAXdirection;
                    dL = MINdirection;
                    //delta = deNormalizeEquation(actual.getData(i),dH,dL) - deNormalizeEquation(pair.getIdeal().getData(i),dH,dL);
                    //delta = Math.abs((delta + 180)%360 -180);
                }else{
                    dH = MAXspeed;
                    dL = MINspeed;   
                    //delta = Math.abs(deNormalizeEquation(actual.getData(i),dH,dL) - deNormalizeEquation(pair.getIdeal().getData(i),dH,dL));
                }
                delta = Math.abs(actual.getData(i) - pair.getIdeal().getData(i));
                totalError += delta*delta;
                count++;
                double currentError = totalError/count;
                System.out.println("\tIdeal: " + deNormalizeEquation(pair.getIdeal().getData(i),dH,dL) + ", Actual: " + deNormalizeEquation(actual.getData(i),dH,dL) + ", Delta: " + delta + ", Current Error: " + currentError);

            }
        }
    }
    
    public static void printDATA(double[][] in, double[][] out){
        for(double[] d: in){
            for(double dd: d) System.out.print(dd + " , ");
            System.out.println();
        }
        System.out.println("//////////////////////////////");
        
        for(double[] d: out){
            for(double dd: d) System.out.print(dd + " , ");
            System.out.println();
        }
    }
 

    public double trainANN(ArrayList<ArrayList<String> > data){
        if(data.size() > 0){
            parseTrainingArray(data);
            initializeRange(SC_INPUT);
            normalizeInput(SC_INPUT, true);
            normalizeOutput(SC_OUTPUT);
            //printDATA(SC_INPUT,SC_OUTPUT);
            MLDataSet trainingSet;
            double generalError;
            if(!twoNets){
                trainingSet = new BasicMLDataSet(SC_INPUT, SC_OUTPUT);
                generalError = backPropagation(network,trainingSet);
            }else{
                trainingSet = new BasicMLDataSet(SC_INPUT, DIR_OUTPUT);
                double DirError = backPropagation(networkDirection,trainingSet);
                trainingSet = new BasicMLDataSet(SC_INPUT, SPD_OUTPUT);
                double SpdError = backPropagation(networkSpeed,trainingSet);
                generalError = (DirError+SpdError)/2;
                System.out.println("Direction: " + (float)DirError*100f + "% ," + "Speed: " + (float)SpdError*100f + "%");
            }
            //errorDiagnostic(network, trainingSet);
            return generalError;
        } else return Double.MAX_VALUE;
    }
    
    private double backPropagation(BasicNetwork net, MLDataSet trainingSet){
        MLTrain train = new Backpropagation(net , trainingSet) ;
        int epoch = 1;
        train.iteration();
        double oldError = Double.MAX_VALUE; double newError = train.getError();
        while(Math.abs(oldError - newError) > 1e-9){ //9
            train.iteration();
            System.out.println("Epoch " + epoch + " Error:" + train.getError());
            ++ epoch;
            oldError = newError; newError = train.getError();
        }
        return newError;
    }
    
    public Object[] inputArrayToANN(ArrayList<double[]> inputArray){
        Object[] result = new Object[inputArray.size()];
        double[][] inputNormalized = new double[inputArray.size()][3];
        for(int i = 0; i < inputArray.size(); ++i) inputNormalized[i] = inputArray.get(i);
        //initializeRange(SC_INPUT);
        normalizeInput(inputNormalized, true);  
        int i = 0;
        for(double[] input: inputNormalized){
            MLData MLDataInput = new BasicMLData(3);
            MLDataInput.setData(0, input[0]);
            MLDataInput.setData(1, input[1]);
            MLDataInput.setData(2, input[2]);
            
            MLData outputDIR = networkDirection.compute(MLDataInput);
            MLData outputSPD = networkSpeed.compute(MLDataInput);
            if(!twoNets){
                MLData output = network.compute(MLDataInput);
                outputDIR = output;  outputSPD = output;
            }
            result[i] = new Object[]{Calculus.minutesToHours(Float.toString((float)deNormalizeEquation(input[0],MAXdistH,MINdistH))) ,
                                     Calculus.minutesToHours(Float.toString((float)deNormalizeEquation(input[1],MAXdt,MINdt ))),
                                     (float)deNormalizeEquation(input[2],MAXdh,MINdh ),
                                     "NaN","NaN",
                                     (float)deNormalizeEquation(outputDIR.getData(0),MAXdirection,MINdirection), 
                                     (float)deNormalizeEquation(outputSPD.getData(0),MAXspeed,MINspeed) };
            ++i;
        }   
        return result;
    }
    public void inputTrainingSetToANN( MLDataSet trainingSet){
        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet){
            final MLData output = network.compute(pair.getInput());
             System.out.println(deNormalizeEquation(pair.getInput().getData(0),MAXdistH,MINdistH) 
                        + "," + deNormalizeEquation(pair.getInput().getData(1),MAXdt,MINdt )
                        + "," + deNormalizeEquation(pair.getInput().getData(2),MAXdh,MINdh )
                     
             + ", actual=" + deNormalizeEquation(output.getData(0),MAXdirection,MINdirection) 
                      + ","+ deNormalizeEquation(output.getData(1),MAXspeed,MINspeed) 
                     
             + ", ideal=" + deNormalizeEquation(pair.getIdeal().getData(0),MAXdirection,MINdirection) 
                    + "," + deNormalizeEquation(pair.getIdeal().getData(1),MAXspeed,MINspeed));
        }
    }
    public static void BasicXOR(){
        BasicNetwork network = new BasicNetwork ( ) ;
        network.addLayer (new BasicLayer ( null , true , 2 ) ) ;
        network.addLayer (new BasicLayer (new ActivationSigmoid ( ) , true , 3 ) ) ;
        network.addLayer (new BasicLayer (new ActivationSigmoid ( ) , false , 1 ) ) ;
        network.getStructure().finalizeStructure();
        network.reset();
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
        MLTrain train = new ResilientPropagation(network, trainingSet);
        int epoch = 1;
        do{
           train.iteration();
           System.out.println("Epoch " + epoch + " Error:" + train.getError());
           ++ epoch;
        }while(train.getError() > 0.01);
        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet){
            final MLData output = network.compute(pair.getInput());
             System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) 
             + ", actual=" + output.getData(0) + ", ideal=" + pair.getIdeal().getData(0));
        }
    }
    
}

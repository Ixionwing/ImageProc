
import java.awt.*;
import java.awt.image.*;


public class ColorHist {
    
    private final int N = 256;
    private double[][] simMatrix;
    
    public ColorHist(){
        initializeSimMatrix();
    }
    
    private void initializeSimMatrix(){
        simMatrix = new double[N][N];
        
        for(int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                simMatrix[i][j] = 1.0 - Math.abs(((double)j - (double)i)/255.0);
            }
        }
    }
    
    public double[][] getGrayscale(double[][] img, double[][] sub){
        int r,g,b;
        int h = img.length;
        int w = img[0].length;
        double[][] gr = new double[h][w];
        Color c;
        
        if (sub == null){
            for (int i = 0; i < h; i++){
                for (int j = 0; j < w; j++){
                    r = g = b = (int)img[i][j];
                    c = new Color(r,g,b);
                    gr[i][j] = c.getRGB();
                }
            }
        }
        
        else {
            for (int i = 0; i < h; i++){
                for (int j = 0; j < w; j++){
                    r = g = b = (int)(img[i][j]) + (int)(sub[i][j]);
                    c = new Color(r,g,b);
                    gr[i][j] = c.getRGB();
                }
            }
        }
        
        return gr;
    }
    
    public double[][] getY(BufferedImage img){
        double[][] Y = new double[img.getHeight()][img.getWidth()];
        Color c;
        int r,g,b;
        
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                c = new Color(img.getRGB(j,i));
                r = c.getRed();
                g = c.getGreen();
                b = c.getBlue();
                Y[i][j] = r * 0.299 + g * 0.587 + b * 0.114;
            }
        }
            
        
        return Y;
    }
    
    public double[][] getY(double[][] img){
        int height = img.length;
        int width = img[0].length;
        double[][] Y = new double[height][width];
        Color c;
        int r,g,b;
        
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                c = new Color((int)img[i][j]);
                r = c.getRed();
                g = c.getGreen();
                b = c.getBlue();
                Y[i][j] = r * 0.299 + g * 0.587 + b * 0.114;
            }
        }
        
        return Y;
    }
    
    public double[][] getColorMatrix(BufferedImage img){
        double imgMatrix[][] = new double[img.getHeight()][img.getWidth()];
            
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                imgMatrix[i][j] = (double)(img.getRGB(j,i));
            }
        }
        
        return imgMatrix;
    }
    
    public double[] getHist(double[][] img, int mode){ //modes: 0 = greyscale, 1 = R, 2 = G, 3 = B
        double[] result = new double[N];
        int h = img.length;
        int w = img[0].length;
        Color c;
        
        if (mode == 0){
            for (int i = 0; i < h; i++){
                for(int j = 0; j < w; j++){
                    c = new Color((int)img[i][j]);
                    result[c.getRed()] += 1.0;
                }
            }
        }
        
        else{
            for (int i = 0; i < h; i++){
                for(int j = 0; j < w; j++){
                    c = new Color((int)img[i][j]);
                    if (mode == 1)
                        result[c.getRed()] += 1.0;
                    if (mode == 2)
                        result[c.getGreen()] += 1.0;
                    if (mode == 3)
                        result[c.getBlue()] += 1.0;
                }
            }
        }
        
        return result;
    }
    
    public double getNormCoefficient(BufferedImage img1, BufferedImage img2){
        double t1 = img1.getHeight() * img1.getWidth();
        double t2 = img2.getHeight() * img2.getWidth();
        
        return t1/t2;
    }
    
    public double getNormCoefficient(double[][] img1, double[][] img2){
        double t1 = img1.length * img1[0].length;
        double t2 = img2.length * img2[0].length;
       
        return t1/t2;
    }
    
    public double[][] transpose(double[][] arr){
        double[][] newArr = new double[arr[0].length][arr.length];
        
        for (int i = 0; i < arr.length; i++){
            for (int j = 0; j < arr[0].length; j++){
                newArr[j][i] = arr[i][j];
            }
        }
        
        return newArr;
    }
    
    public double[] vectorSimMult(double[] input){
        double[] result = new double[N];
        
        if (input.length != N)
            return null;
        
        for(int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                result[i] += input[j] * simMatrix[j][i];
            }
        }
        
        return result;
    }
    
    public double dotProduct(double[] in1, double[] in2){
        double result = 0.0;
        
        for(int i = 0; i < N; i++)
            result += in1[i]*in2[i];
        
        return result;
    }
    
    public double[] subtractVector(double[] in1, double[] in2){
        double[] result = new double[N];
        
        if (in1.length != N || in2.length != N) return null;
        
        for (int i = 0; i < N; i++){
            result[i] = in1[i] - in2[i];
        }
        
        return result;
    }
}
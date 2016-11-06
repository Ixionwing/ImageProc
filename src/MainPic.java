import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class MainPic{
 
    public static void main(String[] args) {
        BufferedImage img1 = null, img2 = null;
        String filename1, filename2;
        Scanner s = new Scanner(System.in);
        ColorHist chobj = new ColorHist();
        double[][] img1proc, img2proc;
        double[] diff;
        double[] trans;
        int colormode;
        final int N = 256;
        
        System.out.print("Please enter a filename (including extension): ");
        filename1 = s.nextLine();
        System.out.print("Please enter another filename (including extension): ");
        filename2 = s.nextLine();
        
        try {
           img1 = ImageIO.read(new File(filename1));
           img2 = ImageIO.read(new File(filename2));
        } catch (IOException e) {
           System.out.println("File not found. Closing program.");
           System.exit(0);
        }

        double nc = chobj.getNormCoefficient(img1, img2);
        
        System.out.println("[0] Greyscale\n[1] RGB");
        System.out.print("Choose a color mode: ");
        
        colormode = s.nextInt();
        
        if (colormode == 0){
            img1proc = chobj.getY(img1);
            img2proc = chobj.getY(img2);
            img1proc = chobj.getGrayscale(img1proc, null);
            img2proc = chobj.getGrayscale(img2proc, null);
            
            /*
            // test grayscale conversion
            
            BufferedImage newImg = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
            
            for (int i = 0; i < img1.getHeight(); i++){
                for (int j = 0; j < img1.getWidth(); j++){
                    newImg.setRGB(j, i, (int)img1proc[i][j]);
                }
            }
            
            try {
                ImageIO.write(newImg, "bmp", new File("wot.bmp"));
            } catch (IOException e) {
                System.exit(0);
            }
            */
            
            double[] img1hist = chobj.getHist(img1proc, 0);
            double[] img2hist = chobj.getHist(img2proc, 0);
            
            
            for(int i = 0; i < N; i++){
                img1hist[i] /= nc;
            }
            
            diff = chobj.subtractVector(img1hist, img2hist);
            
            /*
            // print all histogram differences
            for(i = 0; i < N; i++){
                System.out.println((int)img1hist[i] + " - " + (int)img2hist[i] + " = " + (int)diff[i]);
            }
            */
            
            trans = chobj.vectorSimMult(diff);
            
            System.out.println(chobj.dotProduct(trans, diff) / (img1.getWidth() * img1.getHeight() / nc));
            
        }
        
        else {
            img1proc = chobj.getColorMatrix(img1);
            img2proc = chobj.getColorMatrix(img2);
            
            double[] img1redhist = chobj.getHist(img1proc, 1);
            double[] img2redhist = chobj.getHist(img2proc, 1);
            double[] img1greenhist = chobj.getHist(img1proc, 2);
            double[] img2greenhist = chobj.getHist(img2proc, 2);
            double[] img1bluehist = chobj.getHist(img1proc, 3);
            double[] img2bluehist = chobj.getHist(img2proc, 3);
            
            for(int i = 0; i < N; i++){
                img1redhist[i] /= nc;
                img1greenhist[i] /= nc;
                img1bluehist[i] /= nc;
            }
            
            diff = chobj.subtractVector(img1redhist, img2redhist);
            trans = chobj.vectorSimMult(diff);
            System.out.println(chobj.dotProduct(trans, diff) / (img1.getWidth() * img1.getHeight() / nc));
            
            diff = chobj.subtractVector(img1greenhist, img2greenhist);
            trans = chobj.vectorSimMult(diff);
            System.out.println(chobj.dotProduct(trans, diff) / (img1.getWidth() * img1.getHeight() / nc));
            
            diff = chobj.subtractVector(img1bluehist, img2bluehist);
            trans = chobj.vectorSimMult(diff);
            System.out.println(chobj.dotProduct(trans, diff) / (img1.getWidth() * img1.getHeight() / nc));
        }
        
        s.close();
    }
}
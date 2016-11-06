
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;

public class MainVid {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
		ArrayList<BufferedImage> kfs = new ArrayList<BufferedImage>();
        String filepath1;
        Scanner s = new Scanner(System.in);
        ColorHist chobj = new ColorHist();
        int cthreshold, kthreshold;
        char log;
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
        
        System.out.print("Please enter a filename (including extension): ");
        filepath1 = s.nextLine();
        
        System.out.print("Enter isolated keyframe threshold (recommended 1000 < k < 2000):");
        kthreshold = s.nextInt();
        
        System.out.print("Enter cumulative keyframe threshold (recommended 17500 < c < 40000):");
        cthreshold = s.nextInt();
        
        s.nextLine(); // stream buffer
        
        System.out.print("Would you like to print frame by frame reports? (Y/N)");
        log = s.nextLine().toLowerCase().charAt(0);
        
        FFmpegFrameGrabber g = new FFmpegFrameGrabber(filepath1);
        filepath1 = filepath1.substring(0, filepath1.indexOf('.'));
        System.out.println("Output file path is: ./res/"+filepath1);
        g.setFormat("avi");
        Java2DFrameConverter converter = new Java2DFrameConverter();

		BufferedImage temp;
		Frame f;
        try {
        	
			g.start();
			int vidlength = g.getLengthInFrames();
			for (int i = 0; i < vidlength; i++){
				f = g.grab();
				temp = converter.convert(f);
				if (temp != null)
					imgs.add(imgCopy(temp));
	        }
			
			System.out.println(imgs.size() + " images created");
			
			g.stop();
			g.release();
			
			double[] prdiff = new double[imgs.size()-1];
			double[] pgdiff = new double[imgs.size()-1];
			double[] pbdiff = new double[imgs.size()-1];
			double[] krdiff = new double[imgs.size()-1];
			double[] kgdiff = new double[imgs.size()-1];
			double[] kbdiff = new double[imgs.size()-1];
			
			double pdiff = 0;
			double kdiff = 0;
			
			double[] img1redhist;
            double[] img2redhist;
            double[] img1greenhist;
            double[] img2greenhist;
            double[] img1bluehist;
            double[] img2bluehist;
            
            double[] diff = null;
            double[] trans = null;
			
			double[][] img1proc, img2proc;
			
			int h = imgs.get(0).getHeight();
			int w = imgs.get(0).getWidth();
			
			kfs.add(imgs.get(0));
			
			for(int i = 1; i < imgs.size(); i++){
				img1proc = chobj.getColorMatrix(imgs.get(i-1));
				img2proc = chobj.getColorMatrix(imgs.get(i));
				
				img1redhist = chobj.getHist(img1proc, 1);
	            img2redhist = chobj.getHist(img2proc, 1);
	            img1greenhist = chobj.getHist(img1proc, 2);
	            img2greenhist = chobj.getHist(img2proc, 2);
	            img1bluehist = chobj.getHist(img1proc, 3);
	            img2bluehist = chobj.getHist(img2proc, 3);
	            
	            for(int j = 0; j < 3; j++){
	            	if(j==0) diff = chobj.subtractVector(img1redhist, img2redhist);
	            	else if(j==1) diff = chobj.subtractVector(img1greenhist, img2greenhist);
	            	else if(j==2) diff = chobj.subtractVector(img1bluehist, img2bluehist);
	            	
	            	trans = chobj.vectorSimMult(diff);
	            	
		            if(j==0) prdiff[i-1] = chobj.dotProduct(trans,diff) / (w * h);
		            else if(j==1) pgdiff[i-1] = chobj.dotProduct(trans,diff) / (w * h);
		            else if(j==2) pbdiff[i-1] = chobj.dotProduct(trans,diff) / (w * h);
	            }
	            
	            if (kfs.get(kfs.size()-1) != imgs.get(i-1)){
	            	img1proc = chobj.getColorMatrix(kfs.get(kfs.size()-1));
	            	img1redhist = chobj.getHist(img1proc, 1);
		            img1greenhist = chobj.getHist(img1proc, 2);
		            img1bluehist = chobj.getHist(img1proc, 3);
	            	
		            for(int j = 0; j < 3; j++){
		            	if(j==0) diff = chobj.subtractVector(img1redhist, img2redhist);
		            	else if(j==1) diff = chobj.subtractVector(img1greenhist, img2greenhist);
		            	else if(j==2) diff = chobj.subtractVector(img1bluehist, img2bluehist);
		            	
		            	trans = chobj.vectorSimMult(diff);
		            	
			            if(j==0) krdiff[i-1] = chobj.dotProduct(trans,diff) / (w * h);
			            else if(j==1) kgdiff[i-1] = chobj.dotProduct(trans,diff) / (w * h);
			            else if(j==2) kbdiff[i-1] = chobj.dotProduct(trans,diff) / (w * h);
		            }
		            
		            pdiff += (prdiff[i-1] + pgdiff[i-1] + pbdiff[i-1])/3;
		            kdiff += (krdiff[i-1] + kgdiff[i-1] + kbdiff[i-1])/3;
		            
		            if(log == 'y')System.out.println("PR:" + (int)prdiff[i-1] + " PG:" + (int)pgdiff[i-1] + " PB:" + (int)pbdiff[i-1] + " KR:" + (int)krdiff[i-1] + " KG:" + (int)kgdiff[i-1] + " KB:" + (int)kbdiff[i-1] + " PDiff:" + (int)pdiff + " KDiff:" + (int)kdiff);
		            if (kdiff >= cthreshold && (krdiff[i-1] + kgdiff[i-1] + kbdiff[i-1])/3 > kthreshold){
		            		kfs.add(imgs.get(i-1));
		            		System.out.println("Keyframe found!");
		            		kdiff = 0;
		            		pdiff = 0;
		            }
	            }
			}
		
			
			System.out.println(kfs.size() + " keyframes found.");
			
			for(int i = 0; i < kfs.size(); i++){
				ImageIO.write(kfs.get(i), "bmp", new File("./res/"+filepath1+i+".bmp"));
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        s.close();
	}
	
	private static BufferedImage imgCopy(BufferedImage b){
		return new BufferedImage(b.getColorModel(),b.copyData(null),b.getColorModel().isAlphaPremultiplied(),null);
	}
	

}

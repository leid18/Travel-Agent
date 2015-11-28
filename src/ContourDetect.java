// This class is for contour detection
// The code will first compare all the empty files and create a contour sample of empty images
// This contour sample is used to reduce shadow and light changes of later detections

// I use the detectContours souce code given in lab 3 as reference

import java.io.File;
import java.util.List;
import java.util.Vector;

//import required OpenCV components

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ContourDetect {

	public Mat originImg;
	public Mat testImg;
	public Mat diffImg;
	public Mat diffEmpty;
	public Mat diff;
	public File emptyImg;
	public File[] emptyImgFiles;
	public Mat returnImg;

	
	public ContourDetect(String imgPath, Mat testImg){
		// Load a empty image as standard image
		originImg = Highgui.imread("files/empty/lc-00005.png");
		emptyImg = new File("files/empty");
		emptyImgFiles = emptyImg.listFiles();
		this.testImg = testImg;
		// create a new image object to store image difference
		diffImg = new Mat();
		// create a new image object to store image difference of empty images
		diffEmpty = new Mat();
		// create a new image object to store all the image difference of empty images
		diff = new Mat();
	}
	
	public void detectContours() throws InterruptedException {
	     // Load the Core OpenCV library by name
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
	     // Make the diff image object have the same size with other image objects
		Core.absdiff(originImg, originImg, diff);
		
		// Convert it to grey and threshold it
		Mat grey_diff = new Mat();		
        Imgproc.cvtColor(diff, grey_diff, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(grey_diff, diff, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, 
     							Imgproc.THRESH_BINARY_INV, 7, 10);
     
        // Clean it up using some morphological operations  
        Size ksize_diff = new Size(5,5);
        Mat kernel_diff =  Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, ksize_diff);
     
        Imgproc.morphologyEx(diff, diff, Imgproc.MORPH_CLOSE, kernel_diff);

        // Do difference detection for all the empty images
		for (File emptyImg : emptyImgFiles) {
        	
        	// if the file name ends with .jpg (JPEG) or .png (Portable Network Graphic)

        	if ((emptyImg.getName().endsWith(".png")) || (emptyImg.getName().endsWith(".jpg")))
        	{
        		// Load an image from each file (read and decode image file)

        		Mat img2 = Highgui.imread("files/empty/" + emptyImg.getName());
        		
			    // Compute the difference between the images
        		Core.absdiff(originImg, img2, diffEmpty);
        		
        		// Convert it to grey and threshold it
		        Mat grey_empty = new Mat();
		        Imgproc.cvtColor(diffEmpty, grey_empty, Imgproc.COLOR_BGR2GRAY);
		    
		        Imgproc.adaptiveThreshold(grey_empty, diffEmpty, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, 
		     							Imgproc.THRESH_BINARY_INV, 13, 25);
		     
		        // Clean it up using some morphological operations
		        Size ksize_empty = new Size(25,25);
		        Mat kernel_empty =  Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, ksize_empty);
		        Imgproc.morphologyEx(diffEmpty, diffEmpty, Imgproc.MORPH_CLOSE, kernel_empty);
		        // Add all differences to diff image
	        	Core.addWeighted(diff, 0.7, diffEmpty, 0.3, 0.0, diff);
        	}

		}
		 // Compute the difference between the images      	
         Core.absdiff(originImg, testImg, diffImg);	  
         
	     // now convert it to grey and threshold it
	     Mat grey = new Mat();
	     Imgproc.cvtColor(diffImg, grey, Imgproc.COLOR_BGR2GRAY);
	     Imgproc.adaptiveThreshold(grey, diffImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, 
	     							Imgproc.THRESH_BINARY_INV, 13, 25);
	     
	     // Clean it up using some morphological operations
	     Size ksize = new Size(25,25);
	     Mat kernel =  Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, ksize);
	     Imgproc.morphologyEx(diffImg, diffImg, Imgproc.MORPH_CLOSE, kernel);
	     
	     // Erase difference caused by shadows and light changes
	     for (int a = 0; a < diffImg.rows();a++){
	    	 for(int b = 0;b < diffImg.cols();b++){
	    		 double[] bgr = diffImg.get(a, b);
	    		 double[] bgr2 = diff.get(a, b);
	    		 if(bgr[0]==255 && bgr2[0]!=0){
	    			 double colour = 0.0;
	    		     diffImg.put(a, b, colour);
	    		     
	    		 }
	    	 }
	     }
	     
	     // Clone diffImg to pass to the main class
	     returnImg = diffImg.clone();
	     
	     // Find contours in diffImg
	     List<MatOfPoint> contours = new Vector<MatOfPoint>();
	     
	     Imgproc.findContours(diffImg, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	      
	     // Draw a black rectange outside each contours to highlight the differences
	     for (int i = 0; i < contours.size(); i++) {
	    	 MatOfPoint2f newContours = new MatOfPoint2f( contours.get(i).toArray() );
	        	double length = Imgproc.arcLength(newContours, true);
	        	Rect rect = Imgproc.boundingRect(contours.get(i));
	        	
				Core.rectangle(testImg,
			             new Point(rect.x,rect.y),
			             new Point(rect.x + rect.width,
			            		 rect.y + rect.height),
			             new Scalar (0,0,0), 2, 1, 0);
	        }
	
	}
	
	public Mat getDiffImg(){
		return returnImg;// Return differences to Main class
	}
}

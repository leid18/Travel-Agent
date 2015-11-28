// This work is done by using eclipse on Mac.
// This work requires an OpenCV library


// This Main class is to draw zones and do the monitoring
// This class does not contain a main method.
// To use this class, please run test classes.
// There are eight test classes for different individual events and combination events 
// plus one testUnseen class for unseen image test.

// To run the Main method, a String input is needed to define the image folder of the sample images
// The folder to store all the sample images is the "files" folder inside the project folder
// In the "files" folder, nine folders have been created for all the tests.
// Put images in the certain folder and run the corresponding test.

// I used the PointInPolygonTest LoopImageFiles source codes given in the assignment as references

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Main {

	private Point[] zoneA; //zone A is the zone for railway track
	private Point[] zoneB; //zone B and zone B1 are zones for entering paths
	private Point[] zoneB1;
	private Point[] zoneC; //zone C are zones for leaving paths
	private Point[] zoneC1;
	private Point[] zoneD; //zone D is the zone for barrier
	private MatOfPoint contour1; // Create a contour for zone A
	private MatOfPoint contour2; // Create a contour for zone B
	private MatOfPoint contour3; // Create a contour for zone B
	private MatOfPoint contour4; // Create a contour for zone C
	private MatOfPoint contour5;// Create a contour for zone C
	private MatOfPoint contour6;// Create a contour for zone D
	private Imshow ims;
	private File dir;
	private String folderPath;
	private String imagePath;
	private File[] directoryListing;
	
	// Create counters for each event
	private int enterCount;
	private int railCount;
	private int leaveCount;
	private int barrierCount;
	private Mat diff;
	private Mat inputImage;
	
	
	public Main(String imageFolder){
		zoneA = new Point[4];
	    zoneB = new Point[4];
	    zoneB1 = new Point[3];
	    zoneC = new Point[4];
	    zoneC1 = new Point[3];
	    zoneD = new Point[4];
	    zoneA[0] = new Point(-50,480);
	    zoneA[1] = new Point(704,90);
	    zoneA[2] = new Point(704,195);
	    zoneA[3] = new Point(250,480);
	    zoneB[0] = new Point(-200,0);
	    zoneB[1] = new Point(30,0);
	    zoneB[2] = new Point(420,215);
	    zoneB[3] = new Point(210,325);
	    zoneB1[0] = new Point(530,316);
	    zoneB1[1] = new Point(704,195);
	    zoneB1[2] = new Point(704,430);
	    zoneC[0] = new Point(30,0);
	    zoneC[1] = new Point(160,0);
	    zoneC[2] = new Point(558,150);
	    zoneC[3] = new Point(420,215);
	    zoneC1[0] = new Point(275,478);
	    zoneC1[1] = new Point(530,316);
	    zoneC1[2] = new Point(740,480);
	    zoneD[0] = new Point(120,255);
	    zoneD[1] = new Point(85,230);
	    zoneD[2] = new Point(25,255);
	    zoneD[3] = new Point(25,295);
		ims = new Imshow("Next Image ...");
		
		// Define the folder path of the sample images.
		// All the sample images are stored in the "files" folder under the project folder
		folderPath = "files/";
		imagePath = folderPath + imageFolder;
		dir = new File(imagePath);
	    directoryListing = dir.listFiles();
	    
		
	}
	
	public void main() throws InterruptedException {
		
		// load the Core OpenCV library by name
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    if (directoryListing != null) {
	        for (File imgFile : directoryListing) {
	        	
	        	// if the file name ends with .jpg (JPEG) or .png (Portable Network Graphic)

	        	if ((imgFile.getName().endsWith(".png")) || (imgFile.getName().endsWith(".jpg")))
	        	{
	        		// load an image from each file (read and decode image file)
	        		inputImage = Highgui.imread(imagePath + "/" + imgFile.getName());

	        		// Create the new ContourDetect object 
	        		ContourDetect cd = new ContourDetect(imagePath,inputImage);
	        		// Run the detectContours function
	        		cd.detectContours();
	        		
	        		// convert the contours to java list of OpenCV MatOfPoint 
	                // objects as this is what the draw function requires
	        		contour1 = new MatOfPoint(zoneA);
	        		List<MatOfPoint> contourListA = new ArrayList<MatOfPoint>();
	        		contourListA.add(contour1);
	        		contour2 = new MatOfPoint(zoneB);
	        		List<MatOfPoint> contourListB = new ArrayList<MatOfPoint>();
	        		contourListB.add(contour2);
	        		contour3 = new MatOfPoint(zoneB1);
	        		List<MatOfPoint> contourListB1 = new ArrayList<MatOfPoint>();
	        		contourListB1.add(contour3);
	        		contour4 = new MatOfPoint(zoneC);
	        		List<MatOfPoint> contourListC = new ArrayList<MatOfPoint>();
	        		contourListC.add(contour4);
	        		contour5 = new MatOfPoint(zoneC1);
	        		List<MatOfPoint> contourListC1 = new ArrayList<MatOfPoint>();
	        		contourListC1.add(contour5);
	        		contour6 = new MatOfPoint(zoneD);
	        		List<MatOfPoint> contourListD = new ArrayList<MatOfPoint>();
	        		contourListD.add(contour6);
	        		
	        		//Create the MatOfPoint2f objects for the polygon test
	        		MatOfPoint2f zoneAPoint2f = new MatOfPoint2f(zoneA);
	        		MatOfPoint2f zoneBPoint2f = new MatOfPoint2f(zoneB);
	        		MatOfPoint2f zoneB1Point2f = new MatOfPoint2f(zoneB1);
	        		MatOfPoint2f zoneCPoint2f = new MatOfPoint2f(zoneC);
	        		MatOfPoint2f zoneC1Point2f = new MatOfPoint2f(zoneC1);
	        		MatOfPoint2f zoneDPoint2f = new MatOfPoint2f(zoneD);
	        		
	        		// draw the contours   
	        		Imgproc.drawContours(inputImage, contourListA, -1, new Scalar(0,0,255), 2);
	        		Imgproc.drawContours(inputImage, contourListB, -1, new Scalar(0,255,0), 2);
	        		Imgproc.drawContours(inputImage, contourListB1, -1, new Scalar(0,255,0), 2);
	        		Imgproc.drawContours(inputImage, contourListC, -1, new Scalar(255,0,0), 2);
	        		Imgproc.drawContours(inputImage, contourListC1, -1, new Scalar(255,0,0), 2);
	        		
	        		// Get the binary different image after Contour Detect
	        		diff = cd.getDiffImg();
	        		
	        		// Get all the white pixels from the diff image
	        		// Perform point of polygon test. If one point is inside a zone, add one to the counter for that zone
	       	        for (int a = 0; a < diff.rows();a++){
	    	    	    for(int b = 0;b < diff.cols();b++){
	    	    		    double[] bgr = diff.get(a, b);
	    	    		    if(bgr[0] == 255){
		    	    		    Point p = new Point(b,a);
		    	    		    if (Imgproc.pointPolygonTest(zoneAPoint2f,p, false) > 0)
	        			        { 
	        						railCount++;
	        			        }
		    	    		    if (Imgproc.pointPolygonTest(zoneBPoint2f,p, false) > 0)
	        			        { 
	        						enterCount++;
	        			        }
	        					if (Imgproc.pointPolygonTest(zoneB1Point2f,p, false) > 0)
	        			        { 
	        						enterCount++;
	        			        }
		    	    		    if (Imgproc.pointPolygonTest(zoneCPoint2f,p, false) > 0)
	        			        { 
	        						leaveCount++;
	        			        }
		    	    		    if (Imgproc.pointPolygonTest(zoneC1Point2f,p, false) > 0)
	        			        { 
	        						leaveCount++;
	        			        }
		    	    		    if (Imgproc.pointPolygonTest(zoneDPoint2f,p, false) > 0)
	        			        { 
	        						barrierCount++;
	        			        }
	    	    		    }
	    	    		 }

	       	        }
	       	        
	       	        // Define the conditions for each events.
	       	        // If a event is happened, print it out on the console screen as follows:
	       	        // filename : event N
	       	        if(enterCount<800 && leaveCount<800 && railCount<800){
	       	        	System.out.println(imgFile.getName()+" : Event 0 (No Event)");
	       	        }
	       	        else
	       	        {
		        		if(railCount>12000){
		        			System.out.println(imgFile.getName()+" : Event E (Railway track being used)");
		        		}else if(railCount>6630 &&enterCount > 10000 && leaveCount >= 4545){
		        				System.out.println(imgFile.getName()+" : Event E (Railway track being used)");
		        			}else
			        		{
			        			if(enterCount>=800) {
			        				System.out.println(imgFile.getName()+" : Event B (Vehicle/pedestrain entering)");
			        			}
			        			if(leaveCount>=800){
			        				System.out.println(imgFile.getName()+" : Event C (Vehicle/pedestrain leaving)");
			        			}
			        			if(railCount>=800 && railCount<=12000){
			        				System.out.println(imgFile.getName()+" : Event A (Railway track not clear of road/pedestrain traffic)");
			        			}
			        			if(barrierCount>13){
			        				System.out.println(imgFile.getName()+" : Event D (Barrier deployed)");
			        			}
			        		}
		        	}
	       	        System.out.println("===========================================================");

	       	        // display image with a delay of 800ms
	        		ims.showImage(inputImage);
	        		// Zero all counters
	        		railCount = 0;
	       	        enterCount = 0;
	       	        leaveCount = 0;
	       	        barrierCount = 0;
	       	        Thread.sleep(800);
	       	        


	        	}
	        }
	      } else {
	    	  System.out.println( "Could not get listing for directory: " + imagePath);
	      }		
	}


}

package com.example.test;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends Activity {

	
	private Bitmap bitmapObject;
	private Bitmap bitmapScene;
	//FeatureDetector featureDetector;
	MatOfKeyPoint keyObject;
	MatOfKeyPoint keyScene;
	Mat matObject;
    Mat matScene;
	
	private ImageView ivObject;
	private ImageView ivScene;
	
	final static String TAG = "MAT";
	
	static {
	    if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    	Log.i(TAG,  "init failed");
	    }
	}

	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
	
	public void onManagerConnected(int status) {
	   switch (status) {
	      case LoaderCallbackInterface.SUCCESS:
	       {
	      Log.i(TAG, "OpenCV loaded successfully");
	      
	       } break;
	       default:
	       {
	      super.onManagerConnected(status);
	       } break;
	   }
	}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.i(TAG, "onCreate");
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        
        
        ivObject = (ImageView)findViewById(R.id.object);
        ivScene = (ImageView)findViewById(R.id.scene);
        
        matObject = new Mat();
        matScene = new Mat();
        
        //read in pic
        Bitmap tmpO = BitmapFactory.decodeResource(getResources(), 
    			R.drawable.kdj);
    	bitmapObject = tmpO.copy(Bitmap.Config.ARGB_8888, false);
    	Bitmap tmpS = BitmapFactory.decodeResource(getResources(), 
    			R.drawable.kdjshop4);
    	bitmapScene = tmpS.copy(Bitmap.Config.ARGB_8888, false);
    	//change bitmap to mat
    	Utils.bitmapToMat(bitmapObject, matObject);
    	Utils.bitmapToMat(bitmapScene, matScene);
        
    	
        ivObject.setImageBitmap(bitmapObject);
    	ivScene.setImageBitmap(bitmapScene);
    	
        //declare keypoints
        keyObject = new MatOfKeyPoint();
        keyScene = new MatOfKeyPoint();
        
        //feature detection using orb
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
		featureDetector.detect(matObject, keyObject);    	
		featureDetector.detect(matScene, keyScene);
		
		//change original pictures channels
		Log.i(TAG , "channels: " + matObject.channels());
		Mat rgbObejetct = new Mat();
		Mat rgbScene = new Mat();
		Imgproc.cvtColor(matObject, rgbObejetct, Imgproc.COLOR_RGBA2BGR);
		Imgproc.cvtColor(matScene, rgbScene, Imgproc.COLOR_RGBA2BGR);
		Log.i(TAG , "rgb1 channels: " + rgbObejetct.channels());
		//draw key points
		//drawKeyPoints();
		
		//descriptor
		Mat descriptorObject = new Mat();
		Mat descriptorScene = new Mat();
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		extractor.compute(matObject, keyObject, descriptorObject);
		extractor.compute(matScene, keyScene, descriptorScene);
		
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descriptorScene, descriptorObject, matches);
		
		Mat outImg = new Mat();
		Features2d.drawMatches(rgbObejetct, keyObject, rgbScene, keyScene, matches, outImg);
		
		Bitmap outBitmap = Bitmap.createBitmap(outImg.width(), outImg.height(), Config.ARGB_8888);
		Utils.matToBitmap(outImg, outBitmap);
		
		ivObject.setImageBitmap(outBitmap);
    	
		
		double max_dist = 0; double min_dist = 1000;
		for (int i = 0; i < descriptorObject.rows(); i ++){
			double dist = matches.toList().get(i).distance;
			Log.v(TAG,  i + " :" + dist);
			if( dist < min_dist ) min_dist = dist;
		    if( dist > max_dist ) max_dist = dist;
		}
		
		Log.v(TAG, "Max dist: " + max_dist);
		Log.v(TAG, "Min dist: " + min_dist);
		
		List<DMatch> goodMatches = new ArrayList<DMatch>();
		for (int i = 0; i < descriptorObject.rows(); i++){
			if (matches.toList().get(i).distance < 2 * min_dist){
				DMatch tmp = matches.toList().get(i);
				goodMatches.add(tmp);
			}
		}
		MatOfDMatch goodM = new MatOfDMatch();
		goodM.fromList(goodMatches);
		for (int i = 0; i < goodM.toList().size(); i++){
			Log.v(TAG, "< " + i + " > " + "Key 1: " + goodM.toList().get(i).queryIdx +" -- Key 2: " + goodM.toList().get(i).trainIdx);
		}
		
		//Scalar color = new Scalar(255, 0, 0);
		
    	//Utils.matToBitmap(keyObject, bitmapObjectNew);
    	//Utils.matToBitmap(keyScene, bitmapSceneNew);
    	
    	
    	//ivObject.setImageBitmap(bitmapObjectNew);
   
    	
    	//ivScene.setImageBitmap(bitmapSceneNew);
        
        
    	
    }
    /*
     *
     *这个方法自己可以单独试试看什么效果
    private void drawKeyPoints(){
    	
    	
		
		Mat outObject =  new Mat();
		Mat outScene = new Mat();
		Features2d.drawKeypoints(rgbObejetct, keyObject, outObject);//, color, Features2d.DRAW_RICH_KEYPOINTS);
    	Features2d.drawKeypoints(rgbScene, keyScene, outScene);
    	Bitmap bitmapObjectNew = Bitmap.createBitmap(rgbObejetct.width(), rgbObejetct.height(), Config.ARGB_8888);
    	Bitmap bitmapSceneNew = Bitmap.createBitmap(rgbScene.width(), rgbScene.height(), Config.ARGB_8888);
    	Utils.matToBitmap(outObject, bitmapObjectNew);
    	Utils.matToBitmap(outScene, bitmapSceneNew);
    	
    	ivObject.setImageBitmap(bitmapObjectNew);
    	ivScene.setImageBitmap(bitmapSceneNew);
    }
    
    private void readInPic(){
    	
    	
        Bitmap tmpO = BitmapFactory.decodeResource(getResources(), 
    			R.drawable.kdj);
    	bitmapObject = tmpO.copy(Bitmap.Config.ARGB_8888, false);
    	Bitmap tmpS = BitmapFactory.decodeResource(getResources(), 
    			R.drawable.kdjshop);
    	bitmapScene = tmpS.copy(Bitmap.Config.ARGB_8888, false);
    	
    	Utils.bitmapToMat(bitmapObject, matObject);
    	Utils.bitmapToMat(bitmapScene, matScene);
    	
    	
    }*/

}

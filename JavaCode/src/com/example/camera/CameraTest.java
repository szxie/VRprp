package com.example.camera;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.CameraInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CameraTest extends Activity {
	//private final static String TAG = "MakePhotoActivity";
	public static final String TAG = "CAMERA";
	private Camera mCamera;
	private CameraPreview mPreview; 
	private int mCameraId = 0;
	private PhotoHandler mPicCallback;
	private LocationClient mLocationClient = null;
	private SensorManager mSensorManager;
	public ArrayList<HashMap<String, String>> mInterests = 
    		new ArrayList<HashMap<String,String>>();
	private Context mContext;
	private ShutterCallback mShutterCallback;
	private ToneGenerator mTone;
	
	private double mLatitude;
	private double mLontitude;
	private String mAddress;
	private String mPoi;
	private double mLatitudeOfPic;
	private double mLontitudeOfPic;
	private String mAddressOfPic;
	private String mPoiOfPic;
	private float mAnglex;
	private float mAngley;
	private float mAnglez;
	private float mAnglexOfPic;
	private float mAngleyOfPic;
	private float mAnglezOfPic;
	private String mPathOfpic;
	
	
	private static final int UPDATE_TIME = 5000;
	
	private Button mCaptureButton;
	private Button mSetPoiButton;
	private Button mDrawPoiButton;
	private FrameLayout mPreviewLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mLocationClient = new LocationClient(getApplicationContext());
		
		//set loction client option
		LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);		
        option.setCoorType("gcj02");		
        option.setPriority(LocationClientOption.NetWorkFirst);	
        //option.setProdName("LocationDemo");	
        option.setScanSpan(UPDATE_TIME);
        option.setPoiNumber(10);
        option.setPoiDistance(1000);
        option.setPoiExtraInfo(true);
        mLocationClient.setLocOption(option);
        
        mLocationClient.registerLocationListener(new BDLocationListener(){

			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				
				
			}

			public void onReceivePoi(BDLocation poiLocation) {
				// TODO Auto-generated method stub
				mLatitude = poiLocation.getLatitude();
				mLontitude = poiLocation.getLongitude();
				mPoi = poiLocation.getPoi();
				mAddress = poiLocation.getAddrStr();
				Log.v(TAG, "info:"+ mLatitude + ", " + mLontitude + "\n" +mAddress 
						+ "\n" + mPoi );
			}
        	
        });
        mLocationClient.start();

        
		// do we have a camera?
		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
					.show();
		} else {
		}
			//cameraId = findFrontFacingCamera();
		mCameraId = findBackFacingCamera();
		mCamera = getCameraInstance(mCameraId);
		mCamera.setDisplayOrientation(90);
		Parameters parameters = mCamera.getParameters();
		List<String> focusModes = parameters.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
		{
		    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}
		mCamera.setParameters(parameters);
		//setCameraDisplayOrientation(this, cameraId, camera);
			
		if (mCameraId < 0) {
			Toast.makeText(this, "No front facing camera found.",
					Toast.LENGTH_LONG).show();
		}
			
		mPreview = new CameraPreview(this, mCamera); 
		mPicCallback = new PhotoHandler(getApplicationContext());
		mShutterCallback = new ShutterCallback() {
			
			public void onShutter() {
				if(mTone == null)
                //发出提示用户的声音
                mTone = new ToneGenerator(AudioManager.STREAM_MUSIC,
                                ToneGenerator.MAX_VOLUME);
				mTone.startTone(ToneGenerator.TONE_PROP_BEEP2);
			}
		};
			
		mPreviewLayout = (FrameLayout) findViewById(R.id.camera_preview); 
	    mCaptureButton = (Button) findViewById(R.id.btn_capture); 
	    mSetPoiButton = (Button)findViewById(R.id.set_poi);
	    mDrawPoiButton = (Button)findViewById(R.id.draw_poi);
	        
	    mCaptureButton.setOnClickListener(new View.OnClickListener() {
						
					

					public void onClick(View v) {
						mLatitudeOfPic = mLatitude;
						mLontitudeOfPic = mLontitude;
						mAddressOfPic = mAddress;
						mPoiOfPic = mPoi;
						
						mAnglexOfPic = mAnglex;
						mAngleyOfPic = mAngley;
						mAnglezOfPic = mAnglez;
						
						Log.v(TAG, "PIC INFO:"+ mLatitudeOfPic + ", " + mLontitudeOfPic 
								+ " " + mAnglexOfPic 
								+ "\n" +mAddressOfPic 
								+ "\n" + mPathOfpic
								+ "\n" + mPoiOfPic);
						mCamera.takePicture(mShutterCallback, null,mPicCallback);
						
						
					}
				});
	    mSetPoiButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLocationClient.isStarted()){
					Log.v(TAG, "location client is started");
				}
				if (mLocationClient != null && mLocationClient.isStarted()) {
					mLocationClient.requestPoi();
				}
				
			}
		});
	    
	    mDrawPoiButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
	            mPathOfpic = PhotoHandler.getFilePath();
	            Log.v(TAG, "PIC PATH :" + mPathOfpic);
	            // TODO Auto-generated method stub
	            if (mPathOfpic != "") {
	            	Intent intent = new Intent();
	             	intent.setClass(CameraTest.this, ShowImage.class);
	   		                
	             	Bundle bundle = new Bundle();
	             	bundle.putString("ImagePath", mPathOfpic);
	             	bundle.putDouble("latitude", mLatitudeOfPic);
	             	bundle.putDouble("lontitude", mLontitudeOfPic);
	             	bundle.putString("poi"	, mPoiOfPic);
	             	bundle.putDouble("anglex", mAnglexOfPic);
	             	bundle.putDouble("angley", mAngleyOfPic);
	             	bundle.putDouble("anglez", mAnglezOfPic);
	             	bundle.putString("address", mAddressOfPic);
	             	intent.putExtras(bundle);
	             	startActivity(intent); 
	             	} else {
	             		Toast.makeText(CameraTest.this, "Please Take photo first" , Toast.LENGTH_LONG)
	   							.show();
	             	}
	   				 
	             }
		});
	        
	     mPreviewLayout.addView(mPreview); 
		
		
	}
	
	
	

	public static Camera getCameraInstance(int cameraID){ 
	    Camera c = null; 
	    try { 
	        c = Camera.open(cameraID); // 试图获取Camera实例
	    } 
	    catch (Exception e){ 
	    	Log.e(TAG, "camera can not use");
	    } 
	    return c; // 
	}

	public void dealJson(String s) throws JSONException{
		JSONObject str = new JSONObject(s);
		String arr = str.getString("p");
		JSONArray arrjson = new JSONArray(arr);
		 
		for (int i = 0; i<arrjson.length(); i++){
			JSONObject jo = (JSONObject) arrjson.get(i);
			Log.v(TAG,  "No.:" + i + " : ");
			HashMap<String, String> tmpMap = new HashMap<String, String>();
			tmpMap.put("name", jo.getString("name"));
			tmpMap.put("addr", jo.getString("addr"));
			tmpMap.put("x", jo.getString("x"));
			tmpMap.put("y", jo.getString("y"));
			tmpMap.put("dis", jo.getString("dis"));
			tmpMap.put("tel", jo.getString("tel"));
			mInterests.add(tmpMap);
			/*
			Log.v(TAG,  jo.getString("addr"));
			Log.v(TAG,  jo.getString("dis"));
			Log.v(TAG,  jo.getString("x"));
			Log.v(TAG,  jo.getString("y"));
			Log.v(TAG,  jo.getString("name"));
			Log.v(TAG,  jo.getString("tel"));
			*/
		}
		
	}
    
    
    /*
    public static void setCameraDisplayOrientation ( Activity activity , 
    		int cameraId , Camera camera ){
    	CameraInfo info = new android.hardware.Camera.CameraInfo(); 
    	Camera.getCameraInfo ( cameraId , info );
    	int rotation = activity.getWindowManager ().getDefaultDisplay ().getRotation ();
    	int degrees = 0 ; 
    	switch ( rotation ){ 
    		case Surface.ROTATION_0 : 
    			degrees = 0 ; 
    			break ; 
    		case Surface.ROTATION_90 : 
    			degrees = 90 ;
    			break ; 
    		case Surface.ROTATION_180 : 
    			degrees = 180 ; 
    			break ; 
    		case Surface.ROTATION_270 : 
    			degrees = 270 ; 
    			break ;
    	} 
    	Log.v(TAG, "degree: " + degrees);
    	
    	int result = 0 ; 
    	if ( info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT ){ 
    		result = ( info.orientation + degrees ) % 360 ;
    		result = ( 360 - result ) % 360 ; // compensate the mirror 
    	} else {
    	  // back-facing result = ( info.orientation - degrees + 360 ) % 360 ; 
    	}
    	Log.v(TAG, "result" + result);
    	camera.setDisplayOrientation ( result );
    }
    
    protected void setDisplayOrientation(Camera camera, int angle){
        Method downPolymorphic;
        try
        {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        }
        catch (Exception e1)
        {
        }
    }
    */
	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				Log.d(TAG, "Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
	
	private int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				Log.d(TAG, "Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	private final SensorEventListener selistener = new SensorEventListener(){
        public void onSensorChanged(SensorEvent event) {
        	mAnglex = event.values[SensorManager.DATA_X];
        	mAngley = event.values[SensorManager.DATA_Y];
        	mAnglez = event.values[SensorManager.DATA_Z];
         	//Log.v(TAG, "angle is : " + mAngle);
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy){
        	
        }

    };
    
	protected void onResume(){
		
		mSensorManager.registerListener(selistener, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
				SensorManager.SENSOR_DELAY_NORMAL);
	    super.onResume();
	}
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(selistener);
		
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
			mLocationClient = null;
		}
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}


	

	
	
	//exit Dialog
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                new AlertDialog.Builder(this)
                .setMessage("Want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
                	public void onClick(DialogInterface dialog, int which) {
                		finish();
                		System.exit(0);
                	}
                })//left
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// Nothing
					}
				})//right
               .show();
                return true;
        } 
        return super.onKeyDown(keyCode, event);
	}
    

}
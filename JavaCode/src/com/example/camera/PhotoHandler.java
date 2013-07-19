package com.example.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PhotoHandler implements PictureCallback {

	private static final String TAG = "CAMERA";
	private final Context mContext;
	private static String mPhotoPath = "";
	File mPictureFile;
	//private Camera camera;

	public PhotoHandler(Context context) {
		this.mContext = context;
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		
		mPhotoPath="mnt/sdcard/DCIM/Camera/"+getPhotoFileName();

		try {
			mPictureFile = new File(mPhotoPath);
		} catch (Exception e){
			if (mPictureFile == null){ 
	            Log.d(TAG, "Error creating media file, check storage permissions: " + 
	                e.getMessage()); 
	            return; 
	        } 
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(mPictureFile);
			fos.write(data);
			fos.close();
			Log.v(TAG, "SAVE");
			Toast.makeText(mContext, "New Image saved:" + mPhotoPath,
					Toast.LENGTH_LONG).show();
		} catch (Exception error) {
			
			Log.d(TAG, error.toString());
			Toast.makeText(mContext, "Image could not be saved.",
					Toast.LENGTH_LONG).show();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//camera.release();
		camera.startPreview();
	}
	
	public static String getFilePath(){
		return mPhotoPath;
	}
	
	private String getPhotoFileName() {  
        Date date = new Date(System.currentTimeMillis());  
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");  
        Log.v(TAG, dateFormat.format(date));
        return dateFormat.format(date) + ".jpg";  
    }  
}
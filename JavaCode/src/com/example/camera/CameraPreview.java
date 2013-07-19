package com.example.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback { 
    private static final String TAG = "CAMERA";
	private SurfaceHolder mHolder; 
    private Camera mCamera; 
 
    public CameraPreview(Context context, Camera camera) { 
        super(context); 
        mCamera = camera; 
 
        // 安装一个SurfaceHolder.Callback，
        // 这样创建和销毁底层surface时能够获得通知。
        mHolder = getHolder(); 
        mHolder.addCallback(this); 
        // 已过期的设置，但版本低于3.0的Android还需要
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
    } 
 
    public void surfaceCreated(SurfaceHolder holder) { 
    	
        //Parameters parameters = mCamera.getParameters();
        //parameters.setPreviewSize(320, 480);
        //parameters.setPictureSize(320, 480);
        //mCamera.setParameters(parameters);
    	// 更改时停止预览 
        try { 
            mCamera.stopPreview(); 
        } catch (Exception e){ 
          // 忽略：试图停止不存在的预览
        } 
        
        try { 
            mCamera.setPreviewDisplay(holder); 
            mCamera.startPreview(); 
        } catch (IOException e) { 
            Log.d(TAG, "Error setting camera preview: " + e.getMessage()); 
        } 
    } 
 
    public void surfaceDestroyed(SurfaceHolder holder) { 
        // 空代码。注意在activity中释放摄像头预览对象    
    	//mCamera.release();
    	} 
 
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { 
        // 如果预览无法更改或旋转，注意此处的事件
        // 确保在缩放或重排时停止预览
 
    	Camera.Parameters cameraParams = mCamera.getParameters();
        List<Camera.Size> sizes = cameraParams.getSupportedPictureSizes();
        //Camera.Size result = null;
        /*
        for (int i=0;i<sizes.size();i++){
            result = (Camera.Size) sizes.get(i);
            Log.i(TAG, "Supported Size.Width: " + result.width + "height: " +result.height);         
        }*/
        cameraParams.setPictureSize(sizes.get(3).width, sizes.get(3).height);
        mCamera.setParameters(cameraParams);
        
        if (mHolder.getSurface() == null){ 
          // 预览surface不存在
          return; 
        } 
 
        // 更改时停止预览 
        try { 
            mCamera.stopPreview(); 
        } catch (Exception e){ 
          // 忽略：试图停止不存在的预览
        } 
 
        // 在此进行缩放、旋转和重新组织格式
 
        // 以新的设置启动预览
        try { 
            mCamera.setPreviewDisplay(mHolder); 
            mCamera.startPreview(); 
 
        } catch (Exception e){ 
            Log.d(TAG, "Error starting camera preview: " + e.getMessage()); 
        } 
    } 
}

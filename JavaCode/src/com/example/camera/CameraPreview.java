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
 
        // ��װһ��SurfaceHolder.Callback��
        // �������������ٵײ�surfaceʱ�ܹ����֪ͨ��
        mHolder = getHolder(); 
        mHolder.addCallback(this); 
        // �ѹ��ڵ����ã����汾����3.0��Android����Ҫ
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
    } 
 
    public void surfaceCreated(SurfaceHolder holder) { 
    	
        //Parameters parameters = mCamera.getParameters();
        //parameters.setPreviewSize(320, 480);
        //parameters.setPictureSize(320, 480);
        //mCamera.setParameters(parameters);
    	// ����ʱֹͣԤ�� 
        try { 
            mCamera.stopPreview(); 
        } catch (Exception e){ 
          // ���ԣ���ͼֹͣ�����ڵ�Ԥ��
        } 
        
        try { 
            mCamera.setPreviewDisplay(holder); 
            mCamera.startPreview(); 
        } catch (IOException e) { 
            Log.d(TAG, "Error setting camera preview: " + e.getMessage()); 
        } 
    } 
 
    public void surfaceDestroyed(SurfaceHolder holder) { 
        // �մ��롣ע����activity���ͷ�����ͷԤ������    
    	//mCamera.release();
    	} 
 
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { 
        // ���Ԥ���޷����Ļ���ת��ע��˴����¼�
        // ȷ�������Ż�����ʱֹͣԤ��
 
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
          // Ԥ��surface������
          return; 
        } 
 
        // ����ʱֹͣԤ�� 
        try { 
            mCamera.stopPreview(); 
        } catch (Exception e){ 
          // ���ԣ���ͼֹͣ�����ڵ�Ԥ��
        } 
 
        // �ڴ˽������š���ת��������֯��ʽ
 
        // ���µ���������Ԥ��
        try { 
            mCamera.setPreviewDisplay(mHolder); 
            mCamera.startPreview(); 
 
        } catch (Exception e){ 
            Log.d(TAG, "Error starting camera preview: " + e.getMessage()); 
        } 
    } 
}

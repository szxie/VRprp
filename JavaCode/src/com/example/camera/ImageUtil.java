package com.example.camera;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageUtil {

	/**
	 * zoom in or out pictures
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return picture after zooming
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, float w, float h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = ((float) w / width) < ((float) h / height) ? ((float)w / width) : ((float) h / height);
		//float scale = ((float)(width / w)) > ((float)(height / h)) ? ((float)(width / w)) : ((float)(height / h));
		//float scaleWidht = ((float) w / width);
		//float scaleHeight = ((float) h / height);
		matrix.postScale(scale, scale);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}
	
	public static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(angle);
		Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbitmap;
	}

	/**
	 * change drawable into bitmap
	 * @param drawable
	 * @return picture in bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
	    int width = drawable.getIntrinsicWidth();
	    int height = drawable.getIntrinsicHeight();
	    Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
	    Canvas canvas = new Canvas(bitmap);
	    drawable.setBounds(0, 0, width, height);
	    drawable.draw(canvas);
	    return bitmap;

	}

	/**
	 * create reflection image with origin
	 * @param bitmap
	 * @return picture with reflection
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
	    final int reflectionGap = 4;
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	
	    Matrix matrix = new Matrix();
	    matrix.preScale(1, -1);
	
	    Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
	
	    Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
	
	    Canvas canvas = new Canvas(bitmapWithReflection);
	    canvas.drawBitmap(bitmap, 0, 0, null);
	    Paint deafalutPaint = new Paint();
	    canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
	
	    canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
	
	    Paint paint = new Paint();
	    LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
	    		bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
	    paint.setShader(shader);
	    // Set the Transfer mode to be porter duff and destination in
	    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	    // Draw a rectangle using the paint with our linear gradient
	    canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
	    return bitmapWithReflection;
	}
	
	private byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	private Bitmap Bytes2Bimap(byte[] b){
		if(b.length!=0){
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	* create the bitmap from a byte array
	* with watermark 
	* @param src the bitmap object you want proecss
	* @param watermark the water mark above the src
	* @return return a bitmap object ,if paramter's length is 0,return null
	*/
	public static Bitmap createBitmap( Bitmap src, Bitmap watermark ){
		String tag = "createBitmap";
		Log.d( tag, "create a new bitmap" );
		if( src == null ){
			return null;
		}
		 
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		//create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );//create a new bitmap
		Canvas cv = new Canvas( newb );
		//draw src into
		cv.drawBitmap( src, 0, 0, null );
		//draw watermark into
		cv.drawBitmap( watermark, w - ww + 5, h - wh + 5, null );
		//save all clip
		cv.save( Canvas.ALL_SAVE_FLAG );//
		//store
		cv.restore();//
		return newb;
	}

	/**
	 *  recode bitmap
	 * @param src source bitmap
	 * @param format jpg or png after coded
	 * @param quality after coded
	 * @return new bitmap after coded
	 */
	public static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
			int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);            

		byte[] array = os.toByteArray();
		return BitmapFactory.decodeByteArray(array, 0, array.length);
	}
	
	public static Bitmap rotatePhotos(Bitmap bitmap, int angle){
		  if(bitmap!=null){
		   Matrix m=new Matrix();
		   try{
		    m.setRotate(angle, bitmap.getWidth()/2, bitmap.getHeight()/2);
		    Bitmap bmp2=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
		    bitmap.recycle();
		    bitmap=bmp2;
		   }catch(Exception ex){
		    System.out.print("´´½¨Í¼Æ¬Ê§°Ü£¡"+ex);
		   }
		  }
		  return bitmap;
		 }


}
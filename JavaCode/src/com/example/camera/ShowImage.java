package com.example.camera;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.i.b;

import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;

public class ShowImage extends Activity {
	
	private static final String TAG = "IMAGE";
	//private GetTextImage mTextImage; 
	private double mLatitude;
	private double mLontitude;
	private double mAnglex;
	private double mAngley;
	private double mAnglez;
	private Boolean mMatch;
	
	private String mPoi;
	private String mImagePath;
	private String mAddress;
	
	private float mWindowWidth;
	private float mWindowHeight;
	private WindowManager mWindowManager;
	private Bitmap mOriginBitmap;
	
	private ImageView mPicImageView;
	private LinearLayout mDetailInfo;
	private TextView mDetailName;
	private TextView mDetailAddress;
	private TextView mDetaildistance;
	private TextView mDetailTel;
	private LinearLayout mDirection;
	private ImageView mHand;
	private TextView mDirectionName;
	private TextView mDirectionDis;
	//private SqlHttpURLConnection sqlConnection;
	//private ImageView popUp;
	public ArrayList<HashMap<String, String>> mInterests = 
    		new ArrayList<HashMap<String,String>>();
	
	private final double EARTH_RADIUS = 6378137.0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_showimage);
		mPicImageView = (ImageView) findViewById(R.id.picture );
		mDetailInfo = (LinearLayout) findViewById(R.id.detailinfo);
		mDetailName = (TextView) findViewById(R.id.detailname);
		mDetailAddress = (TextView) findViewById(R.id.detailaddress);
		mDetaildistance = (TextView) findViewById(R.id.detaildistance);
		mDetailTel = (TextView) findViewById(R.id.detailtel);
		mDirection = (LinearLayout) findViewById(R.id.direction);
		mHand = (ImageView)findViewById(R.id.hand);
		mDirectionName = (TextView) findViewById(R.id.directionname);
		mDirectionDis = (TextView) findViewById(R.id.directiondis);
		//get the data
		Bundle bundle = this.getIntent().getExtras(); 
		mImagePath = bundle.getString("ImagePath");
		mLatitude = bundle.getDouble("latitude");
		mLontitude = bundle.getDouble("lontitude");
		mPoi = bundle.getString("poi");
		mAnglex = bundle.getDouble("anglex");
		mAngley = bundle.getDouble("angley");
		mAnglez = bundle.getDouble("anglez");
		mAddress = bundle.getString("address");
		mMatch = bundle.getBoolean("match");
		Log.v(TAG, "PIC INFO:"+ mLatitude + ", " + mLontitude 
				+ " " + mAnglex
				+ " " + mAngley
				+ " " + mAnglez
				+ "\n" +mAddress 
				+ "\n" + mImagePath
				//+ "\n" + mPoi
				);
		//setContentView(mTextImage);
		
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
		mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
		Log.v(TAG, "window H & W:" + mWindowHeight + " ," + mWindowWidth );
		/*
		BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath, options);  
        
        float realWidth = options.outWidth;  
        float realHeight = options.outHeight;  
        Log.v(TAG, "真实图片高度：" + realHeight + "宽度:" + realWidth);  
		*/
		mOriginBitmap = BitmapFactory.decodeFile(mImagePath, null);//.copy(Bitmap.Config.ARGB_8888, true); 
        mPicImageView.setImageBitmap(ImageUtil.rotatePhotos(mOriginBitmap, 90));
        
        if (mPoi == null){
        	Toast.makeText(ShowImage.this, " No Poi Data. Please Try Again " , Toast.LENGTH_LONG)
			.show();
        }
        try {
			dealJson(mPoi);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}
        
	
	
        
        //getPoiInPic();
        
        
        //------------------------------------------------方法一
        /*
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        mPicImageView.measure(w, h);
        int height =mPicImageView.getMeasuredHeight();
        int width =mPicImageView.getMeasuredWidth();
        
        Log.v(TAG, "imageview :"+height+","+width);
        */
        
        
/*
        //-----------------------------------------------方法二
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int height = imageView.getMeasuredHeight();
                int width = imageView.getMeasuredWidth();
                textView.append("\n"+height+","+width);
                return true;
            }
        });
        //-----------------------------------------------方法三   
        ViewTreeObserver vto2 = imageView.getViewTreeObserver();  
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override  
            public void onGlobalLayout() {
            	imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);  
                textView.append("\n\n"+imageView.getHeight()+","+imageView.getWidth());
            }  
        });  
        */
	//direction mode
	private void drawDirection(int nearest) {
		double lat = Double.parseDouble(mInterests.get(nearest).get("y"));
		double lon = Double.parseDouble(mInterests.get(nearest).get("x"));
		double ag = myAngle(mLatitude, mLontitude, lat, lon); 
		float rotate = rotateAngle(mAnglex, ag);
		
		if (mDirection.getVisibility() == View.GONE){
			mDirection.setVisibility(View.VISIBLE);
		}
		
		Bitmap bit=BitmapFactory.decodeResource(this.getResources(), R.drawable.direction);
		mHand.setImageBitmap((ImageUtil.rotateBitmap(bit, rotate)));
		mDirectionName.setText(mInterests.get(nearest).get("name"));
		mDirectionDis.setText("前方" + mInterests.get(nearest).get("dis") + "米");
	}
	
	private void drawHighLight(int focus){
		LayoutParams highLightParams = new LayoutParams();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        highLightParams.type = LayoutParams.TYPE_PHONE; // 设置window type
        //wmParams.format = PixelFormat.RGBA_8888; // 
        
        //wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        highLightParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE ;
        	//	|  LayoutParams.FLAG_ALT_FOCUSABLE_IM | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        highLightParams.gravity = Gravity.LEFT| Gravity.TOP;
        highLightParams.x = 0;
        highLightParams.y = 0;
        highLightParams.width = WindowManager.LayoutParams.FILL_PARENT;
        highLightParams.height =WindowManager.LayoutParams.FILL_PARENT;
        highLightParams.format=PixelFormat.RGBA_8888;
        View view;
        view = inflater.inflate(R.layout.highlight, null);
        //ImageView leftTop = (ImageView)view.findViewById(R.id.lefttop);
        //ImageView rightTop = (ImageView)view.findViewById(R.id.righttop);
        //ImageView leftDown = (ImageView)view.findViewById(R.id.leftdown);
        //ImageView rightDown = (ImageView)view.findViewById(R.id.rightdown);
        TextView hightLightName = (TextView) view.findViewById(R.id.hightlight_name);
        TextView hightLightAddr = (TextView) view.findViewById(R.id.hightlight_address);
        TextView hightLightTel = (TextView) view.findViewById(R.id.hightlight_tel);
        TextView hightLightDis = (TextView) view.findViewById(R.id.hightlight_distance);
        hightLightName.setText(mInterests.get(focus).get("name"));
        hightLightAddr.setText("地址: " +mInterests.get(focus).get("addr"));
        String telephone = mInterests.get(focus).get("tel");
        Log.i(TAG, "tel length:" +telephone.length());
        if (telephone == null || telephone.length() == 0){
        	hightLightTel.setVisibility(view.GONE);
        } else {
        	hightLightTel.setText("电话: " + telephone);
        }
        
        hightLightDis.setText("距离: " +mInterests.get(focus).get("dis"));
        
        mWindowManager.addView(view, highLightParams);
	}
	
	
	private int  isInPic(double anglePhone, double anglePoi){
		double eye = anglePhone - anglePoi;
		if (eye < 60 && eye > 0){
			return 1;
		} else if ((eye + 360) < 60 && (eye + 360) > 0){
			return 2;
		} else if (eye > -60 && eye < 0){
			return 3;
		} else if ((eye - 360) > -60 && (eye - 360) < 0){
			return 4;
		} else return 0;
	}
	
	private float rotateAngle(double anglePhone, double anglePoi){
		float eye = (float) (anglePhone - anglePoi);
		if (eye <= 360 && eye >= 0){
			return eye;
		} else if (eye < 0){
			return eye + 360;
		} else {
			return eye - 360;
		}
	}
	
	private double gpsDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
	       double radLat1 = (lat_a * Math.PI / 180.0);
	       double radLat2 = (lat_b * Math.PI / 180.0);
	       double a = radLat1 - radLat2;
	       double b = (lng_a - lng_b) * Math.PI / 180.0;
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	              + Math.cos(radLat1) * Math.cos(radLat2)
	              * Math.pow(Math.sin(b / 2), 2)));
	       s = s * EARTH_RADIUS;
	       s = Math.round(s * 10000) / 10000;
	       return s;
	    }
	//get the angle of PAB
	private double gpsAngle(double lat_a, double lng_a, double lat_b, double lng_b) {
	       double d = 0;
	       lat_a=lat_a*Math.PI/180;
	       lng_a=lng_a*Math.PI/180;
	       lat_b=lat_b*Math.PI/180;
	       lng_b=lng_b*Math.PI/180;
	      
	        d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
	       d=Math.sqrt(1-d*d);
	       d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
	       d=Math.asin(d)*180/Math.PI;
	      
	       d = Math.round(d*10000)/10000;
	       return d;
	       
	    }
	//Math.atan好像有点问题，当时没有解决 不知道你们会不会遇到
	private double myAngle(double latOrg, double lngOrg, double latPoi, double lngPoi) {
	       double d = 0;
	       double disLat = latPoi - latOrg;
	       double disLon = lngPoi - lngOrg;
	       Log.i(TAG, "myAngle: " + disLat + " , " + disLon);
	       if (disLat == 0 && disLon > 0){
	    	   d = 90;
	       } else if (disLat == 0 && disLon < 0){
	    	   d = 270;
	       } else if (disLat > 0 && disLon == 0){
	    	   d = 0;
	       } else if (disLat < 0 && disLon == 0){
	    	   d = 180;
	       } else if ( disLat > 0 && disLon > 0){
	    	   d = 90 - Math.atan (disLat / disLon /Math.cos(latOrg) );
	       } else if ( disLat > 0 && disLon < 0){
	    	   d = 270 + Math.atan(disLat / -disLon / Math.cos(latOrg));
	       } else if ( disLat < 0 && disLon < 0){
	    	   d = 270 - Math.atan (disLat / disLon / Math.cos(latOrg) );
	       } else if ( disLat < 0 && disLon > 0){
	    	   d = 90 + Math.atan((-disLat) / disLon / Math.cos(latOrg) );
	       }
	       return d;
	       
	    }
	
	public void dealJson(String s) throws JSONException{
		JSONObject str = new JSONObject(s);
		String arr = str.getString("p");
		JSONArray arrjson = new JSONArray(arr);
		 
		for (int i = 0; i<arrjson.length(); i++){
			JSONObject jo = (JSONObject) arrjson.get(i);
			//Log.v(TAG,  "No.:" + i + " : ");
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
		Thread th = new Thread(new Runnable() {
			public void run() {
				String tmp = SqlHttpURLConnection.get();
				Log.i(TAG, "sql data :" + tmp);
				
				try {
					JSONArray tmparrjson = new JSONArray(tmp);
					 
					for (int i = 0; i<tmparrjson.length(); i++){
						JSONObject tmojo = (JSONObject) tmparrjson.get(i);
						HashMap<String, String> tmpMap = new HashMap<String, String>();
						tmpMap.put("name", tmojo.getString("name"));
						tmpMap.put("addr", tmojo.getString("address"));
						String lat = tmojo.getString("latitude");
						String lng = tmojo.getString("lontitude");
						tmpMap.put("x", lat);
						tmpMap.put("y", lng);
						double tmpdis = gpsDistance(mLatitude, mLontitude
								, Double.parseDouble(lat), Double.parseDouble(lng));
						Log.i(TAG, "distance between :" + tmpdis);
						tmpMap.put("dis", Double.toString(tmpdis));
						tmpMap.put("tel", tmojo.getString("phone"));
						mInterests.add(tmpMap);
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
	 });
	 th.start();
		
	}
    
    private int getNearestSeenPoi(int range){
    	double minDis = 1000;
		int index = -1;
		for (int i = 0; i < mInterests.size(); i ++){
			double dis = Double.parseDouble(mInterests.get(i).get("dis"));
			if (dis < range) {
				Log.v(TAG, mInterests.get(i).get("name"));
				double lat = Double.parseDouble(mInterests.get(i).get("y"));
				double lon = Double.parseDouble(mInterests.get(i).get("x"));
				double ag = myAngle(mLatitude, mLontitude, lat, lon);
				if (isInPic(mAnglex, ag) > 0) {
					if (dis < minDis) {
						minDis = dis;
						index = i;
					} 
				}
			}
		}
		return index;
	}
    
    private int getNearestPoi(){
    	double minDis = 1000;
		int index = -1;
		for (int i = 0; i < mInterests.size(); i ++){
			double dis = Double.parseDouble(mInterests.get(i).get("dis"));
			if (dis < minDis) {
				minDis = dis;
				index = i;
			}
		}
		return index;
	}
    
    
    //the biger the lontitue , the easter
    //这个方法绝对绝对要重写，我自己都看不下去了的
    private void getPoiInPic(){
    	for (int i = 0; i < mInterests.size(); i ++){
    		String tel = mInterests.get(i).get("tel");
    		String address = mInterests.get(i).get("addr");
    		String distance = mInterests.get(i).get("dis");
    		String lan = mInterests.get(i).get("y");
    		String lon = mInterests.get(i).get("x");
    		double dlan = Double.parseDouble(lan);
    		double dlon = Double.parseDouble(lon);
    		//double tmpangle = gpsAngle(mLatitude, mLontitude, dlan, dlon);
    		double tmpangle = myAngle(mLatitude, mLontitude, dlan, dlon);
    		//Log.v(TAG, mInterests.get(i).get("name") + " : " + tmpangle 
    		//		+ " (" + dlan + ", " + dlon + " ) , ("+ (mLontitude -dlon) + " ," + (mLatitude - dlan) + " )");
    		
    		double eyeAngle = mAnglex - tmpangle;
    		
    		if (isInPic(mAnglex, tmpangle) == 1){
    			int x = (int) ((1- eyeAngle / 60) * mWindowWidth /2 );
    			double dis = Double.parseDouble(distance);
    			int y = (int) ((1 - dis / 2000) * mWindowHeight /2);
    			String tmpname = mInterests.get(i).get("name");
    			Log.v(TAG, tmpname +  " : " + x + " , " + y);
    			drawPoi(tmpname, x, y, address, tel, distance);
    		} else if (isInPic(mAnglex, tmpangle) == 2){
    			eyeAngle = eyeAngle + 360;
    			int x = (int) ((1- eyeAngle / 60) * mWindowWidth /2 );
    			double dis = Double.parseDouble(distance);
    			int y = (int) ((1 - dis / 2000) * mWindowHeight /2);
    			String tmpname = mInterests.get(i).get("name");
    			Log.v(TAG, tmpname +  " : " + x + " , " + y);
    			drawPoi(tmpname, x, y, address, tel, distance);
    		} else if (isInPic(mAnglex, tmpangle) == 3){
    			eyeAngle = -eyeAngle;
    			int x = (int) ((1 + eyeAngle / 60) * mWindowWidth /2 );
    			double dis = Double.parseDouble(distance);
    			int y = (int) ((1 - dis / 2000) * mWindowHeight /2);
    			String tmpname = mInterests.get(i).get("name");
    			Log.v(TAG, tmpname +  " : " + x + " , " + y);
    			drawPoi(tmpname, x, y, address, tel, distance);
    		} else if (isInPic(mAnglex, tmpangle) == 4){
    			eyeAngle = 360 - eyeAngle;
    			int x = (int) ((1 + eyeAngle / 60) * mWindowWidth /2 );
    			double dis = Double.parseDouble(distance);
    			int y = (int) ((1 - dis / 2000) * mWindowHeight /2);
    			String tmpname = mInterests.get(i).get("name");
    			Log.v(TAG, tmpname +  " : " + x + " , " + y);
    			drawPoi(tmpname, x, y, address, tel, distance);
    		}
    		//double angle = 
    	}
    }
    
    private void drawPoi(final String name, int x, int y, 
    		final String address, final String telephone, final String distance){
    	LayoutParams wmParams = new LayoutParams();
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wmParams.type = LayoutParams.TYPE_PHONE; // 设置window type
        //wmParams.format = PixelFormat.RGBA_8888; // 
        
        //wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE ;
        	//	|  LayoutParams.FLAG_ALT_FOCUSABLE_IM | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        wmParams.gravity = Gravity.LEFT| Gravity.TOP;
        wmParams.x = x;
        wmParams.y = y;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height =WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format=PixelFormat.RGBA_8888;
        View view;
        view = li.inflate(R.layout.ballon, null);
        ImageView tmpiv = (ImageView)view.findViewById(R.id.balloon);
        //TextView tmptv = (TextView)view.findViewById(R.id.poiname);
        tmpiv.setBackgroundResource(R.drawable.balloncolor);
        //tmptv.setText(name);
        
        tmpiv.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v(TAG, "view onclick");
				
				if (mDetailInfo.getVisibility() == View.GONE){
					mDetailInfo.setVisibility(View.VISIBLE);
				}
				
				mDetailName.setText(name);
				mDetailAddress.setText("地址:" + address);
				mDetaildistance.setText("距离:" + distance);
				mDetailTel.setText("电话:" + telephone);
				/*
				Toast.makeText(ShowImage.this, 
						"Name: " + name + "\n"
						+"Address: " + address + "\n"
						+ "Tel: " + telephone + "\n"
						+ "Distance: " + distance, Toast.LENGTH_LONG)
				.show();
				
				Dialog dialog = new Dialog(ShowImage.this, R.style.MyDialog);
                dialog.setContentView(R.layout.dialog);
                //Dialog dialog = new Dialog(ShowImage.this) ;
                dialog.show();
                */
			}
		});
        
        mWindowManager.addView(view, wmParams);
    }
    

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		//捕获返回键
    	}
		return super.onKeyDown(keyCode, event);
	}

    //************add
    //menu button
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	//action
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.poimodel:
			
			if (mDirection.getVisibility() == View.VISIBLE){
				mDirection.setVisibility(View.GONE);
			}
	        int focus = getNearestSeenPoi(100);
	        Log.i(TAG, "Focus : " + focus);
	        //focus = -1;
	        if (focus == -1) {
	        	getPoiInPic();
	        } else {
	        	drawHighLight(focus);
	            }
	     	
			break;
		case R.id.directionmodel:
			int nearest = getNearestPoi();
			Log.i(TAG, " nearest : " + nearest);
			drawDirection(nearest);
			
			break;
		case R.id.addpoi:
			final String [] poiInfomation = new String[8];
			
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.layout_dialog, (ViewGroup) findViewById(R.id.poi_dialog));
			final EditText editName = (EditText)layout.findViewById(R.id.editname);
			final EditText editAddr = (EditText)layout.findViewById(R.id.editaddr);
			final EditText editTel = (EditText)layout.findViewById(R.id.editphone);
			new AlertDialog.Builder(this).setTitle("添加兴趣点").setView(layout)
				     .setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							poiInfomation[0] = Double.toString(mLatitude);
							poiInfomation[1] = Double.toString(mLontitude);
							poiInfomation[2] = Double.toString(mAnglex);
							poiInfomation[3] = Double.toString(mAngley);
							poiInfomation[4] = Double.toString(mAnglez);
							Log.i(TAG, "Edit text");
							poiInfomation[5] = editName.getText().toString().trim();
							poiInfomation[6] = editAddr.getText().toString().trim();
							poiInfomation[7] = editTel.getText().toString().trim();
							Log.i(TAG, "Edit text: " + poiInfomation[5] + " " + poiInfomation[6] + " " + poiInfomation[7]);
							
							Thread th1 = new Thread(new Runnable() {
								public void run() {
									String tmpcmd = formCommand(poiInfomation[0], poiInfomation[1], poiInfomation[2]
											, poiInfomation[3], poiInfomation[4], poiInfomation[5], poiInfomation[6], poiInfomation[7]);
									Log.i(TAG, tmpcmd);
									SqlHttpURLConnection.insert(tmpcmd);
								}
							});
							
							th1.start();
							
						}
					})
				     .setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					})
					.show();
			
			
			//TODO
			break;
		}
		return true;
	}

	private String formCommand(String lan, String lng, 
			String ax, String ay, String az, String n, String add, String tel){
		String str = new String ("insert into poiinfo values (\'"
				+ lan + "\',\'" + lng + "\',\'" + ax + "\',\'" + ay + "\',\'" + az + "\',\'"
				+ n + "\',\'" + add + "\',\'" + tel + "\')");
		
		return str;
	}



	
	
}
	
package com.example.camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class SqlHttpURLConnection {

	private static final String TAG = "SQLHTTP";

	public static String get(){
		StringBuffer sBuffer = new StringBuffer();
		String query = "select * from poiinfo";
		Log.v(TAG , query);
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL("http://59.78.28.97").openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Charset", "UTF-8");
			con.setDoOutput(true);
			con.getOutputStream().write("op=query&q=".getBytes());
			con.getOutputStream().write(query.getBytes());
			con.getOutputStream().flush();
			con.getOutputStream().close();
			int code = con.getResponseCode();
			//System.out.println("code   " + code);
			Log.v(TAG, "code   " + code );
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
			String out;
			
			while((out=in.readLine())!=null) {
				if (out.equals("0")) continue;
				sBuffer.append(out);
				Log.v(TAG, out);
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuffer.toString();
	}
	
	public static void insert(String command){
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL("http://59.78.28.97").openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Charset", "UTF-8");
			con.setDoOutput(true);
			con.getOutputStream().write("op=q&q=".getBytes());
			con.getOutputStream().write(command.getBytes());
			con.getOutputStream().flush();
			con.getOutputStream().close();
			int code = con.getResponseCode();
			//System.out.println("code   " + code);
			Log.v(TAG, "code   " + code );
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
			String out;
			
			while((out=in.readLine())!=null) {
				
				Log.v(TAG, out);
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

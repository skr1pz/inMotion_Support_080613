package com.inmotion.support;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.inmotion.support.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	
	WebView webview;
	
	private static String TAG = "FullscreenActivity";
	
	@Override
	public void onBackPressed()
	{
	    if(webview.canGoBack())
	        webview.goBack();
	    else
	        super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String root = getResources().getString(R.string.html_path);
		String index = getResources().getString(R.string.html_index);
		File f = new File(root + index);
		if(f.exists()){
			Log.i(TAG, "File exists." + f.canRead());
		} else {
			Log.i(TAG, "File DOES NOT exists. Path: " + f.getAbsolutePath());
			createDirectories(root);
			extractFiles(root);
		}
		
		Log.i(TAG ,"file://" + f.getAbsolutePath());
		setContentView(R.layout.webview);
		webview = (WebView) findViewById(R.id.webView1);
		Log.i(TAG ,"Opening webview LocalStorage");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.setVerticalScrollBarEnabled(false);
		webview.setHorizontalScrollBarEnabled(false);
		webview.getSettings().setDomStorageEnabled(true);
		//API 11 min - webview.getSettings().setDisplayZoomControls(false);
		webview.loadUrl("file://" + f.getAbsolutePath());
		
	}

	/**
	 * Creates directories to extract files to.
	 * 
	 * @param root
	 */
	private void createDirectories(String root){
		try  { 
				File rootDir = new File(root);
				if(!rootDir.exists()){
					rootDir.mkdirs();
				}
				InputStream raw = getResources().openRawResource(R.raw.web);
				ZipInputStream zin = new ZipInputStream(raw); 
	
				ZipEntry ze = null;
				//Create all the directories first.
				while ((ze = zin.getNextEntry()) != null) { 

					if(ze.isDirectory()) { 
						Log.v(TAG, "Creating Directory:  " + ze.getName()); 
						File dir = new File(root + ze.getName());
						if(!dir.exists()){
							dir.mkdirs();
						}
						
					}
					zin.closeEntry();
				}
		      zin.close(); 
		      
		    } catch(Exception e) { 
		      Log.e(TAG, "createDirectories", e); 
		    } 

	}
	
	/**
	 * Extracts files from zip file.
	 * 
	 * @param root
	 */
	private void extractFiles(String root){
		try{
			InputStream raw = getResources().openRawResource(R.raw.web);
			ZipInputStream zin = new ZipInputStream(raw); 

			ZipEntry ze = null;
			//Create all the directories first.
			while ((ze = zin.getNextEntry()) != null) { 
				int size;
				byte[] buffer = new byte[2048];
				if(!ze.isDirectory()) { 
					Log.v(TAG, "Unzipping file: " + ze.getName());
					FileOutputStream fout = new FileOutputStream(root + ze.getName());
					BufferedOutputStream bos = new BufferedOutputStream(fout, buffer.length);
					while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
					    bos.write(buffer, 0, size);
					  }
			          bos.close();
				}
				zin.closeEntry();

			}
	      zin.close(); 
	      
	    } catch(Exception e) { 
	      Log.e(TAG, "extractFiles", e); 
	    } 
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

}

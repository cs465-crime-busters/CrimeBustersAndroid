package com.illinoiscrimebusters.httpRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.SharedPreferences;
import android.os.AsyncTask;


import com.illinoiscrimebusters.crimebusters.UpdatedReportSingleton;

//AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue> .\\
/*
 * http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#concurrency_asynchtask
 * 
 * To use AsyncTask you must subclass it. AsyncTask uses generics and varargs. 
 * The parameters are the following AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue> .
 An AsyncTask is started via the execute() method.

 The execute() method calls the doInBackground() and the onPostExecute() method.

 TypeOfVarArgParams is passed into the doInBackground() method as input,
 ProgressValue is used for progress information and ResultValue must be returned from doInBackground() method and is passed to onPostExecute() as a parameter.

 The doInBackground() method contains the coding instruction which should be performed in a background thread.
 This method runs automatically in a separate Thread.

 The onPostExecute() method synchronizes itself again with the user interface thread and allows it to be updated.
 This method is called by the framework once the doInBackground() method finishes.
 */

public class HttpRequestHandler extends AsyncTask<String, Void, String> {

	private UpdatedReportSingleton reportSingleton = UpdatedReportSingleton.getInstance();


	/**
	 * Gathers data, submits report form, returns http response string
	 * 
	 * @return returnString , an http response string from the server
	 */
	private String submitReport(String pushId) {
		// Get Data
		HashMap<String, String> report = reportSingleton.copyReport();
		report.put("pushId", pushId);

		String url = reportSingleton.getUrl();

		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
				.create();
		multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		if (reportSingleton.isIncludeImage() &&  reportSingleton.getImageLocation() != null) {
			multipartEntity.addPart("photo", new FileBody(new File(reportSingleton.getImageLocation())));
		}


		if (reportSingleton.isIncludeAudio() && reportSingleton.getAudioPath() != null) {
			multipartEntity.addPart("audio", new FileBody(new File(reportSingleton.getAudioPath())));
		}
		
		if (reportSingleton.isIncludeVideo() && reportSingleton.getVideoPath() != null) {
			multipartEntity.addPart("video", new FileBody(new File(reportSingleton.getVideoPath())));
		}
		
		for (String name : report.keySet()) {
			String value = (null != report.get(name)) ? report.get(name) : "";
			multipartEntity.addTextBody(name, value);
		}
		

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setEntity(multipartEntity.build());
		HttpEntity entity = null;
		HttpResponse response = null;
		try {
			response = httpClient.execute(post);
			entity = response.getEntity();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		String returnString = "";
		try {
			returnString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reportSingleton.clearImages();
		reportSingleton.clearAudioVideoPaths();
		return returnString;

	}

	/**
	 * Gathers data, submits report form, returns http response string
	 * 
	 * @return returnString , an http response string from the server This
	 *         method runs in the background
	 */

	@Override
	protected String doInBackground(String... args) {
		return submitReport(args[0]);

	}
}

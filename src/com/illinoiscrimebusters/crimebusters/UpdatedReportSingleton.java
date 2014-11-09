package com.illinoiscrimebusters.crimebusters;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.crime.crimebusters.R;

import android.widget.ImageView;

/**
 * @author Ahmed Farouk Soliman
 * A class to store most temp data and to perform a few minor functions.
 * It is updated to have only one image, one video and one audio functions.
 */
public class UpdatedReportSingleton {
	private String url = "http://illinoiscrimebusters.com/services/PostReport.ashx";

	// private String url ="http://sadkhin.net/crimebusters/test_post.php";
	private String username = "test";
	private int reportType = 0;
	private String imageLocation;
	private String image;
	private String audioPath;
	private String videoPath;

	private String audioPathDisplay;
	private String videoPathDisplay;
	
	private HashMap<String, String> report = new HashMap<String, String>();
	
	private String pushId;

	/**
	 * @return audio path string
	 */
	public String getAudioPathDisplay() {
		return audioPathDisplay;
	}

	/**
	 * Make audio path string display on UI
	 * 
	 * @param audioPathDisplay
	 */
	public void setAudioPathDisplay(String audioPathDisplay) {
		this.audioPathDisplay = audioPathDisplay;
	}

	/**
	 * @return video path string
	 */
	public String getVideoPathDisplay() {
		return videoPathDisplay;
	}

	/**
	 * Set video path string on UI
	 * 
	 * @param videoPathDisplay
	 */
	public void setVideoPathDisplay(String videoPathDisplay) {
		this.videoPathDisplay = videoPathDisplay;
	}

	private String temp_desc;
	private String temp_location;

	private String whichButton;

	/**
	 * Clear images on submit
	 * 
	 */
	public void clearImages() {
		image = null;
	}

	/**
	 * Clear audio and video paths from UI
	 * 
	 */
	public void clearAudioVideoPaths() {

		audioPath = null;
		videoPath = null;
		audioPathDisplay = null;
		videoPathDisplay = null;
	}

	private BufferedOutputStream bos;
	private BufferedOutputStream audBos;

	/**
	 * @return buffered output for media video
	 */
	public BufferedOutputStream getBos() {
		return bos;
	}

	/**
	 * @param set
	 *            BufferedOutputStream for video
	 */
	public void setBos(BufferedOutputStream bos) {
		this.bos = bos;
	}

	/**
	 * @return buffered output for media audio
	 */
	public BufferedOutputStream getAudBos() {
		return audBos;
	}

	/**
	 * @param audBos
	 *            BufferedOutputStream for audio
	 */
	public void setAudBos(BufferedOutputStream audBos) {
		this.audBos = audBos;
	}

	private boolean imageValue = false;


	/**
	 * @return if image1set
	 */
	public boolean isImageDone() {
		return imageValue;
	}

	/**
	 * @param iv1Done
	 *            whether or not 1v1 is set
	 */
	public void setImageValueDone(boolean iv1Done) {
		this.imageValue = iv1Done;
	}



	private String language;

	/**
	 * @return language name
	 */
	public String getLanguage() {
		if (language == null) {
			language = "English";
		}
		return language;
	}

	/**
	 * @param language
	 *            to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	public int position;

	/**
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	

	private int themeNumber;

	/**
	 * @return which theme
	 */
	public int getThemeNumber() {
		return themeNumber;
	}

	/**
	 * @param themeNumber
	 *            set the theme
	 */
	public void setThemeNumber(int themeNumber) {
		this.themeNumber = themeNumber;
	}

	/**
	 * @return theme theme after its set
	 */
	public int setTheme() {

		int th = 0;
		if (themeNumber == 0) {
			themeNumber = R.style.MyTheme;
		}

		if (themeNumber == R.style.MyTheme) {
			th = R.drawable.c8;

		}

		else if (themeNumber == R.style.MyTheme2) {
			th = R.drawable.b6;

		}

		else if (themeNumber == R.style.MyTheme3) {
			th = R.drawable.orange;
		}

		return th;
	}

	/**
	 * @param key
	 * @return value in hash
	 */
	public String returnKey(String key) {
		return report.get(key);

	}

	/**
	 * Set key value in has without direct access
	 * 
	 * @param key
	 * @param value
	 */
	public void setKey(String key, String value) {
		report.put(key, value);
	}

	/**
	 * @return username
	 */
	public String getName() {
		return username;

	}

	/**
	 * @return a copy of the hash
	 */
	public HashMap<String, String> copyReport() {
		return this.report;
	}

	// http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents//
	// keyword horse//

	private static UpdatedReportSingleton instance = null;

	/**
	 * // Exists only to defeat instantiation.
	 */
	protected UpdatedReportSingleton() {
		// Exists only to defeat instantiation.
	}

	/**
	 * @return an instance of the singleton, the only way to run it
	 */
	public static UpdatedReportSingleton getInstance() {
		if (instance == null) {
			instance = new UpdatedReportSingleton();
		}
		return instance;
	}

	/**
	 * @return the url set for the http request for form submission
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the reportType ID
	 */
	public int getReportType() {
		return reportType;
	}

	/**
	 * @param reportType
	 *            the reportType to set
	 */
	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	/**
	 * Set the username in memory
	 * 
	 * @param username
	 */
	public void setName(String username2) {
		username = username2;

	}

	/**
	 * @return audio path from memory
	 */
	public String getAudioPath() {
		return audioPath;
	}

	/**
	 * set audio path in memory
	 * 
	 * @param audioPath
	 */
	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	/**
	 * @return video path from memory
	 */
	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * set video path in memory
	 * 
	 * @param videoPath
	 */
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	/**
	 * @return image1 location from memory
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image1
	 */
	public void setImage(String image1) {
		this.image = image1;
	}

	
	/**
	 * @return temp description from memory
	 */
	public String getTemp_desc() {
		return temp_desc;
	}

	/**
	 * Set temp location in mem
	 * 
	 * @param temp_desc
	 */
	public void setTemp_desc(String temp_desc) {
		this.temp_desc = temp_desc;
	}

	/**
	 * @return temp location from mem
	 */
	public String getTemp_location() {
		return temp_location;
	}

	/**
	 * Set temp location in mem
	 * 
	 * @param temp_location
	 */
	public void setTemp_location(String temp_location) {
		this.temp_location = temp_location;
	}

	public String getPushId() {
		return pushId;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

}

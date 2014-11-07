package com.illinoiscrimebusters.crimebusters;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crime.crimebusters.R;

/**
 * 
 * @author Ahmed Farouk Soliman
 *
 */

public class UpdatedReportIncidentActivity extends Activity implements
LocationListener {

	private UpdatedReportSingleton reportSingleton = UpdatedReportSingleton.getInstance();

	// for picture capture
	//private ImageView imageView;
	// Audio
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private Button audioRecordButton, stop, audioPlayButton;

	// Video
	private static final int VIDEO_CAPTURE = 101;

	// GPS
	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;
	//private ImageView iv;
	//private String ivString;
	private Uri fileUri; 
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200; 

	// drawable backgrounds for buttons
	//private Drawable cameraButtonEnabled ;
	private Drawable cameraButtonDisabled ;

	private Drawable playButtonEnabled ;
	private Drawable playButtonDisabled;

	private Drawable stopButtonEnabled;
	private Drawable stopButtonDisabled ;

	private Drawable recordButtonEnabled;
	private Drawable recordButtonDisabled ;

	private Drawable videoButtonEnabled ;
	private Drawable videoButtonDisabled ;




	/** start add media activity and save already filled strings
	 *
	 */
	public void addMedia(View v) {
		Intent intent = new Intent(this, MediaActivity.class);
		startActivity(intent);

		String location = 	 ((EditText) findViewById(R.id.location)).getText().toString();

		String message =  ((EditText) findViewById(R.id.message)).getText().toString();
		reportSingleton.setTemp_desc(message);
		reportSingleton.setTemp_location(location);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUserPreferences();

		Log.v(ACTIVITY_SERVICE, "Report Singleton "+reportSingleton.getReportType());
		if (reportSingleton.getReportType() == 1) {
			getActionBar().setTitle("Report Emergency");
			setContentView(R.layout.activity_high_priority_incident);
			Button picBtn = (Button) findViewById(R.id.pictureCaptureButton);
			
			if (picBtn != null) picBtn.setVisibility(View.INVISIBLE);
			
		} else { // Report Singleton type is 2
			getActionBar().setTitle("Report Non-Emergency");
			setContentView(R.layout.activity_updated_report_incident);
			
			initializeButtonsStates();
			initializeButtons();
			initializeSpinner();
		}

		initializeGPS();
		initializeTime();


		//Log.v(ACTIVITY_SERVICE, "Time as on form "+((EditText) findViewById(R.id.editText_currentTime_RO)).getText().toString());
	}
	
	private void initializeSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.crimteTypespinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.crimetypes_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	private void initializeButtons() {
		// >>> Addition for Media
		// Audio
		audioRecordButton = (Button) findViewById(R.id.audioRecordButton);
		stop = (Button) findViewById(R.id.button2);
		audioPlayButton = (Button) findViewById(R.id.audioPlayButton);

		// disable the stop and play Button initially
		stop.setEnabled(false);
		stop.setBackgroundDrawable(stopButtonDisabled);
		audioPlayButton.setEnabled(false);
		audioPlayButton.setBackgroundDrawable(playButtonDisabled);


		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording.3gp";
		reportSingleton.setAudioPath(outputFile);

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);

		// Video
		Button recBtn = (Button) findViewById(R.id.recordVideoButton);

		Button pic1Btn = (Button) findViewById(R.id.pictureCaptureButton);


		if (!hasCamera()) {
			recBtn.setEnabled(false);
			recBtn.setBackgroundDrawable(videoButtonDisabled);
			pic1Btn.setEnabled(false);
			pic1Btn.setBackgroundDrawable(cameraButtonDisabled);
		}
		// <<< Addition for Media
	}
	

	private void initializeButtonsStates() {
		//cameraButtonEnabled = getResources().getDrawable(R.drawable.ic_action_camera);
		cameraButtonDisabled = getResources().getDrawable(R.drawable.ic_action_camera_dark);

		playButtonEnabled = getResources().getDrawable(R.drawable.ic_action_play);
		playButtonDisabled = getResources().getDrawable(R.drawable.ic_action_play_dark);

		stopButtonEnabled = getResources().getDrawable(R.drawable.ic_action_stop);
		stopButtonDisabled = getResources().getDrawable(R.drawable.ic_action_stop_dark);

		recordButtonEnabled = getResources().getDrawable(R.drawable.ic_action_microphone);
		recordButtonDisabled = getResources().getDrawable(R.drawable.ic_action_mic_dark);

		videoButtonEnabled = getResources().getDrawable(R.drawable.ic_action_video);
		videoButtonDisabled = getResources().getDrawable(R.drawable.ic_action_video_dark);
	}



	/**
	 * This method sets the theme and language as per user preference
	 */
	private void setUserPreferences() {
		//int theme = reportSingleton.setTheme();
		//getWindow().setBackgroundDrawableResource(theme);

		//setContentView(R.layout.activity_updated_report_incident);

		String lang = reportSingleton.getLanguage();
		if (lang != null) {
			if (lang.equalsIgnoreCase("English"))
				changeLocale("en");

			if (lang.equalsIgnoreCase("French"))
				changeLocale("fr");

			if (lang.equalsIgnoreCase("Spanish"))
				changeLocale("es");

		}
	}

	/**
	 * Event handler for the change language Button
	 * 
	 * @param language
	 */
	private void changeLocale(String language) {

		Configuration config = getResources().getConfiguration();

		// Creating an instance of Locale for French language
		Locale locale = new Locale(language);

		// Setting locale of the configuration to French language
		config.locale = locale;

		// Updating the application configuration
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		// Setting the title for the activity, after configuration change
		setTitle(R.string.app_name);

	}

	/**
	 * Set time
	 */
	private void initializeTime() {
		Time now = new Time();
		now.setToNow();
		String format = "%m/%d/%y %H:%M:%S";
		String time = now.format(format);

		((EditText) findViewById(R.id.editText_currentTime_RO)).setText(time);
		Log.v(ACTIVITY_SERVICE, "Setting time "+time);
	}

	/** GPS
	 *  Get the location manager
	 *  Define the criteria how to select the location provider -> use default
	 *  Initialize the location fields
	 */
	public void initializeGPS() {
		latituteField = (TextView) findViewById(R.id.locationGPS_lat);
		longitudeField = (TextView) findViewById(R.id.locationGPS_long);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		Log.v(ACTIVITY_SERVICE, "Location is "+location);
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			latituteField.setText("LAT: N/A");
			longitudeField.setText("LONG: N/A");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_incident, menu);
		return true;
	}

	/** Submit report and clear out media
	 * 
	 */
	public void submitReport(View view) {
		populateReport();
		reportSingleton.setIv1Done(false);
		reportSingleton.setIv2Done(false);
		reportSingleton.setIv3Done(false);

		Intent intent = new Intent(this, HTTPSubmitReportActivity.class);
		startActivity(intent);
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		setUserPreferences();

		locationManager.requestLocationUpdates(provider, 400, 1, this);
		String tempDesc =  reportSingleton.getTemp_desc();
		String tempLocation = reportSingleton.getTemp_desc();

		EditText messageEditText = ((EditText) findViewById(R.id.message));
		EditText locationEditText = ((EditText) findViewById(R.id.location));

		if(tempDesc != null){
			messageEditText.setText(tempDesc);
		}
		if(tempLocation != null){
			locationEditText.setText(tempLocation);
		}
		
		initializeGPS();
		initializeTime();


	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Grab Latitude and Longitude as float and set them as string
	 */
	@Override
	public void onLocationChanged(Location location) {
		float lat = (float) (location.getLatitude());
		float lng = (float) (location.getLongitude());
		latituteField.setText(String.valueOf(lat));
		longitudeField.setText(String.valueOf(lng));

		Log.v(ACTIVITY_SERVICE, "Setting latitude "+String.valueOf(lat));
		Log.v(ACTIVITY_SERVICE, "Setting longitude "+String.valueOf(lng));

	}

	/**Method to populate the data singleton with user inputted data
	 * 
	 */
	public void populateReport() {

		reportSingleton.setKey("message",
				((EditText) findViewById(R.id.message)).getText().toString());

		String location = "";
		String gps = "";
		String latitude = "";
		String longitude = "";
		String message = "";
		String timestamp = "";

		// GET

		location = ((EditText) findViewById(R.id.location)).getText()
				.toString();
		timestamp = ((EditText) findViewById(R.id.editText_currentTime_RO))
				.getText().toString();
		latitude = ((EditText) findViewById(R.id.locationGPS_lat)).getText()
				.toString();
		longitude = ((EditText) findViewById(R.id.locationGPS_long)).getText()
				.toString();
		message = ((EditText) findViewById(R.id.message)).getText().toString();

		reportSingleton.setKey("location", location);
		reportSingleton.setKey("desc", message);
		reportSingleton.setKey("lat", latitude);
		reportSingleton.setKey("lng", longitude);
		reportSingleton.setKey("timeStamp", timestamp);

	}


	// >>> updated for media controls

	/**
	 * This method checks if the mobile has a camera feature or not
	 * @return
	 */
	private boolean hasCamera() {
		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method is called when the video recording starts
	 * 
	 * @param view
	 */
	public void startRecording(View view) {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
		startActivityForResult(intent, VIDEO_CAPTURE);
	}

	/**
	 * This method is called when the audio recording starts
	 * 
	 * @param view
	 */
	public void startAudioRecording(View view) {
		try {
			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		audioRecordButton.setEnabled(false);
		audioRecordButton.setBackgroundDrawable(recordButtonDisabled);

		stop.setEnabled(true);
		stop.setBackgroundDrawable(stopButtonEnabled);

		Toast.makeText(getApplicationContext(), "Recording started",
				Toast.LENGTH_LONG).show();

	}


	/**
	 * This method is called when the audio recording stops
	 * 
	 * @param view
	 */
	public void stop(View view) {
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
		stop.setEnabled(false);
		stop.setBackgroundDrawable(stopButtonDisabled);
		audioPlayButton.setEnabled(true);
		stop.setBackgroundDrawable(playButtonEnabled);
		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				Toast.LENGTH_LONG).show();
	}

	/**
	 * This method is called when the audio recording is played
	 * 
	 * @param view
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void play(View view) throws IllegalArgumentException,
	SecurityException, IllegalStateException, IOException {

		MediaPlayer m = new MediaPlayer();
		m.setDataSource(outputFile);
		m.prepare();
		m.start();
		Toast.makeText(getApplicationContext(), "Playing audio",
				Toast.LENGTH_LONG).show();
		File audFile = new File(outputFile);
		saveAudio(audFile);

		reportSingleton.setAudioPathDisplay(outputFile);

		audioRecordButton.setEnabled(true);
		audioRecordButton.setBackgroundDrawable(recordButtonEnabled);

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	/**
	 * This method is called when an audio file is created by the user and is to be saved
	 *  
	 * @param outputFile
	 * @return
	 */
	private String saveAudio(File outputFile) {
		String sourceFilename = outputFile.getPath();
		String destinationFilename = android.os.Environment
				.getExternalStorageDirectory().getPath()
				+ File.separatorChar
				+ "cb_audio.3gp";

		BufferedInputStream bis = null;
		BufferedOutputStream audBos = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(sourceFilename));
			audBos = new BufferedOutputStream(new FileOutputStream(
					destinationFilename, false));
			byte[] buf = new byte[1024];
			bis.read(buf);
			do {
				audBos.write(buf);
			} while (bis.read(buf) != -1);
			reportSingleton.setAudBos(audBos);
		} catch (IOException e) {

		} finally {
			try {
				if (bis != null)
					bis.close();
				if (audBos != null)
					audBos.close();
			} catch (IOException e) {

			}
		}

		reportSingleton.setAudioPath(destinationFilename);
		return destinationFilename;
	}


	/**
	 * This method is taken when the user clicks the Button to take the first picture
	 * 
	 * @param v
	 */
	public void takePicture(View v) {
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
		reportSingleton.setWhichButton("1");
	}

	@Override
	/**
	 * 
	 * This method is called after the camera captures a picture or records a video
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Video
		if (requestCode == VIDEO_CAPTURE) {
			if (resultCode == RESULT_OK) {
				Uri videoUri = data.getData();
				String path = saveVideo(videoUri);

				Toast.makeText(this, "Video has been saved to:\n" + path,
						Toast.LENGTH_LONG).show();

				reportSingleton.setVideoPath(getPath(videoUri));
				reportSingleton.setVideoPathDisplay(path);

				//textView3 = (TextView) findViewById(R.id.textView3);
				//textView3.setText(path);

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Video recording cancelled.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Failed to record video",
						Toast.LENGTH_LONG).show();
			}
		}

		else if (data != null) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");

			if (reportSingleton.getWhichButton().equals("1")) {
				//imageView.setImageBitmap(photo);
				SaveImage savefile = new SaveImage();
				savefile.SavePic(this, photo);
				reportSingleton.setIv1Done(true);
			} 
		}

	}




	/**
	 * This method is called when the user creates a video recording and it needs to be saved
	 * 
	 * 
	 * @param uri
	 * @return
	 */
	private String saveVideo(Uri uri) {
		String sourceFilename = uri.getPath();
		String destinationFilename = android.os.Environment
				.getExternalStorageDirectory().getPath()
				+ File.separatorChar
				+ "cb_vid.mp4";

		String NameOfFolder = "/CB_Folder/";
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+NameOfFolder + "cb_vid.mp4";

		destinationFilename = file_path;

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(sourceFilename));
			bos = new BufferedOutputStream(new FileOutputStream(
					destinationFilename, false));
			byte[] buf = new byte[1024];
			bis.read(buf);
			do {
				bos.write(buf);
			} while (bis.read(buf) != -1);
			reportSingleton.setBos(bos);
		} catch (IOException e) {

		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {

			}
		}

		File from = new File(uri.toString());
		File to = new File(destinationFilename);
		from.renameTo(to);

		reportSingleton.setVideoPath(destinationFilename);
		return destinationFilename;
	}

	/**
	 * This method is called when the path for video is retrieved from the URI
	 * 
	 * @param contentUri
	 * @return
	 */
	public String getPath(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		if(cursor.moveToFirst()){;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	// <<< updated for media controls

}

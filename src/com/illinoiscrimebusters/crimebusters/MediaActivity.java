package com.illinoiscrimebusters.crimebusters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crime.crimebusters.R;

/**
 * Activity for the Media page.
 * 
 * @author Khushbu
 * 
 */
public class MediaActivity extends Activity {
	private ReportSingleton _reportSingleton = ReportSingleton.getInstance();
	private ImageView iv1;
	private ImageView iv2;
	private ImageView iv3;

	// Audio
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private Button start, stop, play;

	// Video
	private static final int VIDEO_CAPTURE = 101;
	
	private static TextView textView2;
	private static TextView textView3;


	@Override
	/**
	 * This is the method that is called when an intent is initialized for the first time
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUserPreferences();

		if (_reportSingleton.getReportType() == 1) {
			setContentView(R.layout.activity_high_priority_incident);

		} else {
			setContentView(R.layout.activity_media);
			iv1 = (ImageView) findViewById(R.id.imageView1);
			iv2 = (ImageView) findViewById(R.id.imageView2);
			iv3 = (ImageView) findViewById(R.id.imageView3);
		}

		// Audio
		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		play = (Button) findViewById(R.id.button3);

		stop.setEnabled(false);
		play.setEnabled(false);
		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/myrecording.3gp";
		;

		_reportSingleton.setAudioPath(outputFile);

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);

		// Video
		Button recBtn = (Button) findViewById(R.id.recordButton);

		Button pic1Btn = (Button) findViewById(R.id.Picture1);
		Button pic2Btn = (Button) findViewById(R.id.Picture2);
		Button pic3Btn = (Button) findViewById(R.id.Picture3);

		if (!hasCamera()) {
			recBtn.setEnabled(false);
			pic1Btn.setEnabled(false);
			pic2Btn.setEnabled(false);
			pic3Btn.setEnabled(false);
		}
	}

	/**
	 * This method sets the theme and language as per user preference
	 */
	private void setUserPreferences() {
		int theme = _reportSingleton.setTheme();
		getWindow().setBackgroundDrawableResource(theme);

		setContentView(R.layout.activity_media);

		String lang = _reportSingleton.getLanguage();
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
	public void start(View view) {
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
		start.setEnabled(false);
		stop.setEnabled(true);
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
		play.setEnabled(true);
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

		textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setText(outputFile);
		_reportSingleton.setAudioPathDisplay(outputFile);

		start.setEnabled(true);

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	/**
	 * Method to be called while setting the language for the UI
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
	 * This method is called when the "Return to Submission" button is clicked
	 * @param v
	 */
	public void returnToSubmit(View v) {
		Intent intent = new Intent(this, ReportIncidentActivity.class);
		startActivity(intent);
	}

	/**
	 * This method is taken when the user clicks the button to take the first picture
	 * 
	 * @param v
	 */
	public void takePicture1(View v) {
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
		_reportSingleton.setWhichButton("1");
	}
	
	
	/**
	 * This method is taken when the user clicks the button to take the second picture
	 * 
	 * @param v
	 */
	public void takePicture2(View v) {
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
		_reportSingleton.setWhichButton("2");
	}
	
	
	/**
	 * This method is taken when the user clicks the button to take the third picture
	 * 
	 * @param v
	 */
	public void takePicture3(View v) {
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
		_reportSingleton.setWhichButton("3");
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
			
				_reportSingleton.setVideoPath(getPath(videoUri));
				_reportSingleton.setVideoPathDisplay(path);

				textView3 = (TextView) findViewById(R.id.textView3);
				textView3.setText(path);

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

			if (_reportSingleton.getWhichButton().equals("1")) {
				iv1.setImageBitmap(photo);
				SaveImage savefile = new SaveImage();
				savefile.SavePic(this, photo);
				_reportSingleton.setIv1Done(true);
			} else if (_reportSingleton.getWhichButton().equals("2")) {
				iv2.setImageBitmap(photo);
				SaveImage savefile = new SaveImage();
				savefile.SavePic(this, photo);
				_reportSingleton.setIv2Done(true);

			} else if (_reportSingleton.getWhichButton().equals("3")) {
				iv3.setImageBitmap(photo);
				SaveImage savefile = new SaveImage();
				savefile.SavePic(this, photo);
				_reportSingleton.setIv3Done(true);

			}
		}

	}
	
	/**
	 * This method is called when the path for video is retrieved from the Uri
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
	
	/**
	 * This method is called when the user creates a video reocrding and it needs to be saved
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
			_reportSingleton.setBos(bos);
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
				
	    _reportSingleton.setVideoPath(destinationFilename);
	    return destinationFilename;
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
			_reportSingleton.setAudBos(audBos);
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
		
		_reportSingleton.setAudioPath(destinationFilename);
		return destinationFilename;
	}

	/**
	 * This method is called when the user returns to an intent from a paused state
	 * If the user has been authenticated before, it redirects the user to the Main Form
	 * 
	 */
	protected void onResume() {
		super.onResume();
		
		setUserPreferences();
		
		iv1 = (ImageView) findViewById(R.id.imageView1);
		iv2 = (ImageView) findViewById(R.id.imageView2);
		iv3 = (ImageView) findViewById(R.id.imageView3);
		
		
		String image1 = _reportSingleton.getImage1();
		String image2 = _reportSingleton.getImage2();
		String image3 = _reportSingleton.getImage3();
		if(image1 != null){
			iv1.setImageURI(Uri.fromFile(new File(image1)));
		}
		if(image2 != null){
			iv2.setImageURI(Uri.fromFile(new File(image2)));
		}
		if(image3 != null){
			iv3.setImageURI(Uri.fromFile(new File(image3)));
		}
		
		
		textView3 = (TextView) findViewById(R.id.textView3);
		textView3.setText(_reportSingleton.getVideoPathDisplay());
		
		textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setText(_reportSingleton.getAudioPathDisplay());
	}

	@Override
	/**
	 * It is triggered to specify the options menu for an activity
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.media, menu);
		return true;
	}

}

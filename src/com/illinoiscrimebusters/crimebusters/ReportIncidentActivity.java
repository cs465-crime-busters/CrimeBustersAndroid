package com.illinoiscrimebusters.crimebusters;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import com.crime.crimebusters.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReportIncidentActivity extends Activity implements
		LocationListener {

	private ReportSingleton reportSingleton = ReportSingleton.getInstance();

	// GPS
	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;
	private ImageView iv;
	//private String ivString;


	/** start add media activity and save already filled strings
	 *
	 */
	public void addMedia(View v) {
		Intent intent = new Intent(this, MediaActivity.class);
		startActivity(intent);
		
		String location = 	location = ((EditText) findViewById(R.id.location)).getText().toString();
		
		String message = message = ((EditText) findViewById(R.id.message)).getText().toString();
		reportSingleton.setTemp_desc(message);
		reportSingleton.setTemp_location(location);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUserPreferences();

		if (reportSingleton.getReportType() == 1) {
			setContentView(R.layout.activity_high_priority_incident);
			Intent intent = getIntent();

		} else {
			setContentView(R.layout.activity_report_incident);
			Intent intent = getIntent();
			iv = (ImageView) findViewById(R.id.imageView1);

		}

		GPS();
		time();

	}

	/**
	 * This method sets the theme and language as per user preference
	 */
	private void setUserPreferences() {
		int theme = reportSingleton.setTheme();
		getWindow().setBackgroundDrawableResource(theme);

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
	 * Event handler for the change language button
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
	private void time() {
		Time now = new Time();
		now.setToNow();
		String format = "%m/%d/%y %H:%M:%S";
		String time = now.format(format);

		((EditText) findViewById(R.id.editText_currentTime)).setText(time);

	}

	/** GPS
	 *  Get the location manager
	 *  Define the criteria how to select the location provider -> use default
	 *  Initialize the location fields
	 */
	public void GPS() {
		latituteField = (TextView) findViewById(R.id.locationGPS_lat);
		longitudeField = (TextView) findViewById(R.id.locationGPS_long);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
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
		timestamp = ((EditText) findViewById(R.id.editText_currentTime))
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
}

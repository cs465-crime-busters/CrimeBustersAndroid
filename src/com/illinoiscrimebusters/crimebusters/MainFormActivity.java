package com.illinoiscrimebusters.crimebusters;

import java.lang.reflect.Field;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.crime.crimebusters.R;
import com.illinoiscrimebusters.login.Login;
import com.illinoiscrimebusters.user.User;


//Main page. 
public class MainFormActivity extends Activity {

	public static final String PANIC_MESSAGE = "Panic Message";
	public static final String REPORT_MESSAGE = "Report Message";
	ReportSingleton _reportSingleton = ReportSingleton.getInstance();
	private String _userName;

	@SuppressWarnings("null")
	@Override
	/**
	 * This is the method that is called when an intent is initialized for the first time
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_form);
		
		setUserPreferences();
		
		TextView textView = (TextView) findViewById(R.id.main_activity_header);
		textView.setTextSize(24);
		
		
		if((getIntent().getStringExtra("userName")!= null))
		{
			_userName = getIntent().getStringExtra("userName");
		}
		else
		{
			_userName = getSharedPreferences("cbPreference",MODE_PRIVATE).getString("userName", _userName);
		}
		
		if (_userName != null) {
			textView.setText(textView.getText() + " " + _userName);
		}
	}

	/**
	 * This method sets the theme and language as per user preference
	 */
	private void setUserPreferences() {
		int theme = _reportSingleton.setTheme();
		getWindow().setBackgroundDrawableResource(R.drawable.black);

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

	@Override
	/**
	 * This method is called when the user returns to an intent from a paused state
	 */
	protected void onResume() {
		super.onResume();

		_reportSingleton.setName(_userName);
		setUserPreferences();
		onCreate(null);
	}

	/**
	 * It is triggered to specify the options menu for an acitivity
	 */
	@SuppressWarnings("unused")
	private void getOverflowMenu() {

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_form, menu);

		// return true;
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_EditProfile:
			editProfile();
			return true;
		case R.id.menu_logout:
			System.out.println("logoutClick");
			onLogoutClick();
			return true;
		case R.id.menu_EditSettings:
			editSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method to be called while setting the language for the UI
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
	 * Called when the user clicks the Update Profile button, starts the
	 * UpdateProfile intent
	 * 
	 * @param view
	 */
	public void editProfile() {
		Intent intent = new Intent(this, UpdateProfileActivity.class);
		intent.putExtra("userName", _userName);
		startActivity(intent);
	}

	/**
	 * Called when the user clicks the settings button, starts the settings
	 * intent
	 * 
	 * @param view
	 */
	public void editSettings() {
		Intent intent = new Intent(this, UpdateSettingsActivity.class);
		intent.putExtra("userName", _userName);
		startActivity(intent);
	}

	/**
	 * Called when the user clicks the High Priority button, triggers the
	 * ReportIncident intent
	 * 
	 * @param view
	 */
	public void incident(View view) {
		Intent intent = new Intent(this, ReportIncidentActivity.class);
		_reportSingleton.setReportType(1); // high
		startActivity(intent);
	}

	/**
	 * Called when the user clicks the Low Priority button, triggers the
	 * ReportIncident intent
	 * 
	 * @param view
	 */
	public void report(View view) {
		Intent intent = new Intent(this, ReportIncidentActivity.class);
		_reportSingleton.setReportType(2); // low priority
		startActivity(intent);
	}

	public void onLogoutClick() {
		new AlertDialog.Builder(this)
				.setTitle("Logout")
				.setMessage("Are you sure you want to log out?")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Login login = new Login();
								login.logOut(MainFormActivity.this);
								Intent intent = new Intent(getBaseContext(),
										LoginActivity.class);
								startActivity(intent);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}

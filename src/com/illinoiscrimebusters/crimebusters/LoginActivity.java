package com.illinoiscrimebusters.crimebusters;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crime.crimebusters.R;
import com.illinoiscrimebusters.login.Login;

/**
 * Activity for the Login page.
 * @author Chris 
 * Testing
 */
public class LoginActivity extends Activity {	
	private ReportSingleton _reportSingleton = ReportSingleton.getInstance();
	private SharedPreferences _preference;
	
	@Override
	/**
	 * This is the method that is called when an intent is initialized for the first time
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setUserPreferences();
		setContentView(R.layout.activity_login);   
	}

	/**
	 * This method sets the theme and language as per user preference
	 */
	private void setUserPreferences() {
		int theme = _reportSingleton.setTheme();
		getWindow().setBackgroundDrawableResource(theme);
		
		String lang = _reportSingleton.getLanguage();
		if (lang!=null)
		{
			if (lang.equalsIgnoreCase("English"))
				changeLocale("en");
			
			if (lang.equalsIgnoreCase("French"))
				changeLocale("fr");
			
			if (lang.equalsIgnoreCase("Spanish"))
				changeLocale("es");
			
		}
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
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
 
        // Setting the title for the activity, after configuration change
        setTitle(R.string.app_name);
        
	}
	
	/**
	 * This method is called when the user returns to an intent from a paused state
	 */
	protected void onResume() {
		super.onResume();
		_preference = getSharedPreferences("cbPreference", MODE_PRIVATE);
		boolean isAuthenticated = _preference.getBoolean("isAuthenticated", false);
				
		//If the user has been authenticated before, redirect the user to the Main Form
		if (isAuthenticated) {
			String userName = _preference.getString("userName", "");
			redirectToMainForm(userName);
			LoginActivity.this.finish();
		}
	}
	

	@Override
	/**
	 * It is triggered to specify the options menu for an acitivity
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	/** 
	 * Event handler for the login button 
	 * 
	 * It is triggered when the user logs in and authentication is performed 
	 * 
	 * @throws ExecutionException 
	 * @throws InterruptedException */
	public void loginUser(View view) throws InterruptedException, ExecutionException {	
		ProgressDialog dialog = 
				ProgressDialog.show(this, "Logging in", "Please wait...", true);
		
		EditText editUserName = (EditText) findViewById(R.id.email);
		String userName = editUserName.getText().toString();
		
		_reportSingleton.setName(userName);
		
		EditText editPassword = (EditText) findViewById(R.id.password);
		String password = editPassword.getText().toString();
		
		if (!validateFields(userName, password)) {
			Toast.makeText(this, 
					"Username and password required.", 
					Toast.LENGTH_LONG).show();
			dialog.dismiss();
			return;
		}
		
		Login login = new Login();
		String loginStatus = login.validateCredentials(userName, password);
	
		if (loginStatus.equals("success")) {	
			SharedPreferences pref = getSharedPreferences("cbPreference", MODE_PRIVATE);
			pref.edit().putBoolean("isAuthenticated", true).putString("userName", userName).commit();
			
			redirectToMainForm(userName);
			dialog.dismiss();
		} else {
			Toast.makeText(getApplicationContext(), "Login failed! " + 
					loginStatus, Toast.LENGTH_LONG).show();
			dialog.dismiss();
		}
	}
	
	/**
	 * Validates the userName and password before sending a web service call.
	 * @param userName userName of the user.
	 * @param password password of the user
	 * @return
	 */
	private boolean validateFields(String userName, String password) {
		if (isFieldEmpty(userName, password)) {
			return false;
		}
		
		return true;
	}

	/**
	 * Validates for empty fields.
	 * @param userName userName of the user
	 * @param password password of the user
	 * @return true if one field is empty
	 */
	private boolean isFieldEmpty(String userName, String password) {
		return userName.equals("") || password.equals("");
	}

	/**
	 * Starts the Register User activity
	 * @param view
	 */
	public void createAccount(View view) {
		startActivity(new Intent(this, RegisterUserActivity.class));
	}	

	/**
	 * Redirects the user to the MainFormActivity 
	 * if the user has been authenticated previously.
	 * @param userName userName of the user.
	 */
	private void redirectToMainForm(String userName) {
		Intent intent = new Intent(this, MainFormActivity.class);
		intent.putExtra("userName", userName);		
		startActivity(intent);
	}
}

package com.illinoiscrimebusters.crimebusters;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.crime.crimebusters.R;

public class UpdateSettingsActivity extends Activity implements
		OnItemSelectedListener {
	private String _userName;
	private UpdatedReportSingleton _reportSingleton = UpdatedReportSingleton.getInstance();
	Spinner spinnerLanguage;
	EditText selLanguage;
	Spinner spinnerPreferredContact; 
	private String[] contactMethods = {"Phone Call", "Text", "Email", "None"};
	private String[] language = { "English", "French", "Spanish" };

	@Override
	/**
	 * This is the method that is called when an intent is initialized for the first time
	 */
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setTitle("Settings");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_settings);
		set_userName(getIntent().getStringExtra("userName"));

		setUserPreferences();

		spinnerLanguage = (Spinner) findViewById(R.id.spinnerlanguage);
		spinnerPreferredContact = (Spinner) findViewById(R.id.spinnerPreferredContact);
		
		ArrayAdapter<String> adapter_language = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, language);
		adapter_language
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		ArrayAdapter<String> adapterContact = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contactMethods);
		adapterContact.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPreferredContact.setAdapter(adapterContact);
		spinnerLanguage.setAdapter(adapter_language);
		spinnerLanguage.setOnItemSelectedListener(this);
		spinnerLanguage.setSelection(_reportSingleton.getPosition(), false);

	}

	/**
	 * 
	 * This method is called to set user preferences
	 */
	private void setUserPreferences() {
		/*
		 * int theme = _reportSingleton.setTheme();
		 * getWindow().setBackgroundDrawableResource(theme);
		 */

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
	 * This is the function that sets the language when it is modified or when
	 * the intent is activated
	 * 
	 */
	private void languagePreference() {
		String selLang = (String) spinnerLanguage.getSelectedItem();
		if (selLang.equalsIgnoreCase("English"))
			changeLocale("en");

		if (selLang.equalsIgnoreCase("French"))
			changeLocale("fr");

		if (selLang.equalsIgnoreCase("Spanish"))
			changeLocale("es");
	}

	/**
	 * This method is called when a language is set from the language drop down
	 * 
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		spinnerLanguage.setSelection(position);
		spinnerPreferredContact.setSelection(position);

		String selLang = (String) spinnerLanguage.getSelectedItem();
		String selContact = (String) spinnerPreferredContact.getSelectedItem();
		
		for (int i = 0; i < 3; i++) {
			if (language[i].equalsIgnoreCase(selLang)) {
				_reportSingleton.setPosition(i);
			}
		}
		
		for (int i = 0; i < 4; i++) {
			if (contactMethods[i].equalsIgnoreCase(selContact)) {
				_reportSingleton.setPosition(i);
			}
		}
		
		
	}

	@Override
	/**
	 * Empty auto generated stub for the abstract method onNothingSelected for OnItemSelectedListener
	 */
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * This method is called when the button Change Language is clicked
	 * 
	 * @param view
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void changeLanguage(View view) throws InterruptedException,
			ExecutionException {

		languagePreference();
		String selLang = (String) spinnerLanguage.getSelectedItem();
		_reportSingleton.setLanguage(selLang);

		onCreate(null);
		onResume();

	}

	@Override
	/**
	 * This method is called when the user returns to an intent from a paused state
	 */
	protected void onResume() {
		super.onResume();
		setUserPreferences();
		spinnerLanguage.setSelection(_reportSingleton.getPosition(), false);
	}

	/**
	 * This method is called when the "Change Theme" button is clicked
	 * 
	 * @param view
	 *            The object that throws the event.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void changeTheme(View view) throws InterruptedException,
			ExecutionException {

		int theme = _reportSingleton.getThemeNumber();

		if (theme == R.style.MyTheme) {
			getWindow().setBackgroundDrawableResource(R.drawable.b6);
			_reportSingleton.setThemeNumber(R.style.MyTheme2);
		}

		else if (theme == R.style.MyTheme2) {
			getWindow().setBackgroundDrawableResource(R.drawable.orange);
			_reportSingleton.setThemeNumber(R.style.MyTheme3);
		}

		else if (theme == R.style.MyTheme3) {
			getWindow().setBackgroundDrawableResource(R.drawable.c8);
			_reportSingleton.setThemeNumber(R.style.MyTheme);
		}
	}

	/**
	 * This method is called when a new locale is created for a language
	 * 
	 * @param language
	 * @param config
	 */
	private void changeLocale(String language) {

		Configuration config = getResources().getConfiguration();

		// Creating an instance of Locale
		Locale locale = new Locale(language);

		// Setting locale of the configuration
		config.locale = locale;

		// Updating the application configuration
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		// Setting the title for the activity, after configuration change
		setTitle(R.string.app_name);

	}
	
	public void onUpdateSettings(View view) throws InterruptedException, ExecutionException{
		ProgressDialog dialog = ProgressDialog.show(this, "Updating settings",
				"Please wait...", true);
		
		Spinner preferredContact = (Spinner) findViewById(R.id.spinnerPreferredContact);
		String contact = preferredContact.toString();
		System.out.println("contact: " + contact);
		
		//String updateProfile = user.updateProfile(UpdateSettingsActivity.this);
		//Toast.makeText(this,  , duration)
		Toast.makeText(this, "Successfully updated user profile",
				Toast.LENGTH_LONG).show();
		dialog.dismiss();
	}

	public String get_userName() {
		return _userName;
	}

	public void set_userName(String _userName) {
		this._userName = _userName;
	}
}

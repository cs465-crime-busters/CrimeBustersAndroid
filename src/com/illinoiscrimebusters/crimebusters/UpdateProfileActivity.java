package com.illinoiscrimebusters.crimebusters;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crime.crimebusters.R;
import com.illinoiscrimebusters.user.User;

public class UpdateProfileActivity extends Activity implements
		OnItemSelectedListener {
	private String _userName;
	private UpdatedReportSingleton _reportSingleton = UpdatedReportSingleton.getInstance();

	@Override
	/**
	 * This is the method that is called when an intent is initialized for the first time
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle("Edit Profile");
		
		setContentView(R.layout.activity_update_profile);
		_userName = getIntent().getStringExtra("userName");

		setUserPreferences();

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

	@Override
	/**
	 * Empty auto generated stub for the abstract method onNothingSelected for OnItemSelectedListener
	 */
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * This method is called when the user returns to an intent from a paused state
	 */
	protected void onResume() {
		super.onResume();
		_reportSingleton.setName(_userName);
		setUserPreferences();
		initializeFields();
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

	/**
	 * Initialize fields based from the data retrieved from the web service.
	 */
	private void initializeFields() {
		EditText editFirstName = (EditText) findViewById(R.id.updateProfile_firstName);
		EditText editLastName = (EditText) findViewById(R.id.updateProfile_lastName);
		EditText editPhoneNumber = (EditText) findViewById(R.id.updateProfile_phoneNumber);

		SharedPreferences preference = getSharedPreferences("cbPreference",
				MODE_PRIVATE);
		String firstName = preference.getString("firstName", "");

		if (!firstName.equals("")) {
			initializeFromPreference(editFirstName, editLastName,
					editPhoneNumber, preference);
			return;
		}
		User user = new User(_userName);
		try {
			user.getUserProfile();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initializeFromDatabase(editFirstName, editLastName, editPhoneNumber, user);
	}

	/**
	 * Initialize the values from the database
	 * 
	 * @param editFirstName
	 *            FirstName Edit Text
	 * @param editLastName
	 *            LastNameName Edit Text
	 * @param editPhoneNumber
	 *            PhoneNumber Edit Text
	 * @param editAddress
	 *            Address Edit Text
	 * @param editZipCode
	 *            ZipCode Edit Text
	 * @param radioGender
	 *            Gender Radio button
	 * @param user
	 *            User object from the database
	 */
	private void initializeFromDatabase(EditText editFirstName,
			EditText editLastName, EditText editPhoneNumber, User user) {
		editFirstName.setText(user.getFirstName());
		editLastName.setText(user.getLastName());
		editPhoneNumber.setText(user.getPhoneNumber());
	}

	/**
	 * Initialize the values from the sharedPreference
	 * 
	 * to eliminate the need to query the database everytime a user access the
	 * update profile.
	 * 
	 * @param editFirstName
	 *            FirstName Edit Text
	 * @param editLastName
	 *            LastNameName Edit Text
	 * @param editPhoneNumber
	 *            PhoneNumber Edit Text
	 * @param editAddress
	 *            Address Edit Text
	 * @param editZipCode
	 *            ZipCode Edit Text
	 * @param radioGender
	 *            Gender Radio button
	 * @param preference
	 *            Shared preference object.
	 */
	private void initializeFromPreference(EditText editFirstName,
			EditText editLastName, EditText editPhoneNumber,
			SharedPreferences preference) {
		editFirstName.setText(preference.getString("firstName", ""));
		editLastName.setText(preference.getString("lastName", ""));
		editPhoneNumber.setText(preference.getString("phoneNumber", ""));

		return;
	}

	/**
	 * This method is called when the user clicks on the "Update Profile"
	 * button, after modifying personal information
	 * 
	 * @param view
	 *            The object that throws the event.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void onUpdateProfile(View view) throws InterruptedException,
			ExecutionException {
		ProgressDialog dialog = ProgressDialog.show(this, "Updating profile",
				"Please wait...", true);

		EditText editFirstName = (EditText) findViewById(R.id.updateProfile_firstName);
		String firstName = editFirstName.getText().toString();

		EditText editLastName = (EditText) findViewById(R.id.updateProfile_lastName);
		String lastName = editLastName.getText().toString();

		EditText editPhoneNumber = (EditText) findViewById(R.id.updateProfile_phoneNumber);
		String phoneNumber = editPhoneNumber.getText().toString();

		if (!validateFields(firstName, lastName)) {
			Toast.makeText(this,
					"First and last name fields are required.",
					Toast.LENGTH_LONG).show();
			dialog.dismiss();
			return;
		}

		User user = new User(_userName, firstName, lastName, phoneNumber);
		String updateStatus = user.updateProfile(UpdateProfileActivity.this);

		if (updateStatus.equals("success")) {
			Toast.makeText(this, "Successfully updated user profile",
					Toast.LENGTH_LONG).show();
			dialog.dismiss();

		} else {
			Toast.makeText(
					this,
					"Error in updating user profile. Error Details: "
							+ updateStatus, Toast.LENGTH_LONG).show();
			dialog.dismiss();
		}
	}

	/**
	 * Validates the firstName, lastName and gender before calling the web
	 * service
	 * 
	 * @param firstName
	 *            First name of the user
	 * @param lastName
	 *            Last name of the user.
	 * @param selectedGenderId
	 *            ID of the selected gender
	 * @return true if validation succeeds.
	 */
	private boolean validateFields(String firstName, String lastName) {
		if (isFieldEmpty(firstName, lastName)) {
			return false;
		}
		return true;
	}

	/**
	 * Validates empty fields
	 * 
	 * @param firstName
	 *            First name of the user
	 * @param lastName
	 *            Last name of the user.
	 * @return true if both fields are non empty
	 */
	private boolean isFieldEmpty(String firstName, String lastName) {
		return firstName.equals("") || lastName.equals("");
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	}
}

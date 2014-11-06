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
	//Spinner spinnerLanguage;
	//EditText selLanguage;
	//private String[] language = { "English", "French", "Spanish" };

	@Override
	/**
	 * This is the method that is called when an intent is initialized for the first time
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
		_userName = getIntent().getStringExtra("userName");

		setUserPreferences();

		//spinnerLanguage = (Spinner) findViewById(R.id.spinnerlanguage);

	/*	ArrayAdapter<String> adapter_language = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, language);
		adapter_language
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerLanguage.setAdapter(adapter_language);
		spinnerLanguage.setOnItemSelectedListener(this);
		spinnerLanguage.setSelection(_reportSingleton.getPosition(), false);
*/
	}

	/**
	 * 
	 * This method is called to set user preferences
	 */
	private void setUserPreferences() {
		/*int theme = _reportSingleton.setTheme();
		getWindow().setBackgroundDrawableResource(theme);*/

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
	/*private void languagePreference() {
		String selLang = (String) spinnerLanguage.getSelectedItem();
		if (selLang.equalsIgnoreCase("English"))
			changeLocale("en");

		if (selLang.equalsIgnoreCase("French"))
			changeLocale("fr");

		if (selLang.equalsIgnoreCase("Spanish"))
			changeLocale("es");
	}*/

	/**
	 * This method is called when a language is set from the language drop down
	 * 
	 */
	/*public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		spinnerLanguage.setSelection(position);

		String selLang = (String) spinnerLanguage.getSelectedItem();

		for (int i = 0; i < 3; i++) {
			if (language[i].equalsIgnoreCase(selLang)) {
				_reportSingleton.setPosition(i);
			}

		}
	}*/

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
	/*	public void changeLanguage(View view) throws InterruptedException,
			ExecutionException {

		//languagePreference();
		String selLang = (String) spinnerLanguage.getSelectedItem();
		_reportSingleton.setLanguage(selLang);

		onCreate(null);
		onResume();

	}
*/
	@Override
	/**
	 * This method is called when the user returns to an intent from a paused state
	 */
	protected void onResume() {
		super.onResume();
		_reportSingleton.setName(_userName);
		setUserPreferences();
		initializeFields();
		//onCreate(null);
		//spinnerLanguage.setSelection(_reportSingleton.getPosition(), false);
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
		EditText editAddress = (EditText) findViewById(R.id.updateProfile_address);
		EditText editZipCode = (EditText) findViewById(R.id.updateProfile_zipCode);
		RadioGroup radioGender = (RadioGroup) findViewById(R.id.updateProfile_gender);

		SharedPreferences preference = getSharedPreferences("cbPreference",
				MODE_PRIVATE);
		String firstName = preference.getString("firstName", "");

		if (!firstName.equals("")) {
			initializeFromPreference(editFirstName, editLastName,
					editPhoneNumber, editAddress, editZipCode, radioGender,
					preference);
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

		initializeFromDatabase(editFirstName, editLastName, editPhoneNumber,
				editAddress, editZipCode, radioGender, user);
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
			EditText editLastName, EditText editPhoneNumber,
			EditText editAddress, EditText editZipCode, RadioGroup radioGender,
			User user) {
		editFirstName.setText(user.getFirstName());
		editLastName.setText(user.getLastName());
		editPhoneNumber.setText(user.getPhoneNumber());
		editAddress.setText(user.getAddress());
		editZipCode.setText(user.getZipCode());

		RadioButton selectedRadioButton = (RadioButton) radioGender
				.findViewById(user.getGender().equals("M") ? R.id.male
						: R.id.female);
		selectedRadioButton.setChecked(true);
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
			EditText editAddress, EditText editZipCode, RadioGroup radioGender,
			SharedPreferences preference) {
		editFirstName.setText(preference.getString("firstName", ""));
		editLastName.setText(preference.getString("lastName", ""));
		editPhoneNumber.setText(preference.getString("phoneNumber", ""));
		editAddress.setText(preference.getString("address", ""));
		editZipCode.setText(preference.getString("zipCode", ""));

		RadioButton selectedRadioButton = (RadioButton) radioGender
				.findViewById(preference.getString("zipCode", "").equals("M") ? R.id.male
						: R.id.female);
		selectedRadioButton.setChecked(true);

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

		EditText editAddress = (EditText) findViewById(R.id.updateProfile_address);
		String address = editAddress.getText().toString();

		EditText editZipCode = (EditText) findViewById(R.id.updateProfile_zipCode);
		String zipCode = editZipCode.getText().toString();

		RadioGroup radioGender = (RadioGroup) findViewById(R.id.updateProfile_gender);
		int checkedGenderId = radioGender.getCheckedRadioButtonId();
		String gender = checkedGenderId == R.id.male ? "M" : "F";

		if (!validateFields(firstName, lastName, checkedGenderId)) {
			Toast.makeText(this,
					"First, last name and gender fields are required.",
					Toast.LENGTH_LONG).show();
			dialog.dismiss();
			return;
		}

		User user = new User(_userName, firstName, lastName, gender,
				phoneNumber, address, zipCode);
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
	private boolean validateFields(String firstName, String lastName,
			int selectedGenderId) {
		if (isFieldEmpty(firstName, lastName) || selectedGenderId == -1) {
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

	/**
	 * This method is called when the user clicks on the "log Out" button.
	 * 
	 * @param view
	 */
	/*public void onLogoutClick(View view) {
		new AlertDialog.Builder(this)
				.setTitle("Logout")
				.setMessage("Are you sure you want to log out?")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Login login = new Login();
								login.logOut(UpdateProfileActivity.this);
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
*/
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	}
}

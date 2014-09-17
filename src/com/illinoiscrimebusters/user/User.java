package com.illinoiscrimebusters.user;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.illinoiscrimebusters.util.RequestMethod;
import com.illinoiscrimebusters.util.RestClient;

/**
 * Contains the business logic for the User module.
 * 
 * @author Chris
 * 
 */
public class User {
	private String _userName;
	private String _firstName;
	private String _lastName;
	private String _gender;
	private String _phoneNumber;
	private String _address;
	private String _zipCode;
	private SharedPreferences _preference;
	private final String UPDATE_PROFILE_SERVICE = "http://illinoiscrimebusters.com/Services/UpdateProfile.ashx";
	private final String USER_INFO_SERVICE = "http://illinoiscrimebusters.com/Services/GetUserInfo.ashx";

	public User() {

	}

	/**
	 * Get usernane
	 * 
	 * @param userName
	 */
	public User(String userName) {
		_userName = userName;
	}

	/**
	 * Creates a new user
	 * 
	 * @param userName
	 * @param firstName
	 * @param lastName
	 * @param gender
	 * @param phoneNumber
	 * @param address
	 * @param zipCode
	 */
	public User(String userName, String firstName, String lastName,
			String gender, String phoneNumber, String address, String zipCode) {
		_userName = userName;
		_firstName = firstName;
		_lastName = lastName;
		_gender = gender;
		_phoneNumber = phoneNumber;
		_address = address;
		_zipCode = zipCode;
	}

	/**
	 * Updates the user profile.
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public String updateProfile(Activity activity) throws InterruptedException,
			ExecutionException {
		AsyncTask<String, Void, String> task = new UpdateUserTask().execute(
				_firstName, _lastName, _gender, _phoneNumber, _address,
				_zipCode, _userName);
		try {
			JSONObject jsonObject = new JSONObject(task.get());
			String result = jsonObject.getString("result");

			if (result.equals("success")) {
				activity.getApplicationContext();
				_preference = activity.getSharedPreferences("cbPreference",
						Context.MODE_PRIVATE);
				_preference.edit().putString("firstName", _firstName)
						.putString("lastName", _lastName)
						.putString("phoneNumber", _phoneNumber)
						.putString("address", _address)
						.putString("zipCode", _zipCode)
						.putString("gender", _gender).commit();
			}

			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Gets the user profile from the web service
	 * 
	 * @param userName
	 *            UserName of the user.
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void getUserProfile() throws InterruptedException,
			ExecutionException {
		AsyncTask<String, Void, String> task = new GetUserTask()
				.execute(_userName);
		try {
			JSONObject jsonObject = new JSONObject(task.get());

			setFirstName(jsonObject.getString("FirstName"));
			setLastName(jsonObject.getString("LastName"));
			setGender(jsonObject.getString("Gender"));
			setPhoneNumber(jsonObject.getString("PhoneNumber"));
			setAddress(jsonObject.getString("Address"));
			setZipCode(jsonObject.getString("ZipCode"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates the user profile asynchronously.
	 * 
	 * @author Chris
	 * 
	 */
	private class UpdateUserTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {
			RestClient client = new RestClient(UPDATE_PROFILE_SERVICE);
			client.AddParam("firstName", params[0]);
			client.AddParam("lastName", params[1]);
			client.AddParam("gender", params[2]);
			client.AddParam("phoneNumber", params[3]);
			client.AddParam("address", params[4]);
			client.AddParam("zipCode", params[5]);
			client.AddParam("userName", params[6]);

			try {
				client.Execute(RequestMethod.POST);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return client.getResponse();
		}
	}

	/**
	 * Gets the user information asynchronously.
	 * 
	 * @author Chris
	 * 
	 */
	private class GetUserTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {
			RestClient client = new RestClient(USER_INFO_SERVICE);
			client.AddParam("userName", params[0]);

			try {
				client.Execute(RequestMethod.GET);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return client.getResponse();
		}
	}

	/**
	 * Gets the userName
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return _userName;
	}

	/**
	 * Sets the userName
	 * 
	 * @param userName
	 *            username of the user
	 */
	public void setUserName(String userName) {
		this._userName = userName;
	}

	/**
	 * Gets the first name
	 * 
	 * @return the first name of the user
	 */
	public String getFirstName() {
		return _firstName;
	}

	/**
	 * Sets the first name
	 * 
	 * @param firstName
	 *            first name of the user
	 */
	public void setFirstName(String firstName) {
		this._firstName = firstName;
	}

	/**
	 * Gets the last name
	 * 
	 * @return the last name of the user
	 */
	public String getLastName() {
		return _lastName;
	}

	/**
	 * Sets the user's last name
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this._lastName = lastName;
	}

	/**
	 * Gets the gender of the user
	 * 
	 * @return "M" for male, "F" for female
	 */
	public String getGender() {
		return _gender;
	}

	/**
	 * Sets the gender of the user
	 * 
	 * @param gender
	 *            Either "M" or "F"
	 */
	public void setGender(String gender) {
		this._gender = gender;
	}

	/**
	 * Gets the phone number
	 * 
	 * @return
	 */
	public String getPhoneNumber() {
		return _phoneNumber;
	}

	/**
	 * Sets the phone number
	 * 
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this._phoneNumber = phoneNumber;
	}

	/**
	 * Gets the user address
	 * 
	 * @return the user's address
	 */
	public String getAddress() {
		return _address;
	}

	/**
	 * Sets the user's address
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this._address = address;
	}

	/**
	 * Gets the user's zip code.
	 * 
	 * @return
	 */
	public String getZipCode() {
		return _zipCode;
	}

	/**
	 * Sets the user's zip code.
	 * 
	 * @param zipCode
	 */
	public void setZipCode(String zipCode) {
		this._zipCode = zipCode;
	}
}

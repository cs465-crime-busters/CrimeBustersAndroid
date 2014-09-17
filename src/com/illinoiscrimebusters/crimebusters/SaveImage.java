package com.illinoiscrimebusters.crimebusters;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Within this class we will create a location for our images and store them We
 * will store the images in a designated folder in the gallery section as well
 * as send the image to reportsingleton in order to transfer the data to
 * webservices
 * */

public class SaveImage {

	private Context TheThis;
	private String NameOfFolder = "/CB_Folder/";
	private String NameOfFile = "CBImage"; // Prefix to each file

	/**
	 * Method to save a picture
	 * 
	 * @param context
	 * @param ImageToSave
	 */
	public void SavePic(Context context, Bitmap ImageToSave) {
		TheThis = context;
		String file_path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + NameOfFolder;
		String CurrentDateAndTime = getCurrentDateAndTime();

		File dir = new File(file_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, NameOfFile + CurrentDateAndTime + ".jpg");
		String imageLocation = file_path + NameOfFile + CurrentDateAndTime
				+ ".jpg";

		// Each image is associated to a specific button
		String whichButton = ReportSingleton.getInstance().getWhichButton();

		if (whichButton == "1") {
			ReportSingleton.getInstance().setImage1(imageLocation);
		} else if (whichButton == "2") {
			ReportSingleton.getInstance().setImage2(imageLocation);
		} else {
			ReportSingleton.getInstance().setImage3(imageLocation);

		}

		// We will use jpeg for our images
		try {
			FileOutputStream fOut = new FileOutputStream(file);
			ImageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
			MakeSureFileWasCreatedThenMakeAvailable(file);
			AbleToSave();
		} catch (FileNotFoundException e) {
			UnableToSave();
		} catch (IOException e) {
			UnableToSave();
		}
	}

	//
	/**
	 * Android needs to scan directory and see what kind of files are available
	 * 
	 * @param file
	 *            file to save
	 */
	private void MakeSureFileWasCreatedThenMakeAvailable(File file) {
		MediaScannerConnection.scanFile(TheThis,
				new String[] { file.toString() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					public void onScanCompleted(String path, Uri uri) {
						Log.e("ExternalStorage", "Scanned" + path + ":");
						Log.e("ExternalStorage", "-> uri=" + uri);

					}
				});
	}

	//
	/**
	 * Method to get time and date
	 * 
	 * @return a formatted date
	 */
	private String getCurrentDateAndTime() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}

	/**
	 * Makes a toast if it wasnt able to save
	 */
	private void UnableToSave() {
		Toast.makeText(TheThis, "Picture cannot be saved to gallery",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Makes a toast if it was able to save
	 */
	private void AbleToSave() {
		Toast.makeText(TheThis, "Picture saved", Toast.LENGTH_SHORT).show();
	}
}

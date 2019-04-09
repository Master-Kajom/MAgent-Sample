package com.softclans.agents;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.softclans.agents.helper.AsyncTaskActivity;
import com.softclans.agents.helper.DatabaseHelper;
import com.softclans.agents.helper.Globalvar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * The Class SplashScreen will launched at the start of the application. It will
 * be displayed for 3 seconds and than finished automatically and it will also
 * start the next activity of app.
 */
@SuppressWarnings("unused")
public class SplashScreen extends Activity {

	/** Check if the app is running. */
	private boolean isRunning;
	DatabaseHelper db;
	int reg = 0;
	Globalvar globalVariable;
	String imei = "";
	String[] pdetails;
	private String agentno;
	boolean internet = false;
	String agentname;
	FileOutputStream fos = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		// create a file
		try {
			fos = openFileOutput("MAGENT", Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		globalVariable = (Globalvar) getApplicationContext();
		// 197.159.142.163
		//http://magent.sobust.com/agentimei/phones/
		//globalVariable.setUrl("http://magent.sobust.com/Agent/");
		//globalVariable.setUrl("http://10.0.0.69:88/Agents/Agent/");
		globalVariable.setUrl("http://197.159.142.163/glico/Agent/");
		//globalVariable.setUrl("http://192.168.0.10/Agent/");
		db = new DatabaseHelper(getApplicationContext());
		isRunning = true;
		checkreg();
		doreg();
		startSplash();
	}

	/*public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}*/

	private void checkreg() {
		// TODO Auto-generated method stub
		// db.checki(0);

		
		final TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei = mngr.getDeviceId();
		Log.e("imei", imei);
		int count = db.reg(imei);
		if (count > 0) {
			reg = count;
		} else {
			reg = 0;
		}
		

	}

	/**
	 * Starts the count down timer for 3-seconds. It simply sleeps the thread
	 * for 3-seconds.
	 */
	private void startSplash() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {

					Thread.sleep(3000);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							doFinish();
						}

					});
				}
			}
		}).start();
	}

	/**
	 * If the app is still running than this method will start the MainActivity
	 * and finish the Splash.
	 */
	private synchronized void doFinish() {

		if (isRunning) {

			isRunning = false;

			gotologin();

		}
	}

	private void gotologin() {
		// TODO Auto-generated method stub
		Log.e("log in called ", "true");
		if (getdetails(imei)) {
			globalVariable.setImei(imei);
			globalVariable.setAgentno(agentno);
			if (reg == 0) {
				Intent i = new Intent(SplashScreen.this, Registration.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			} else if (reg == 2) {
				Intent i = new Intent(SplashScreen.this, Registration.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			} else if (reg == 1) {
				Intent i = new Intent(SplashScreen.this, LoginLocal.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
		} else {
			// Alert that phone is not registered.
			// Create an exit pop up
			AlertDialog.Builder adglobals = new AlertDialog.Builder(
					SplashScreen.this);
			if (internet == true) {

				adglobals.setTitle("Server connection error!");
				adglobals
						.setMessage("Contact administrator to register your phone");
				// adglobals.setIcon(R.drawable.delete);
				internet = false;
			} else {
				adglobals.setTitle("Internet connection error!");
				adglobals.setMessage("Data connection is off ");
			}

			adglobals.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							// closed
							finish();
						}
					});

			// Showing Alert Message
			adglobals.show();

		}
	}

	private void doreg() {
		// TODO Auto-generated method stub
		if (reg == 2) {
			Log.e("Not Registered", "True");
			if (isNetworkAvailable(getBaseContext())) {
				Log.e("Internet", "True");
				internet = true;
				String url_select = globalVariable.getUrl() + "login.php";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("Imei", imei.trim()));
				new AsyncTaskActivity(SplashScreen.this).execute(url_select,
						nameValuePairs, 1);
			} else {
				internet = false;
				Log.e("Internet", "False");
				return;
		
			}
			// }else if(){

		}
	}

	private boolean getdetails(String imei2) {
		// TODO Auto-generated method stub
		pdetails = db.phoneDetails(imei2);
		Log.e("imei from db", pdetails[0]);
		agentno = pdetails[2];
		if (pdetails[0].equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isRunning = false;
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean isInternetAvailable() {
		try {
			InetAddress ipAddr = InetAddress.getByName("www.google.com"); // You
																			// can
																			// replace
																			// it
																			// with
																			// your
																			// name

			if (ipAddr.equals("")) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			return false;
		}

	}

	public static boolean isNetworkAvailable(Context context) {
		return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}

}
package com.softclans.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.softclans.agents.helper.BranchTag;
import com.softclans.agents.helper.DatabaseHelper;
import com.softclans.agents.helper.Globalvar;
import com.softclans.agents.helper.ItemSelectedListener;

public class Login extends ActionBarActivity {

	SharedPreferences app_preferences;
	EditText editUsername, editUserPassword;
	Spinner spinnerBranch;
	Button buttonOK, buttonLCancel;
	TextView textViewAttempts;
	String lname = "", lpass = "", host = Index.editHost.getText().toString(),
			name = Index.editUser.getText().toString(),
			pass = Index.editPassword.getText().toString(), userbranch = "",
			userbranchid = "", username = "", userpass = "";
	protected ArrayList<NameValuePair> nameValuePairs;
	DatabaseHelper db;
	int counter = 0;

	// Calling Application class (see application tag in AndroidManifest.xml)
	Globalvar globalVariable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		getSupportActionBar().setTitle("M-agent");
		globalVariable = (Globalvar) getApplicationContext();

		editUsername = (EditText) findViewById(R.id.editUsername);
		editUserPassword = (EditText) findViewById(R.id.editUserPassword);
		spinnerBranch = (Spinner) findViewById(R.id.spinnerBranch);
		buttonOK = (Button) findViewById(R.id.buttonOK);
		textViewAttempts = (TextView) findViewById(R.id.textViewAttempts);
		db = new DatabaseHelper(getApplicationContext());
		if (Index.sync.isChecked()) {
			addItemsOnBranch();
		} else {
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Login.this,
					android.R.layout.simple_spinner_item, db.branchlist());
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerBranch.setAdapter(dataAdapter);
		}
		
		db.closeDB();

		spinnerBranch.setOnItemSelectedListener(new ItemSelectedListener());

		buttonOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username = editUsername.getText().toString();
				userpass = editUserPassword.getText().toString();
				userbranch = String.valueOf(spinnerBranch.getSelectedItem());
				userbranchid = ItemSelectedListener.idbranch;

				Log.e("userbranch", userbranchid);
				userlogin();
			}
		});
	}

	protected void userlogin() {
		Log.e("host", host);
		Log.e("name", name);
		Log.e("pass", pass);
		Log.e("userbranchid", userbranchid);
		// TODO Auto-generated method stub
		nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("Host", host.trim()));
		nameValuePairs.add(new BasicNameValuePair("Name", name.trim()));
		nameValuePairs.add(new BasicNameValuePair("Pass", pass.trim()));
		nameValuePairs.add(new BasicNameValuePair("Username", username.trim()));
		nameValuePairs.add(new BasicNameValuePair("Userbranch", userbranchid
				.trim()));
		nameValuePairs.add(new BasicNameValuePair("Userpass", userpass.trim()));
		String url_select = globalVariable.getUrl() + "login.php";
		new MyAsyncTaskcr().execute(url_select, nameValuePairs, 2);
	}

	private void addItemsOnBranch() {
		Log.e("host", host);
		Log.e("name", name);
		Log.e("pass", pass);
		// TODO Auto-generated method stub

		nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("Host", host.trim()));
		nameValuePairs.add(new BasicNameValuePair("Name", name.trim()));
		nameValuePairs.add(new BasicNameValuePair("Pass", pass.trim()));
		String url_select = globalVariable.getUrl() + "branch.php";
		// String url_select = "http://10.0.2.2:91/Agent/schemes.php";
		// String url_select = "http://www.softclans.co.ke/Agent/schemes.php";
		// String url_select = "http://agents.sobust.com/schemes.php";

		new MyAsyncTaskcr().execute(url_select, nameValuePairs, 1);
		/**/
	}

	class MyAsyncTaskcr extends AsyncTask<Object, String, Void> {

		private ProgressDialog progressDialog = new ProgressDialog(Login.this);
		InputStream inputStream = null;
		String result = "";
		int n = 0;

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading...");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					MyAsyncTaskcr.this.cancel(true);
				}
			});
		}

		@Override
		protected Void doInBackground(Object... params) {

			// Add your data
			String url = (String) params[0];
			@SuppressWarnings("unchecked")
			ArrayList<NameValuePair> namepairvalue = (ArrayList<NameValuePair>) params[1];
			n = (Integer) params[2];
			try {
				// Set up HTTP post
				// HttpClient is more then less deprecated. Need to change to
				// URLConnection
				HttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(namepairvalue));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				// Read content & Log
				inputStream = httpEntity.getContent();
			} catch (UnsupportedEncodingException e1) {
				Log.e("UnsupportedEncodingException", e1.toString());
				e1.printStackTrace();
			} catch (ClientProtocolException e2) {
				Log.e("ClientProtocolException", e2.toString());
				e2.printStackTrace();
			} catch (IllegalStateException e3) {
				Log.e("IllegalStateException", e3.toString());
				e3.printStackTrace();
			} catch (IOException e4) {
				Log.e("IOException", e4.toString());
				e4.printStackTrace();
			}
			// Convert response to string using String Builder
			try {
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sBuilder = new StringBuilder();

				String line = null;
				while ((line = bReader.readLine()) != null) {
					sBuilder.append(line + "\n");
				}/**/

				inputStream.close();
				result = sBuilder.toString();

			} catch (Exception e) {
				Log.e("StringBuilding & BufferedReader",
						"Error converting result " + e.toString());
			}
			return null;
		} // protected Void doInBackground(String... params)

		@Override
		@SuppressLint("NewApi")
		protected void onPostExecute(Void v) {
			if (n == 1) {
				try {
					Log.e("result", result);
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0, count = jsonArray.length(); i < count; i++) {
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// jsonObject.opt("Description");
							String description = jsonObject.opt("branch_name")
									.toString();
							String branch = jsonObject.opt("branch").toString();
							// stringArray.add(description);
							// stringArray.add(jsonObject.toString());
							// add INFO TO DATABASE
							Log.e("", branch);
							// db.checki();
							branchToDb(description, branch);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					// ArrayAdapter<String> dataAdapter = new
					// ArrayAdapter<String>(this,
					// android.R.layout.simple_spinner_item,stringArray);

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
							Login.this, android.R.layout.simple_spinner_item,
							db.branchlist());
					dataAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerBranch.setAdapter(dataAdapter);
					/**/
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (n == 2) {
				confirmCredentials(result);
			}
			// getArray(result);
			this.progressDialog.dismiss();
		} // protected void onPostExecute(Void v)

	}

	public void branchToDb(String brname, String branchr) {
		// TODO Auto-generated method stub
		Log.d("Scheme todb", "Tag todb: " + brname);
		BranchTag tag1 = new BranchTag(brname, branchr);
		@SuppressWarnings("unused")
		long tag2_id = db.createBranchTag(tag1);
	}

	public void confirmCredentials(String result) {
		// TODO Auto-generated method stub
		try {
			if (result.charAt(0) == 'Y') {
				Toast.makeText(Login.this, "login successfull",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Login.this, Home.class);
				//Intent intent = new Intent(Login.this, Client.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				Login.this.finish();
			} else if (result.charAt(0) == 'N') {

				// limit the number of attempts that can be made
				counter = counter + 1;
				if (counter == 1) {
					Toast toast2 = Toast.makeText(getBaseContext(),
							"Wrong userName password combination",
							Toast.LENGTH_SHORT);
					toast2.show();
					textViewAttempts.setText("Attempts " + (3 - counter));

					editUsername.setText("");
					editUserPassword.setText("");
				} else if (counter == 2) {
					Toast toast2 = Toast.makeText(getBaseContext(),
							"Wrong userName password combination",
							Toast.LENGTH_SHORT);
					toast2.show();
					textViewAttempts.setText("Attempts " + (3 - counter));
					editUsername.setText("");
					editUserPassword.setText("");
				} else {
					Toast toast2 = Toast.makeText(getBaseContext(),
							"Application blocked", Toast.LENGTH_SHORT);
					toast2.show();
					finish();
				}
			} else {
				Toast toast2 = Toast.makeText(getBaseContext(), "Error: "
						+ result, Toast.LENGTH_SHORT);
				toast2.show();
			}
		} catch (Exception e) {
			Toast.makeText(Login.this, "error" + e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

}

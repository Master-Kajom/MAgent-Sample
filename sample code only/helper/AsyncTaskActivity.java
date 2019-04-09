package com.softclans.agents.helper;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.softclans.agents.Home;
import com.softclans.agents.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TwoLineListItem;

public class AsyncTaskActivity extends AsyncTask<Object, String, Void> {

	private Context context;
	ProgressDialog prog;
	DatabaseHelper db;
	private AlertDialog adglobal;
	ListView markets, clients, payments, reports;
	public final static String EXTRA_MESSAGE = "com.softclans.agents";

	public AsyncTaskActivity(Context context) {
		this.context = context;
	}

	// progressDialog = new ProgressDialog(context);
	InputStream inputStream = null;
	String result = "";
	int n = 0;
	private String tag = "Asynch Task";
	Context c;
	ArrayList<NameValuePair> namepairvalue;

	@Override
	protected void onPreExecute() {
		Log.e(tag, "onPreExecute");
		db = new DatabaseHelper(context);
		//v = inflater.inflate(R.layout.payments, container, false);
		
		prog = new ProgressDialog(context);
		prog.setMessage("Loading...");
		prog.show();
		prog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				AsyncTaskActivity.this.cancel(true);
			}
		});
		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(Object... params) {
		Log.e(tag, "doInBackground");
		// TODO Auto-generated method stub
		// Add your data
		String url = (String) params[0];
	
		this.namepairvalue = (ArrayList<NameValuePair>) params[1];
		n = (Integer) params[2];
		if (n == 4) {
			c = (Context) params[3];
		}
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
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sBuilder = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}/**/

			inputStream.close();
			result = sBuilder.toString();

		} catch (Exception e) {
			Log.e("StringBuilding & BufferedReader", "Error converting result "+ e.toString());
		}
		return null;
	}

	@Override
	@SuppressLint("NewApi")
	protected void onPostExecute(Void v) {
		Log.e(tag, "onPostExecute");
		Log.e(tag, "n:" + n);
		if (n == 1) {
			fetch1();
		} else if (n == 2) {
			fetch2();
		} else if (n == 3) {
			fetch3();
		} else if (n == 4) {
			fetch4();
		}
		// getArray(result);
		this.prog.dismiss();
		
		
		
	} // protected void onPostExecute(Void v)

	@SuppressWarnings({ "deprecation", "unused" })
	private void fetch4() {
		// TODO Auto-generated method stub
		Log.e("Internet", result);
		
		// try {
		// JSONArray jsonArray = new JSONArray(result);
		// Log.e(tag, "array lenghth: " + jsonArray.length());
		// for (int i = 0, count = jsonArray.length(); i < count; i++) {
		try {
			//JSONObject jsonObject = new JSONObject(result);
			boolean success;// = jsonObject.optBoolean("success");
			//String rowcount = jsonObject.opt("rowcount").toString();
			String msg = result;// = jsonObject.opt("msg").toString();
			if(msg.equals("Payment Details Synchronised Succesfuly!\n"))
			{ 
				success = true;
			}
			else {
				success = false;
			}
			//Log.e("internet", "" + success);
			adglobal = new AlertDialog.Builder(c).create();
			adglobal.setTitle("Alert");
			// EDIT DATABASE
			if (success) {
				//db.checkis(namepairvalue);
				adglobal.setMessage(msg);
				adglobal.setIcon(R.drawable.tick);
				adglobal.setButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed
					}
				});
				adglobal.show();
			} else {
				if(msg.equals("\n") || msg.equals(null) || msg.length() == 0) {
					adglobal.setMessage("Connection Timed Out not all data Synced. Kindly Sync again until its successfull");
				}else {
					adglobal.setMessage(msg);
				}
				adglobal.setIcon(R.drawable.delete);
				adglobal.setButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed
					}
				});
				// }
				adglobal.show();

				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fetch3() {
		// TODO Auto-generated method stub
		try {
			JSONArray jsonArray = new JSONArray(result);
			if (jsonArray.length() > 0) {
				db.checki(1);
			}
			for (int i = 0, count = jsonArray.length(); i < count; i++) {
				try {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String policy_no = jsonObject.opt("policy_no").toString();
					String client_no = jsonObject.opt("client_no").toString();
					String name = jsonObject.opt("name").toString();
					String email = jsonObject.opt("email").toString();
					String telephone = jsonObject.opt("telephone").toString();
					// add INFO TO DATABASE
					clientToDb(policy_no, client_no, name, email, telephone);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//add an alert to show that sync for markets is complete.
		//Alert that phone is not registered.
		//Create an exit pop up
		AlertDialog.Builder adglobals = new AlertDialog.Builder(this.context);
		adglobals.setTitle("Alert!");
		
		adglobals.setMessage("Clients successfully synchronized");
		//adglobal.setIcon(R.drawable.tick);
		
		
		adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				//re-direct to the markets view.
				/*Home home = new Home();
				home.refreshmarkets(context);*/
				/*DetailFragment dFragment = new DetailFragment();
				dFragment.viewpayments();*/
				
				//refreshmarkets();
				Intent intent = new Intent(context, Home.class);
				String menuItem = "Clients";
				intent.putExtra(EXTRA_MESSAGE,menuItem);
				context.startActivity(intent);
				//context .startActivity(new Intent(context, Home.class));
				
			}
		});
		

		// Showing Alert Message
		adglobals.show();
		
	}

	private void fetch1() {
		// TODO Auto-generated method stub
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			for (int i = 0, count = jsonArray.length(); i < count; i++) {
				try {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String imei = jsonObject.opt("imei").toString();
					String agent_no = jsonObject.opt("agent_no").toString();
					String inactive = jsonObject.opt("inactive").toString();
					// add INFO TO DATABASE
					Log.e("", inactive);
					// db.checki();
					authToDb(imei, agent_no, inactive);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void fetch2() {
		// TODO Auto-generated method stub
		try {
			// Log.e("result", result);
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0, count = jsonArray.length(); i < count; i++) {
				try {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String market_name = jsonObject.opt("market_name")
							.toString();
					String market_code = jsonObject.opt("market_code")
							.toString();
					// add INFO TO DATABASE
					Log.e("", market_code);
					marketToDb(market_code, market_name);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			//add an alert to show that sync for markets is complete.
			//Alert that phone is not registered.
			//Create an exit pop up
			AlertDialog.Builder adglobals = new AlertDialog.Builder(this.context);
			adglobals.setTitle("Alert!");
			
			adglobals.setMessage("Markets successfully synchronized");
			//adglobal.setIcon(R.drawable.tick);
			
			
			adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to execute after dialog closed		
					//refreshmarkets();
					Intent intent = new Intent(context, Home.class);
					String menuItem = "Markets";
					intent.putExtra(EXTRA_MESSAGE,menuItem);
					context.startActivity(intent);
					//context .startActivity(new Intent(context, Home.class));
					
				}
			});
			

			// Showing Alert Message
			adglobals.show();
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void authToDb(String imei, String agent_no, String inactive) {
		// TODO Auto-generated method stub
		Log.e(tag, "authToDb");
		AuthTag tag1 = new AuthTag(imei, agent_no, "", "", "");
		db.createAuthTagTag(tag1);
	}

	public void marketToDb(String market_code, String market_name) {
		// TODO Auto-generated method stub
		Log.d("mkt todb", "Tag todb: " + market_name);
		MarketTag tag1 = new MarketTag(market_name, market_code);
		db.createMarketTag(tag1);
	}

	public void clientToDb(String policy_no, String client_no, String client,
			String email2, String telephone) {
		// TODO Auto-generated method stub
		Log.d("Scheme todb", "Tag todb: " + client);
		ClientTag tag1 = new ClientTag(client_no, policy_no, client, email2,
				telephone);
		db.createClientTag(tag1);
	}

}

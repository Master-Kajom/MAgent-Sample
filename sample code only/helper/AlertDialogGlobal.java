package com.softclans.agents.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.softclans.agents.Home;
import com.softclans.agents.LoginLocal;
import com.softclans.agents.R;

public class AlertDialogGlobal extends FragmentActivity {

	Globalvar globalVariable;
	EditText cpolno, cname, paymentdate, amount;
	Spinner cmarket, paymenttypes;
	DatabaseHelper db;
	ArrayAdapter<String> dataAdapter;
	private int year;
	private int month;
	private int day;
	private AlertDialog adglobal;
	String clientno;

	public AlertDialogGlobal(final Context c, String message, String title,
			final int n) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(c);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage(message).setTitle(title);

		// Add the buttons
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				dothis(c, n);
			}
		});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public AlertDialogGlobal(final Context c, String title, final View v,
			final String[] o, final int n,final boolean pulled_client) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(c);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setTitle(title);
		// .setTitle(title);

		builder.setView(v);
		setValues(c, o, v, n);
		// Add the buttons
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				dothiswithview(c, n, v,o,pulled_client);
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void setValues(final Context c, String[] s, View v, int n) {
		// TODO Auto-generated method stub
		if (n == 2) {
			db = new DatabaseHelper(c);
			cpolno = (EditText) v.findViewById(R.id.editTextPEPolicyNo);
			cname = (EditText) v.findViewById(R.id.editTextPEClientDetails);
			cmarket = (Spinner) v.findViewById(R.id.spinnerMarket);
			paymenttypes = (Spinner) v.findViewById(R.id.spinnerPaymentType);
			paymentdate = (EditText) v.findViewById(R.id.editTextPEPaymentDate);
			amount = (EditText) v.findViewById(R.id.editTextPEAmount);

			cpolno.setText(s[1]);
			cname.setText(s[0]);
			this.clientno = s[2];
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(c,
					android.R.layout.simple_spinner_item, db.marketlists());
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cmarket.setAdapter(dataAdapter);
			
			//TODO 
			int index = 0;
			String[] marketlists =  db.marketlists();
			for(int i=0;i<marketlists.length;i++){
				if(marketlists[i].equals(Home.Agent_Market)){
					index = i;
				}
				
			}
			
			//Object[] marketlists
			
			cmarket.setSelection(index);
			
			//cmarket.setId(5);	
			
			
			
			

			List<String> paymenttype = new ArrayList<String>();
			paymenttype.add("Premium");
			paymenttype.add("Loan");

			dataAdapter = new ArrayAdapter<String>(c,
					android.R.layout.simple_spinner_item, paymenttype);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			paymenttypes.setAdapter(dataAdapter);

			setCurrentDateOnView();

			paymentdate.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDatePickerDialog(v, c);
				}
			});
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected void dothiswithview(Context c, int n, View v,final String [] clientnamepolno, final boolean pulled_client) {
		// TODO Auto-generated method stub

		globalVariable = (Globalvar) c.getApplicationContext();
		String agentno = globalVariable.getAgentno();
		db = new DatabaseHelper(c);

		if (n == 1) {
			adglobal = new AlertDialog.Builder(c).create();
			adglobal.setTitle("Alert");
			Log.e("the jsonarray", "one");

			CheckBox[] cboxes = new CheckBox[3];
			String[] urls = new String[3];
			ArrayList<NameValuePair>[] nameValuePairs = new ArrayList[3];

			cboxes[0] = (CheckBox) v.findViewById(R.id.checkBoxMarkets);
			cboxes[1] = (CheckBox) v.findViewById(R.id.checkBoxClients);
			cboxes[2] = (CheckBox) v.findViewById(R.id.checkBoxPaayments);

			urls[0] = globalVariable.getUrl() + "markets.php";
			urls[1] = globalVariable.getUrl() + "clients.php";
			urls[2] = globalVariable.getUrl() + "payments.php";

			nameValuePairs[0] = new ArrayList<NameValuePair>(1);
			nameValuePairs[1] = new ArrayList<NameValuePair>(1);
			nameValuePairs[2] = new ArrayList<NameValuePair>(1);

			nameValuePairs[0].add(new BasicNameValuePair("AgentNo", agentno
					.trim()));

			nameValuePairs[1].add(new BasicNameValuePair("AgentNo", agentno
					.trim()));

			if (cboxes[2].isChecked()) {
				Log.e("the jsonarray", "two");
				List<PaymentTag> tags = db.paymentlistupload();
				
				Log.e("the jsonarray", "three");
				
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < tags.size(); i++) {
				        jsonArray.put(tags.get(i).getJSONObject());
				}
				Log.e("length jsonarray", ""+jsonArray.length());
				nameValuePairs[2].add(new BasicNameValuePair("paymenttag",
						jsonArray.toString()));
				Log.e("the jsonarray", "5");
				//Log.e("the jsonarray", jsArray.toString());
			}
			if (isNetworkAvailable(c)) {
				for (int i = 0; i < 3; i++) {
					if (cboxes[i].isChecked()) {
						Log.e("Checked", "true" + i);

						new AsyncTaskActivity(c).execute(urls[i],
								nameValuePairs[i], i + 2,c);
					}
				}
			} else {
				adglobal.setMessage("Network Error");
				adglobal.setIcon(R.drawable.delete);
				adglobal.setButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed
					}
				});
				adglobal.show();
			}
		} else if (n == 2) {
			adglobal = new AlertDialog.Builder(c).create();
			adglobal.setTitle("Alert");

			String client_no = clientno;
			String policy_no = cpolno.getText().toString();
			String pamount = amount.getText().toString();
			String client_name = cname.getText().toString();
			String payment_date = paymentdate.getText().toString();
			String agent_no = agentno;
			String imei = globalVariable.getImei();
			
			
			String market_name = cmarket.getSelectedItem().toString();
			//get the market code of the market
			String[] market_codes = db.getmarketcode(market_name);
			String market_code = market_codes[0];
			
			String systime = "" + System.currentTimeMillis();
			String payment_type = paymenttypes.getSelectedItem().toString();

			if (pamount.equals("")) {
				adglobal.setMessage("Please Enter Amount");
				adglobal.setIcon(R.drawable.delete);
				adglobal.setButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed
					}
				});

				// Showing Alert Message
				adglobal.show();
			} else {
				//db.checki(2);
				PaymentTag tag1 = new PaymentTag(client_no, policy_no, pamount,
						client_name, payment_date, payment_type, systime,
						market_code, imei, agent_no);

				try {
					db.createPaymentTag(tag1);
					adglobal.setMessage("Record Saved");
					adglobal.setIcon(R.drawable.tick);
					adglobal.setButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Write your code here to execute after
									// dialog closed

									//TODO insert the record into clientlist
									if(pulled_client == true) {
										ClientTag tag1 = new ClientTag(clientnamepolno[2], clientnamepolno[1], clientnamepolno[0], clientnamepolno[3],
												clientnamepolno[4]);
										db.createClientTag(tag1);
									}
								}
							});

					// Showing Alert Message
					adglobal.show();
				} catch (Exception e) {
					adglobal.setMessage("" + e.getMessage());
					adglobal.setIcon(R.drawable.delete);
					adglobal.setButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Write your code here to execute after
									// dialog closed
								}
							});

					// Showing Alert Message
					adglobal.show();
				}
			}
		}
	}

	protected void dothis(Context c, int n) {
		// TODO Auto-generated method stub
		if (n == 1) {
			Intent i = new Intent(c, LoginLocal.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			c.startActivity(i);
			((Activity) c).finish();
		}
	}

	public void showDatePickerDialog(View v, Context c2) {
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH);

		DatePickerDialog dpd = new DatePickerDialog(c2, dateSetListener, year,
				month, day);
		dpd.show();
	}

	DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			paymentdate.setText(new StringBuilder()
					// Month is 0 based, just add 1
					.append(year).append("-").append(month + 1).append("-")
					.append(day).append(" "));
		} // onDateSet
	}; //

	private void setCurrentDateOnView() {
		// TODO Auto-generated method stub
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		paymentdate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(year).append("-").append(month + 1).append("-")
				.append(day).append(" "));
	}
	public static boolean isNetworkAvailable(Context context) {
	    return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}
}

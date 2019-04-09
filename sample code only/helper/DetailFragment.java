package com.softclans.agents.helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LauncherActivity.ListItem;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.softclans.agents.Client2;
import com.softclans.agents.Home;
import com.softclans.agents.R;
import com.softclans.agents.SplashScreen;


@SuppressWarnings("deprecation")
public class DetailFragment extends Fragment implements OnItemSelectedListener {

	int mCurCheckPosition = 0;
	Globalvar globalVariable;
	public String result;
	public ArrayList<NameValuePair> nameValuePairs;
	ListView markets, clients, payments, reports;
	EditText search;
	DatabaseHelper db;
	public ArrayAdapter<String> adapterMkt, dataAdapter;
	Spinner spinnerOccupation, spinnerPlans;
	public static String paclass = "";
	public static String paclassDesc = "";
	public static EditText dateofbirth;
	public static EditText age;
	protected static final int DATE_DIALOG_ID = 13;
	protected String dob;
	private Pattern pattern;
	private Matcher matcher;
	private int year;
	private int month;
	private int day;
	public static EditText address;
	View layout;
	LayoutInflater inflater;
	TextView text;
	public static EditText email;
	public static EditText phoneNumber;
	public static EditText clientname;
	Button send;
	boolean sms = false;

	public String datefromsonline;
	public String datetosonline;

	//String[] clientnames, policy_nos, clientnos, emails, telephones, searchresults;
	String[] agent_nos,policy_nos,client_names,amounts,payment_dates,searchresults;
	String searchpolno = "";
	//String agent_no,client_name,amount,payment_date = "";

	ViewGroup container1;
	AlertDialogGlobal adglobal;
	AlertDialog adglobals;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = null;
		container1 = container;

		db = new DatabaseHelper(getActivity().getApplicationContext());
		// db.checki();
		globalVariable = (Globalvar) getActivity().getApplicationContext();
		// vehicleno = ((MyApplication)
		// getActivity().getApplication()).getVehicleNumber().toString();

		// Retrieving the currently selected item number
		int position = getArguments().getInt("position");

		// List of rivers
		String[] countries = getResources().getStringArray(R.array.countries);

		if (position == 1) {

			v = inflater.inflate(R.layout.markets, container, false);
			filldata(v, position);

		}else if (position == 0) {
			// mCurCheckPosition = position;
			v = inflater.inflate(R.layout.home, container, false);
			EditText txtagentno = (EditText)v.findViewById(R.id.txtagentno);
			EditText txtagentname = (EditText)v.findViewById(R.id.txtagentname);			
			String agentname;			
			txtagentno.setText(globalVariable.getAgentno());			
			txtagentname.setText(Home.Agent_Name);
			/*Intent intent = new Intent(getActivity(), Home.class);
			getActivity().startActivity(intent);*/
		} 
		else if (position == 2) {
			// mCurCheckPosition = position;
			v = inflater.inflate(R.layout.clients, container, false);
			filldata2(v, position);
		} else if (position == 3) {
			v = inflater.inflate(R.layout.payments, container, false);
			filldata3(v, position);

		} else if (position == 4) {
			mCurCheckPosition = position;
			v = inflater.inflate(R.layout.reports, container, false);
			filldata4(v, position);
		} else if (position == 5) {
			//((Activity) getActivity()).finish();	
		} else if (position == 6) {
			mCurCheckPosition = position;
			v = inflater.inflate(R.layout.client, container, false);
			filldata5(v, position);
		} else {
			mCurCheckPosition = position;
			// Creating view correspoding to the fragment
			v = inflater.inflate(R.layout.fragment_layout, container, false);

			// Getting reference to the TextView of the Fragment
			TextView tv = (TextView) v.findViewById(R.id.tv_content);

			// Setting currently selected river name in the TextView
			tv.setText(countries[position]);

		}

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
		}

		return v;
	}
	
	EditText paymentdate;
	
	private void setCurrentDateOnView1() {
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
	
	String cemail = null;
	String phoneno = null;

	// TODO: Create a method to handle all click listener events for payments
	public void viewpaymentdetails(AdapterView<?> parent, View view,
			final int position, long id) {

		String[] parameters = new String[2];
		TwoLineListItem list_item = (TwoLineListItem) view;
		TextView text1 = list_item.getText1();
		TextView text2 = list_item.getText2();
		parameters[0] = text1.getText().toString();
		//TODO split 
		String[] results = new String[2];
		results = parameters[0].split(" - ",2);		
		parameters[0] = results[0];
		
		parameters[1] = text2.getText().toString();
		String[] pdetails = db.paymentDetails(parameters);
		final String ClientName = pdetails[0];
		final String PolicyNo = pdetails[1];
		final String Amount = pdetails[2];
		final String PaymentDate = pdetails[3];
		final String PaymentType = pdetails[4];
		final String Market = pdetails[5];

		// TODO Auto-generated method stub
		// Creating the instance of PopupMenu
		PopupMenu popup = new PopupMenu(getActivity(), view);
		// Inflating the Popup using xml file
		popup.getMenuInflater().inflate(R.menu.payment_popup, popup.getMenu());

		// registering popup with OnMenuItemClickListener
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			// On Click pick the all the payments details

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.e("item ID", "" + item.getTitle());
				if (item.getTitle().equals("View")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Payment Details");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(
							R.layout.viewpaymentdetails, null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					TextView txtClientName = (TextView) dialogView
							.findViewById(R.id.textClientName);
					TextView txtPolicyNo = (TextView) dialogView
							.findViewById(R.id.textPolicyNo);
					TextView txtAmount = (TextView) dialogView
							.findViewById(R.id.textAmount);
					TextView txtPaymentDate = (TextView) dialogView
							.findViewById(R.id.textPaymentDate);
					TextView txtPaymentType = (TextView) dialogView
							.findViewById(R.id.textPaymentType);
					TextView txtMarket = (TextView) dialogView
							.findViewById(R.id.textMarket);

					txtClientName.setText(ClientName);
					txtPolicyNo.setText(PolicyNo);
					txtAmount.setText(Amount);
					txtPaymentDate.setText(PaymentDate);
					txtPaymentType.setText(PaymentType);
					txtMarket.setText(Market);

					// Add the buttons
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked ok button

								}
							});

					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();

				}
				//if email is selected, send email to client.
				if (item.getTitle().equals("Email / Share pay info")) {
					
					//get the email address of the client, use the client_no
					String mid = (String) paymentlists[position];
					String[] clientno = db.getclientnoss(mid);
					//PaymentTag data = (com.softclans.agents.helper.PaymentTag) db.getclientno(mid);
					//String clientno = data.getPolicy_no();
					//TODO
					//use the client number to get the client details
					String[] clientinfo = db.getclientinfo(clientno[0]);
					cemail = clientinfo[0];
					phoneno = clientinfo[1];
					sms = false;
					//send and email to the client of the payment received.
					loadSomeStuff load = new loadSomeStuff();
					load.execute();
					
					//Toast.makeText(getActivity(),"Hehehehe",Toast.LENGTH_LONG ).show();
				}
				return true;
			}
		});
		popup.show();// showing popup menu
	}

	//TODO viewpayment details
	// TODO: Create a method to handle all click listener events for payments online though
	public void viewpaymentdetailsonline(AdapterView<?> parent, View view,
								   final int position, long id, JSONArray jArray) {
		try {

			String[] parameters = new String[2];
			TwoLineListItem list_item = (TwoLineListItem) view;
			TextView text1 = list_item.getText1();
			TextView text2 = list_item.getText2();
			parameters[0] = text1.getText().toString();
			//TODO split
			String[] results = new String[2];
			results = parameters[0].split(" - ", 2);
			parameters[0] = results[0];

			parameters[1] = text2.getText().toString();
			//String[] pdetails = db.paymentDetails(parameters);
			JSONObject json = jArray.getJSONObject(position);

			// make it display only what you wanna see
			////agent_no,policy_no,client_name,amount,payment_date
			//agent_nos,policy_nos,client_names,amounts,payment_dates
			/*policy_no = json.getString("policy_no");
			agent_no = json.getString("agent_no");
			client_name = json.getString("client_name");
			amount = json.getString("amount");
			payment_date = json.getString("payment_date");*/


			final String ClientName = json.getString("client_name");
			final String PolicyNo = json.getString("policy_no");
			final String Amount = json.getString("amount");
			final String PaymentDate = json.getString("payment_date");
			final String PaymentType = json.getString("payment_type");
			final String Market = json.getString("market_code");

			// TODO Auto-generated method stub
			// Creating the instance of PopupMenu
			PopupMenu popup = new PopupMenu(getActivity(), view);
			// Inflating the Popup using xml file
			popup.getMenuInflater().inflate(R.menu.payment_popup, popup.getMenu());

			// registering popup with OnMenuItemClickListener
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				// On Click pick the all the payments details

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Log.e("item ID", "" + item.getTitle());
					if (item.getTitle().equals("View")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setTitle("Payment Details");

						LayoutInflater inflater = getActivity().getLayoutInflater();
						View dialogView = inflater.inflate(
								R.layout.viewpaymentdetails, null);
						builder.setView(dialogView);
						// builder.setMessage("View Details");
						TextView txtClientName = (TextView) dialogView
								.findViewById(R.id.textClientName);
						TextView txtPolicyNo = (TextView) dialogView
								.findViewById(R.id.textPolicyNo);
						TextView txtAmount = (TextView) dialogView
								.findViewById(R.id.textAmount);
						TextView txtPaymentDate = (TextView) dialogView
								.findViewById(R.id.textPaymentDate);
						TextView txtPaymentType = (TextView) dialogView
								.findViewById(R.id.textPaymentType);
						TextView txtMarket = (TextView) dialogView
								.findViewById(R.id.textMarket);

						txtClientName.setText(ClientName);
						txtPolicyNo.setText(PolicyNo);
						txtAmount.setText(Amount);
						txtPaymentDate.setText(PaymentDate);
						txtPaymentType.setText(PaymentType);
						txtMarket.setText(Market);

						// Add the buttons
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int id) {
										// User clicked ok button

									}
								});

						// Create the AlertDialog
						AlertDialog dialog = builder.create();
						dialog.show();

					}
					//if email is selected, send email to client.
					if (item.getTitle().equals("Email / Share pay info")) {


					}
					return true;
				}
			});
			popup.show();// showing popup menu
		} catch (Exception e) {
			// TODO handle exception
			Log.e("log.tag", "Error Parsing Data " + e.toString());
		}
	}

	//class to post emailaddress
	public class loadSomeStuff extends AsyncTask<String, Integer, String> {
		final ProgressDialog ringProgressDialog = ProgressDialog.show(
				getActivity(), "Please wait..", "Sending...", true);
		
		//String type = String;
		String urls = globalVariable.getUrl() + "payments.php";
		String type;
		boolean internet = false;

		protected void onPreExecute() {
			// example of setting up something
			ringProgressDialog.setCancelable(true);
		}

		@Override
		protected String doInBackground(String... arg0) {
			String result = "";
			InputStream isr = null;
			//String phoneno = phoneno;
			if(isNetworkAvailable(getActivity())){
				try {
					internet = true;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(urls);
					StringEntity Entity = null;
					if (sms == false) {
						Entity = new StringEntity("email=" + cemail + "&sms="
								+ "false");
					} else {
						Entity = new StringEntity("sms=" + "true" + "&phoneno="
								+ phoneno);
					}
					httppost.setHeader("Content-type",
							"application/x-www-form-urlencoded; charset=ASCII");
					httppost.setEntity(Entity);

					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					isr = entity.getContent();
				} catch (Exception e) {
					Log.e("log_tag", "Error in http connection " + e.toString());
				}
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(isr, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					isr.close();
					result = sb.toString();// string holding the json data.

				} catch (Exception e) {
					Log.e("log_tag",
							"Error in http connections " + e.toString());
				}
		}else{
			internet = false;
			
			
		}

			// TODO Auto-generated method stub
			return null;
		}
			@Override
			protected void onPostExecute(String result) {
					//resultView.setText(result);
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				ringProgressDialog.dismiss();
				
				
				
				if(internet == false){
					
					//Create an exit pop up
					AlertDialog.Builder adglobals1 = new AlertDialog.Builder(getActivity());
					//adglobals.setTitle("Alert!");
					
					adglobals1.setMessage("Network Error");
					//adglobal.setIcon(R.drawable.tick);
					
					
					adglobals1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});		
					// Showing Alert Message
					adglobals1.show();
					
				}else{
					AlertDialog.Builder adglobals = new AlertDialog.Builder(getActivity());
					adglobals.setTitle("Success!");
					
					if(sms == false){
						adglobals.setMessage("Email Sent");
					}else{
						adglobals.setMessage("SMS Sent");
					}
					//adglobal.setIcon(R.drawable.tick);
					
					
					adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					

					// Showing Alert Message
					adglobals.show();
					sms = false;
				}
			}
			
				
			
		}
	
	Object[] paymentlists;
	int sizepaymentlist;
	
	public void viewpayments(){
		
		sizepaymentlist = (int)db.paymenttaginfo().size();
		paymentlists = new Object[sizepaymentlist];
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adapterp = new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_2, db.paymenttaginfo()) {
			
			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TwoLineListItem row;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getApplicationContext().getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
					row = (TwoLineListItem) inflater.inflate(
							android.R.layout.simple_list_item_2, null);
				} else {
					row = (TwoLineListItem) convertView;
				}
				PaymentTag data = db.paymenttaginfo().get(position);
				row.getText1().setText(data.getPolicy_no());
				row.getText2().setText(data.getAmount());
				row.getText1().setTextColor(Color.parseColor("#545454"));
				row.getText2().setTextColor(Color.parseColor("#545454"));
				paymentlists[position] = data.getClient_no();
				
				if(data.getPolicy_no().equals("No record found")){
					search.setVisibility(View.GONE);
				}

				return row;
			}
		};
		payments.setAdapter(adapterp);
		
		
		
	}
	
	public void searchpaymentlist(final String search){
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adapterp = new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_2, db.searchpaymentslistpolno(search)) {
			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TwoLineListItem row;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getApplicationContext().getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
					row = (TwoLineListItem) inflater.inflate(
							android.R.layout.simple_list_item_2, null);
				} else {
					row = (TwoLineListItem) convertView;
				}
				PaymentTag data = db.searchpaymentslistpolno(search).get(position);
				row.getText1().setText(data.getPolicy_no());
				row.getText2().setText(data.getAmount());
				row.getText1().setTextColor(Color.parseColor("#545454"));
				row.getText2().setTextColor(Color.parseColor("#545454"));

				return row;
			}
		};
		payments.setAdapter(adapterp);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void filldata3(View v, int position) {
		// TODO Auto-generated method stub
		// db.checki();
		/*
		 * if (Index.sync.isChecked()) { nameValuePairs = new
		 * ArrayList<NameValuePair>(3); nameValuePairs.add(new
		 * BasicNameValuePair("Host", host.trim())); nameValuePairs.add(new
		 * BasicNameValuePair("Name", name.trim())); nameValuePairs.add(new
		 * BasicNameValuePair("Pass", pass.trim())); String url_select =
		 * globalVariable.getUrl() + "payments.php";
		 * 
		 * new MyAsyncTaskcr().execute(url_select, nameValuePairs, 3); }
		 */

		payments = (ListView) v.findViewById(R.id.listViewPayments);
		
		search = (EditText) v.findViewById(R.id.txtsearch);
		final String marketname;
		// final TextView text1;
		// final TextView text2;
		// populate the client list.
		viewpayments();

		// /implement a listener to filter the list view values
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO reset listview
				if (s.toString().equals("")) {
					// reset the listview
					viewpayments();
				} else {


					searchpaymentlist(s.toString());
				}

			}

		});
		
		
		/////////////////////////////////////////////////////////////////////////////////
		payments.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				viewpaymentdetails(parent, view, position, id);

			}

		});

	}

	public void getDateFromDatePicker(DatePicker datePicker, int year,
			int month, int day) {
		day = datePicker.getDayOfMonth();
		month = datePicker.getMonth();
		year = datePicker.getYear();

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);

		// return calendar.getTime();
	}
	public static boolean isNetworkAvailable(Context context) {
	    return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}
	
	@SuppressLint("InflateParams")
	private void sync(String datefrom,String dateto,String type) {
		// TODO Auto-generated method stub
		//Log.e("sync", "trueeee");
		//LayoutInflater inflater = this.getLayoutInflater();
		//View dialogView = inflater.inflate(R.layout.sync_menu, null);
		//adglobal = new AlertDialogGlobal(getActivity(), "Sync", dialogView,null, 1);
		
		AlertDialog adglobal = new AlertDialog.Builder(getActivity()).create();
		adglobal.setTitle("Alert");
		Log.e("the jsonarray", "one");


		String urls = null;
		ArrayList<NameValuePair>[] nameValuePairs = new ArrayList[3];

		urls = globalVariable.getUrl() + "payments.php";

		nameValuePairs[0] = new ArrayList<NameValuePair>(1);
		nameValuePairs[1] = new ArrayList<NameValuePair>(1);
		nameValuePairs[2] = new ArrayList<NameValuePair>(1);

		/*nameValuePairs[0].add(new BasicNameValuePair("AgentNo", agentno
				.trim()));

		nameValuePairs[1].add(new BasicNameValuePair("AgentNo", agentno
				.trim()));*/

		//if (cboxes[2].isChecked()) {
			Log.e("the jsonarray", "two");
			List<PaymentTag> tags = db.paymentreportlistupload(datefrom,dateto,type,globalVariable.getImei(),globalVariable.getAgentno());
			
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
		//}
		if (isNetworkAvailable(getActivity())) {
			
					Log.e("Checked", "true");
					new AsyncTaskActivity(getActivity()).execute(urls,nameValuePairs[2], 2 + 2,getActivity());
		
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
	}

	////loadsomes stuff
	public class loadSomeStuffs extends AsyncTask<String, Integer, String> {
		final ProgressDialog ringProgressDialog = ProgressDialog.show(
				getActivity(), "Please wait..", "Sending...", true);

		//String type = String;
		String urls = globalVariable.getUrl() + "get_synced_payments.php";
		String type;
		boolean internet = false;
		JSONArray jArray;
		String policy_no;
		String agent_no,client_name,amount,payment_date;
		String email;
		String telephone;
		boolean notempty = false;

		protected void onPreExecute() {
			// example of setting up something
			ringProgressDialog.setCancelable(true);
		}

		@Override
		protected String doInBackground(String... arg0) {
			String result = "";
			InputStream isr = null;
			//String phoneno = phoneno;
			if (isNetworkAvailable(getActivity())) {
				try {
					internet = true;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(urls);
					StringEntity Entity = null;
					//agent_no,date_from,date_to
					Entity = new StringEntity("agent_no=" + globalVariable.getAgentno() + "&date_from=" + datefromsonline + "&date_to=" + datetosonline);
					httppost.setHeader("Content-type",
							"application/x-www-form-urlencoded; charset=ASCII");
					httppost.setEntity(Entity);

					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					isr = entity.getContent();
				} catch (Exception e) {
					Log.e("log_tag", "Error in http connection " + e.toString());
				}
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(isr, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					isr.close();
					result = sb.toString();// string holding the json data.

				} catch (Exception e) {
					Log.e("log_tag",
							"Error in http connections " + e.toString());
				}

				try {

					jArray = new JSONArray(result);
					JSONObject cred = new JSONObject();

					//initialize the string array
					if (jArray.length() >= 1) {
						//agent_nos,policy_nos,client_names,amounts,payment_dates
						searchresults = new String[jArray.length()];
						agent_nos = new String[jArray.length()];
						policy_nos = new String[jArray.length()];
						client_names = new String[jArray.length()];
						amounts = new String[jArray.length()];
						payment_dates = new String[jArray.length()];
						notempty = true;
					} else {

						notempty = false;
						//agent_nos,policy_nos,client_names,amounts,payment_dates
						searchresults = new String[1];
						agent_nos = new String[0];
						policy_nos = new String[0];
						client_names = new String[0];
						amounts = new String[0];
						payment_dates = new String[0];

					}

				} catch (Exception e) {
					// TODO handle exception
					Log.e("log.tag", "Error Parsing Data " + e.toString());
				}


			} else {
				internet = false;
			}

			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			//resultView.setText(result);
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ringProgressDialog.dismiss();
			searchpolno = "";


			if (internet == false) {

				//Create an exit pop up
				AlertDialog.Builder adglobals1 = new AlertDialog.Builder(getActivity());
				//adglobals.setTitle("Alert!");

				adglobals1.setMessage("Network Error");
				//adglobal.setIcon(R.drawable.tick);
				adglobals1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				// Showing Alert Message
				adglobals1.show();

			} else {

				//create and alert dialog to insert the policy number
				//Create an exit pop up
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Synced Payments");

				//adglobals1.setMessage("Network Error");
				//adglobal.setIcon(R.drawable.tick);

				LayoutInflater inflater = getActivity().getLayoutInflater();
				View dialogView = inflater.inflate(R.layout.payments, null);
				builder.setView(dialogView);
				// builder.setMessage("View Details");
				clients = (ListView) dialogView.findViewById(R.id.listViewPayments);
				EditText search = (EditText) dialogView.findViewById(R.id.txtsearch);
				search.setVisibility(View.GONE);

				//searchpolno = search.getText().toString();
				@SuppressWarnings({"unchecked", "rawtypes"})
				ArrayAdapter adapter = new ArrayAdapter(getActivity(),
						android.R.layout.simple_list_item_2, searchresults) {

					@SuppressWarnings("deprecation")
					@SuppressLint("InflateParams")
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						TwoLineListItem row;
						if (convertView == null) {
							LayoutInflater inflater = (LayoutInflater) getActivity()
									.getApplicationContext().getSystemService(
											Context.LAYOUT_INFLATER_SERVICE);
							row = (TwoLineListItem) inflater.inflate(
									android.R.layout.simple_list_item_2, null);
						} else {
							row = (TwoLineListItem) convertView;
						}

						try {
							//jArray = new JSONArray(result);
							//JSONObject cred = new JSONObject();
							//initialize the string array
							//searchresults = new String[3];
							if (notempty == true) {
								JSONObject json = jArray.getJSONObject(position);

								// make it display only what you wanna see
								////agent_no,policy_no,client_name,amount,payment_date
								//agent_nos,policy_nos,client_names,amounts,payment_dates
								policy_no = json.getString("policy_no");
								agent_no = json.getString("agent_no");
								client_name = json.getString("client_name");
								amount = json.getString("amount");
								payment_date = json.getString("payment_date");
								//notempty = false;
								//pass the variables to the array
								policy_nos[position] = policy_no;
								client_names[position] = client_name;
								amounts[position] = amount;
								payment_dates[position] = payment_date;
								agent_nos[position] = agent_no;

							} else {
								policy_no = "No Record Found";
								client_name = "";
								agent_no = "";
								payment_date = "";
							}
						} catch (Exception e) {
							// TODO handle exception
							Log.e("log.tag", "Error Parsing Data " + e.toString());
						}

						row.getText1().setText(policy_no + " - " + client_name);
						row.getText2().setText(amount);
						row.getText1().setTextColor(Color.parseColor("#545454"));
						row.getText2().setTextColor(Color.parseColor("#545454"));

						return row;
					}
				};
				clients.setAdapter(adapter);

				////////////////////////////////////////////////////////////////////////////////////////
				clients.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(
							AdapterView<?> parent,
							View view, int position, long id) {

						viewpaymentdetailsonline(parent, view, position, id, jArray);

					}
				});
				////////////////////////////////////////////////////////////////////////////////////////


				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						//call the list view


					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				// Showing Alert Message
				builder.show();
			}
		}


	}
	////end of loadsome stuff

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void filldata4(View v, int position) {
		// TODO Auto-generated method stub

		payments = (ListView) v.findViewById(R.id.listViewReports);
		//"Payments Already Synchronized",
		final String[] menus = new String[] { "Payments By Date",
				"Payments By Date Range", "Payments By Client",
				"Payment By Market", "Summary By Day", "Summary By Date Range",
				"Daily Summaries" };

		payments.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, menus));

		payments.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// Log.e("item ID", "" + item.getTitle());
				// if (item.getTitle().equals("Payments By Date")) {}

				if (menus[position] == "Payments Already Synchronized") {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Set Date Range");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.reportdate,
							null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					final DatePicker dateto = (DatePicker) dialogView
							.findViewById(R.id.dateto);
					TextView txtdateto = (TextView) dialogView
							.findViewById(R.id.textto);
					final DatePicker datefrom = (DatePicker) dialogView
							.findViewById(R.id.datefrom);
					TextView txtdatefrom = (TextView) dialogView
							.findViewById(R.id.textfrom);

					dateto.setVisibility(View.VISIBLE);
					txtdateto.setVisibility(View.VISIBLE);
					datefrom.setVisibility(View.VISIBLE);
					txtdatefrom.setVisibility(View.VISIBLE);

					// Show the View Payments Dialog.
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int id) {
									// User clicked ok button
									datefromsonline = String
											.valueOf(datefrom.getYear())
											+ "-"
											+ String.valueOf(datefrom
											.getMonth() + 1)
											+ "-"
											+ String.valueOf(datefrom
											.getDayOfMonth());
									datetosonline = String
											.valueOf(dateto.getYear())
											+ "-"
											+ String.valueOf(dateto
											.getMonth() + 1)
											+ "-"
											+ String.valueOf(dateto
											.getDayOfMonth());
									// /////////////////////////////////////////////////////////////////////////////////
									loadSomeStuffs load = new loadSomeStuffs();
									load.execute();
									// ////////////////////////////////////////////////////////////////////////////////

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog, int id) {
									// User clicked cancel
									// button

								}
							});
					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();

				}
				else if (menus[position] == "Payments By Date") {
					// call the criteria dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Set Date");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.reportdate,
							null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					final DatePicker datefrom = (DatePicker) dialogView
							.findViewById(R.id.datefrom);

					datefrom.setVisibility(View.VISIBLE);

					final DatePicker dateto = (DatePicker) dialogView
							.findViewById(R.id.dateto);

					// Call the View Payments Dialog
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK button
									// ////////////////////////////////////////////////////////////////////////////////////////
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									

									LayoutInflater inflater = getActivity()
											.getLayoutInflater();
									final View dialogView = inflater.inflate(
											R.layout.payments, null);
									builder.setView(dialogView);

									payments = (ListView) dialogView
											.findViewById(R.id.listViewPayments);

									final String type = "Payments By Date";
									// datefrom.get

									final String datefroms = String
											.valueOf(datefrom.getYear())
											+ "-"
											+ String.valueOf(datefrom
													.getMonth() + 1)
											+ "-"
											+ String.valueOf(datefrom
													.getDayOfMonth());
									final String dates =String.valueOf(datefrom.getDayOfMonth())+ "-"+ String.valueOf(datefrom.getMonth() + 1)+ "-"+String.valueOf(+datefrom.getYear());
									builder.setTitle("Payments of date: "+dates);
									search = (EditText) dialogView.findViewById(R.id.txtsearch);
									search.setVisibility(View.GONE);

									ArrayAdapter adapterp = new ArrayAdapter(
											getActivity(),
											android.R.layout.simple_list_item_2,
											db.reportpaymenttaginfo(datefroms,
													datefroms, type, "0", "")) {
										@SuppressLint("InflateParams")
										@Override
										public View getView(int position,
												View convertView,
												ViewGroup parent) {
											TwoLineListItem row;
											if (convertView == null) {
												LayoutInflater inflater = (LayoutInflater) getActivity()
														.getApplicationContext()
														.getSystemService(
																Context.LAYOUT_INFLATER_SERVICE);
												row = (TwoLineListItem) inflater
														.inflate(
																android.R.layout.simple_list_item_2,
																null);
											} else {
												row = (TwoLineListItem) convertView;
											}
											PaymentTag data = db.reportpaymenttaginfo(datefroms, datefroms, type,
													"0", "").get(
															position);

											row.getText1().setText(
													data.getPolicy_no());
											row.getText2().setText(
													data.getAmount());
											row.getText1()
													.setTextColor(
															Color.parseColor("#545454"));
											row.getText2().setTextColor(Color.parseColor("#545454"));

											return row;
										}
									};

									payments.setAdapter(adapterp);

									payments.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											viewpaymentdetails(parent, view, position, id);

										}

									});

									// Add the buttons
									builder.setPositiveButton(
											"Sync",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int id) {
													// User clicked sync button
													// /display the value of the
													// textbox
													sync(datefroms,datefroms,type);
													/*
													 * Toast.makeText(getActivity
													 * (),testtext,
													 * Toast.LENGTH_LONG
													 * ).show();
													 */

												}
											});
									builder.setNegativeButton("Cancel",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog, int id) {
													// User clicked cancel
													// button

												}
											});

									// Create the AlertDialog
									AlertDialog dialog1 = builder.create();
									dialog1.show();
								}
								// ///////////////////////////////////////////////////////////////////////
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked cancel button

								}
							});

					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();
				} else if (menus[position] == "Payments By Date Range") {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Set Date Range");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.reportdate,
							null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					final DatePicker dateto = (DatePicker) dialogView
							.findViewById(R.id.dateto);
					TextView txtdateto = (TextView) dialogView
							.findViewById(R.id.textto);
					final DatePicker datefrom = (DatePicker) dialogView
							.findViewById(R.id.datefrom);
					TextView txtdatefrom = (TextView) dialogView
							.findViewById(R.id.textfrom);

					dateto.setVisibility(View.VISIBLE);
					txtdateto.setVisibility(View.VISIBLE);
					datefrom.setVisibility(View.VISIBLE);
					txtdatefrom.setVisibility(View.VISIBLE);

					// Show the View Payments Dialog.
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked ok button
									// /////////////////////////////////////////////////////////////////////////////////
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									

									LayoutInflater inflater = getActivity()
											.getLayoutInflater();
									final View dialogView = inflater.inflate(
											R.layout.payments, null);
									builder.setView(dialogView);

									payments = (ListView) dialogView
											.findViewById(R.id.listViewPayments);

									final String type = "Payments By Date Range";
									// datefrom.get

									final String datefroms = String
											.valueOf(datefrom.getYear())
											+ "-"
											+ String.valueOf(datefrom
													.getMonth() + 1)
											+ "-"
											+ String.valueOf(datefrom
													.getDayOfMonth());
									final String datetos = String
											.valueOf(dateto.getYear())
											+ "-"
											+ String.valueOf(dateto.getMonth()+ 1)
											+ "-"
											+ String.valueOf(dateto
													.getDayOfMonth());
									final String datesfrom =String.valueOf(datefrom.getDayOfMonth())+ "-"+ String.valueOf(datefrom.getMonth() + 1)+ "-"+String.valueOf(+datefrom.getYear());
									final String datesto =String.valueOf(dateto.getDayOfMonth())+ "-"+ String.valueOf(dateto.getMonth() + 1)+ "-"+String.valueOf(+dateto.getYear());
									// long dates = Date.parse(datefroms);
									search = (EditText) dialogView.findViewById(R.id.txtsearch);
									search.setVisibility(View.GONE);
									builder.setTitle("Payments made between: "+datesfrom+" to "+datesto);

									ArrayAdapter adapterp = new ArrayAdapter(
											getActivity(),
											android.R.layout.simple_list_item_2,
											db.reportpaymenttaginfo(datefroms,
													datetos, type, "0", "")) {
										@SuppressLint("InflateParams")
										@Override
										public View getView(int position,
												View convertView,
												ViewGroup parent) {
											TwoLineListItem row;
											if (convertView == null) {
												LayoutInflater inflater = (LayoutInflater) getActivity()
														.getApplicationContext()
														.getSystemService(
																Context.LAYOUT_INFLATER_SERVICE);
												row = (TwoLineListItem) inflater
														.inflate(
																android.R.layout.simple_list_item_2,
																null);
											} else {
												row = (TwoLineListItem) convertView;
											}
											PaymentTag data = db
													.reportpaymenttaginfo(
															datefroms, datetos,
															type, "0", "").get(
															position);

											row.getText1().setText(
													data.getPolicy_no());
											row.getText2().setText(
													data.getAmount());
											row.getText1()
													.setTextColor(
															Color.parseColor("#545454"));
											row.getText2().setTextColor(Color.parseColor("#545454"));

											return row;
										}
									};

									payments.setAdapter(adapterp);

									payments.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											viewpaymentdetails(parent, view,
													position, id);

										}

									});

									// Add the buttons
									builder.setPositiveButton(
											"Sync",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int id) {
													// User clicked sync button
													// /display the value of the
													// textbox
													sync(datefroms,datetos,type);
													/*
													 * Toast.makeText(getActivity
													 * (),testtext,
													 * Toast.LENGTH_LONG
													 * ).show();
													 */

												}
											});
									builder.setNegativeButton("Cancel",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog, int id) {
													// User clicked cancel
													// button

												}
											});

									// Create the AlertDialog
									AlertDialog dialog1 = builder.create();
									dialog1.show();
									// ////////////////////////////////////////////////////////////////////////////////

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog, int id) {
									// User clicked cancel
									// button

								}
							});
					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();

				} else if (menus[position] == "Payments By Client") {
					// First display the clients dialog with the search at the
					// top
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Select Client");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.clients, null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					clients = (ListView) dialogView
							.findViewById(R.id.listViewClients);
					search = (EditText) dialogView.findViewById(R.id.txtsearch);
					final String clientname;
					// final TextView text1;
					// final TextView text2;
					// populate the client list.
					viewclientlist();

					// /implement a listener to filter the list view values
					search.addTextChangedListener(new TextWatcher() {

						@Override
						public void afterTextChanged(Editable s) {
							// TODO Auto-generated method stub

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							// TODO reset listview
							if (s.toString().equals("")) {
								// reset the listview
								viewclientlist();
							} else {
								// perform search
								// searchclient(s.toString());
								viewsearchclientlist(s.toString());
							}

						}

					});

					// add an onclick listener and capture the value of the
					clients.setOnItemClickListener(new OnItemClickListener() {

						@Override
						@SuppressLint("InflateParams")
						public void onItemClick(AdapterView<?> parent,
												View view, int position, long id) {
							// TODO Auto-generated method stub

							TwoLineListItem list_item = (TwoLineListItem) view;
							TextView text1 = list_item.getText1();
							TextView text2 = list_item.getText2();
							// clientname = text2.getText().toString();
							final String cliPolicyNo = text1.getText()
									.toString();
							// create the pop up dialog to select criteria of
							// report.
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							final String clientname = text2.getText()
									.toString();
							builder.setTitle("Set Date Range & Report Type\n For "
									+ clientname);

							LayoutInflater inflater = getActivity()
									.getLayoutInflater();
							View dialogView = inflater.inflate(
									R.layout.reportdate, null);
							builder.setView(dialogView);
							// builder.setMessage("View Details");
							final DatePicker dateto = (DatePicker) dialogView
									.findViewById(R.id.dateto);
							TextView txtdateto = (TextView) dialogView
									.findViewById(R.id.textto);
							final DatePicker datefrom = (DatePicker) dialogView
									.findViewById(R.id.datefrom);
							TextView txtdatefrom = (TextView) dialogView
									.findViewById(R.id.textfrom);
							TextView txtreporttype = (TextView) dialogView
									.findViewById(R.id.textreporttype);
							final RadioButton rbtndetail = (RadioButton) dialogView
									.findViewById(R.id.rbtndetailed);
							final RadioButton rbtnsummary = (RadioButton) dialogView
									.findViewById(R.id.rbtnsummary);

							dateto.setVisibility(View.VISIBLE);
							txtdateto.setVisibility(View.VISIBLE);
							datefrom.setVisibility(View.VISIBLE);
							txtdatefrom.setVisibility(View.VISIBLE);
							txtreporttype.setVisibility(View.VISIBLE);
							rbtndetail.setVisibility(View.VISIBLE);
							rbtnsummary.setVisibility(View.VISIBLE);


							// retreive the clinets name
							// final String clientname =
							// text2.getText().toString();
							// txtclient.setText(clientname);

							// Add the buttons
							builder.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// User clicked ok button
											final String subtype;
											if (rbtndetail.isChecked()) {
												subtype = "1";
											} else if (rbtnsummary.isChecked()) {
												subtype = "2";
											} else {
												subtype = "0";
											}

											if (subtype == "0") {
												AlertDialog.Builder adglobals = new AlertDialog.Builder(
														getActivity());
												adglobals.setTitle("Alert!");

												adglobals.setMessage("Please Select Report type");
												//adglobals.setIcon(R.drawable.delete);


												adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														// Write your code here to execute after dialog closed
														//finish();
													}
												});
												// Showing Alert Message
												adglobals.show();
											} else {
												// display the respective payments
												// listview
												// ///////////////////////////////////////////////////////////////////////////

												AlertDialog.Builder builder = new AlertDialog.Builder(
														getActivity());


												LayoutInflater inflater = getActivity()
														.getLayoutInflater();
												View dialogView = inflater.inflate(
														R.layout.payments, null);
												builder.setView(dialogView);


												payments = (ListView) dialogView
														.findViewById(R.id.listViewPayments);

												final String type = "Payments By Client";
												// datefrom.get

												final String datefroms = String
														.valueOf(datefrom.getYear())
														+ "-"
														+ String.valueOf(datefrom
														.getMonth() + 1)
														+ "-"
														+ String.valueOf(datefrom
														.getDayOfMonth());
												final String datetos = String
														.valueOf(dateto.getYear())
														+ "-"
														+ String.valueOf(dateto
														.getMonth() + 1)
														+ "-"
														+ String.valueOf(dateto
														.getDayOfMonth());
												final String datesfrom = String.valueOf(datefrom.getDayOfMonth()) + "-" + String.valueOf(datefrom.getMonth() + 1) + "-" + String.valueOf(+datefrom.getYear());
												final String datesto = String.valueOf(dateto.getDayOfMonth()) + "-" + String.valueOf(dateto.getMonth() + 1) + "-" + String.valueOf(+dateto.getYear());
												builder.setTitle(clientname + "\n" + datesfrom + " to: " + datesto);
												// long dates =
												// Date.parse(datefroms);
												search = (EditText) dialogView.findViewById(R.id.txtsearch);
												search.setVisibility(View.GONE);

												ArrayAdapter adapterp = new ArrayAdapter(
														getActivity(),
														android.R.layout.simple_list_item_2,
														db.reportpaymenttaginfo(datefroms, datetos, type, subtype, clientname)) {
													@SuppressLint("InflateParams")
													@Override
													public View getView(
															int position,
															View convertView,
															ViewGroup parent) {
														TwoLineListItem row;
														if (convertView == null) {
															LayoutInflater inflater = (LayoutInflater) getActivity()
																	.getApplicationContext()
																	.getSystemService(
																			Context.LAYOUT_INFLATER_SERVICE);
															row = (TwoLineListItem) inflater
																	.inflate(
																			android.R.layout.simple_list_item_2,
																			null);
														} else {
															row = (TwoLineListItem) convertView;
														}
														PaymentTag data = db.reportpaymenttaginfo(datefroms, datetos, type, subtype, clientname)
																.get(position);

														row.getText1().setText(data.getPolicy_no());
														row.getText2().setText(data.getAmount());
														row.getText1().setTextColor(Color.parseColor("#545454"));
														row.getText2().setTextColor(Color.parseColor("#545454"));

														return row;
													}
												};

												payments.setAdapter(adapterp);

												payments.setOnItemClickListener(new OnItemClickListener() {

													@Override
													public void onItemClick(
															AdapterView<?> parent,
															View view,
															int position, long id) {

														viewpaymentdetails(parent,
																view, position, id);

													}

												});

												// Add the buttons
												builder.setPositiveButton(
														"Ok",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialog,
																	int id) {
																// User clicked sync
																// button
																// /display the
																// value of the
																// textbox
															/*
															 * Toast.makeText(
															 * getActivity
															 * (),testtext,
															 * Toast.LENGTH_LONG
															 * ).show();
															 */

															}
														});

												// Create the AlertDialog
												AlertDialog dialog1 = builder
														.create();
												dialog1.show();

												// //////////////////////////////////////////////////////////////////////////
											}
										}
									});
							builder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// User clicked cancel
											// button

										}
									});
							// Create the AlertDialog
							AlertDialog dialog1 = builder.create();
							dialog1.show();
						}
					});

					// Add the buttons
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// TODO display the reportdata layout to add

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked cancel button

								}
							});
					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();

				} else if (menus[position] == "Payment By Market") {
					
					// First display the clients dialog with the search at the
					// top
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Select Market");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.markets, null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					markets = (ListView) dialogView.findViewById(R.id.listViewMarkets);
					search = (EditText) dialogView.findViewById(R.id.txtsearch);
					final String marketname;
					// final TextView text1;
					// final TextView text2;
					// populate the client list.
					viewmarketlist();

					// /implement a listener to filter the list view values
					search.addTextChangedListener(new TextWatcher() {

						@Override
						public void afterTextChanged(Editable s) {
							// TODO Auto-generated method stub

						}

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onTextChanged(CharSequence s, int start, int before,
								int count) {
							// TODO reset listview
							if (s.toString().equals("")) {
								// reset the listview
								viewmarketlist();
							} else {


								viewmarketclientlist(s.toString());
							}

						}

					});

					markets.setOnItemClickListener(new OnItemClickListener() {


						@Override
						@SuppressLint("InflateParams")
						public void onItemClick(AdapterView<?> parent, View view,
												int position, long id) {
							// TODO Auto-generated method stub

							// create the pop up dialog to select criteria of
							// report.
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							//final String marketname = text1.getText()
							//.toString();
							final String marketname = (String) marketlists[position];
							builder.setTitle("Set Date Range & Report Type\n For "
									+ marketname);

							LayoutInflater inflater = getActivity()
									.getLayoutInflater();
							View dialogView = inflater.inflate(
									R.layout.reportdate, null);
							builder.setView(dialogView);
							// builder.setMessage("View Details");
							final DatePicker dateto = (DatePicker) dialogView
									.findViewById(R.id.dateto);
							TextView txtdateto = (TextView) dialogView
									.findViewById(R.id.textto);
							final DatePicker datefrom = (DatePicker) dialogView
									.findViewById(R.id.datefrom);
							TextView txtdatefrom = (TextView) dialogView
									.findViewById(R.id.textfrom);
							TextView txtreporttype = (TextView) dialogView
									.findViewById(R.id.textreporttype);
							final RadioButton rbtndetail = (RadioButton) dialogView
									.findViewById(R.id.rbtndetailed);
							final RadioButton rbtnsummary = (RadioButton) dialogView
									.findViewById(R.id.rbtnsummary);

							dateto.setVisibility(View.VISIBLE);
							txtdateto.setVisibility(View.VISIBLE);
							datefrom.setVisibility(View.VISIBLE);
							txtdatefrom.setVisibility(View.VISIBLE);
							txtreporttype.setVisibility(View.VISIBLE);
							rbtndetail.setVisibility(View.VISIBLE);
							rbtnsummary.setVisibility(View.VISIBLE);

							// Add the buttons
							builder.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											final String subtype;
											if (rbtndetail.isChecked()) {
												subtype = "1";
											} else if (rbtnsummary.isChecked()) {
												subtype = "2";
											} else {
												subtype = "0";
											}

											if (subtype == "0") {
												///if o the pop an alert dialog to select report type.
												AlertDialog.Builder adglobals = new AlertDialog.Builder(
														getActivity());
												adglobals.setTitle("Alert!");

												adglobals.setMessage("Please Select Report type");
												//adglobals.setIcon(R.drawable.delete);


												adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														// Write your code here to execute after dialog closed
														//finish();
													}
												});


												// Showing Alert Message
												adglobals.show();
											} else {

												// User clicked ok button
												// display the respective payments
												// listview
												// ///////////////////////////////////////////////////////////////////////////

												AlertDialog.Builder builder = new AlertDialog.Builder(
														getActivity());


												LayoutInflater inflater = getActivity()
														.getLayoutInflater();
												View dialogView = inflater.inflate(
														R.layout.payments, null);
												builder.setView(dialogView);

												payments = (ListView) dialogView
														.findViewById(R.id.listViewPayments);

												final String type = "Payment By Market";
												// datefrom.get

												final String datefroms = String
														.valueOf(datefrom.getYear())
														+ "-"
														+ String.valueOf(datefrom
														.getMonth() + 1)
														+ "-"
														+ String.valueOf(datefrom
														.getDayOfMonth());
												final String datetos = String
														.valueOf(dateto.getYear())
														+ "-"
														+ String.valueOf(dateto
														.getMonth() + 1)
														+ "-"
														+ String.valueOf(dateto
														.getDayOfMonth());
												final String datesfrom = String.valueOf(datefrom.getDayOfMonth()) + "-" + String.valueOf(datefrom.getMonth() + 1) + "-" + String.valueOf(+datefrom.getYear());
												final String datesto = String.valueOf(dateto.getDayOfMonth()) + "-" + String.valueOf(dateto.getMonth() + 1) + "-" + String.valueOf(+dateto.getYear());
												builder.setTitle(marketname + " \n " + datesfrom + " to " + datesto);
												search = (EditText) dialogView.findViewById(R.id.txtsearch);
												search.setVisibility(View.GONE);

												ArrayAdapter adapterp = new ArrayAdapter(
														getActivity(),
														android.R.layout.simple_list_item_2,
														db.reportpaymenttaginfo(
																datefroms, datetos,
																type, subtype,
																marketname)) {
													@SuppressLint("InflateParams")
													@Override
													public View getView(
															int position,
															View convertView,
															ViewGroup parent) {
														TwoLineListItem row;
														if (convertView == null) {
															LayoutInflater inflater = (LayoutInflater) getActivity()
																	.getApplicationContext()
																	.getSystemService(
																			Context.LAYOUT_INFLATER_SERVICE);
															row = (TwoLineListItem) inflater
																	.inflate(
																			android.R.layout.simple_list_item_2,
																			null);
														} else {
															row = (TwoLineListItem) convertView;
														}
														PaymentTag data = db
																.reportpaymenttaginfo(
																		datefroms,
																		datetos,
																		type,
																		subtype,
																		marketname)
																.get(position);

														row.getText1().setText(data.getPolicy_no());
														row.getText2().setText(data.getAmount());
														row.getText1().setTextColor(Color.parseColor("#545454"));
														row.getText2().setTextColor(Color.parseColor("#545454"));

														return row;
													}
												};

												payments.setAdapter(adapterp);

												payments.setOnItemClickListener(new OnItemClickListener() {

													@Override
													public void onItemClick(
															AdapterView<?> parent,
															View view,
															int position, long id) {

														viewpaymentdetails(parent,
																view, position, id);

													}

												});

												// Add the buttons
												builder.setPositiveButton(
														"Ok",
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialog,
																	int id) {
																// User clicked sync
																// button
																// /display the
																// value of the
																// textbox
															/*
															 * Toast.makeText(
															 * getActivity
															 * (),testtext,
															 * Toast.LENGTH_LONG
															 * ).show();
															 */

															}
														});

												// Create the AlertDialog
												AlertDialog dialog1 = builder
														.create();
												dialog1.show();

												// //////////////////////////////////////////////////////////////////////////
											}
										}
									});
							builder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// User clicked cancel
											// button

										}
									});
							// Create the AlertDialog
							AlertDialog dialog1 = builder.create();
							dialog1.show();
						}
					});

					// Add the buttons
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// TODO display the reportdata layout to add

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked cancel button

								}
							});
					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();

				} else if (menus[position] == "Summary By Day") {
					// call the criteria dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Set Date");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.reportdate,
							null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					final DatePicker datefrom = (DatePicker) dialogView
							.findViewById(R.id.datefrom);

					datefrom.setVisibility(View.VISIBLE);

					final DatePicker dateto = (DatePicker) dialogView
							.findViewById(R.id.dateto);

					// Call the View Payments Dialog
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK button
									// ////////////////////////////////////////////////////////////////////////////////////////
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									

									LayoutInflater inflater = getActivity()
											.getLayoutInflater();
									View dialogView = inflater.inflate(
											R.layout.payments, null);
									builder.setView(dialogView);

									payments = (ListView) dialogView.findViewById(R.id.listViewPayments);

									final String type = "Summary By Day";
									// datefrom.get

									final String datefroms = String
											.valueOf(datefrom.getYear())
											+ "-"
											+ String.valueOf(datefrom
													.getMonth() + 1)
											+ "-"
											+ String.valueOf(datefrom
													.getDayOfMonth());
									
									final String datesfrom =String.valueOf(datefrom.getDayOfMonth())+ "-"+ String.valueOf(datefrom.getMonth() + 1)+ "-"+String.valueOf(+datefrom.getYear());
									//final String datesto =String.valueOf(dateto.getDayOfMonth())+ "-"+ String.valueOf(dateto.getMonth() + 1)+ "-"+String.valueOf(+dateto.getYear());
									search = (EditText) dialogView.findViewById(R.id.txtsearch);
									search.setVisibility(View.GONE);
									//set the title of the dialog
									builder.setTitle("Payment Summary of date : " + datesfrom);
									
									ArrayAdapter adapterp = new ArrayAdapter(
											getActivity(),
											android.R.layout.simple_list_item_2,
											db.reportpaymenttaginfo(datefroms,
													datefroms, type, "2", "")) {
										@SuppressLint("InflateParams")
										@Override
										public View getView(int position,
												View convertView,
												ViewGroup parent) {
											TwoLineListItem row;
											if (convertView == null) {
												LayoutInflater inflater = (LayoutInflater) getActivity()
														.getApplicationContext()
														.getSystemService(
																Context.LAYOUT_INFLATER_SERVICE);
												row = (TwoLineListItem) inflater
														.inflate(
																android.R.layout.simple_list_item_2,
																null);
											} else {
												row = (TwoLineListItem) convertView;
											}
											PaymentTag data = db
													.reportpaymenttaginfo(
															datefroms,
															datefroms, type,
															"2", "").get(
															position);

											row.getText1().setText(
													data.getPolicy_no());
											row.getText2().setText(
													data.getAmount());
											row.getText1()
													.setTextColor(
															Color.parseColor("#545454"));
											row.getText2().setTextColor(Color.parseColor("#545454"));

											return row;
										}
									};

									payments.setAdapter(adapterp);

									payments.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											viewpaymentdetails(parent, view,
													position, id);

										}

									});

									// Add the buttons
									builder.setPositiveButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int id) {
													// User clicked sync button
													// /display the value of the
													// textbox
													/*
													 * Toast.makeText(getActivity
													 * (),testtext,
													 * Toast.LENGTH_LONG
													 * ).show();
													 */

												}
											});

									// Create the AlertDialog
									AlertDialog dialog1 = builder.create();
									dialog1.show();
								}
								// ///////////////////////////////////////////////////////////////////////
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked cancel button

								}
							});

					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();
				} else if (menus[position] == "Summary By Date Range") {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Set Date Range");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.reportdate,
							null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					final DatePicker dateto = (DatePicker) dialogView
							.findViewById(R.id.dateto);
					TextView txtdateto = (TextView) dialogView
							.findViewById(R.id.textto);
					final DatePicker datefrom = (DatePicker) dialogView
							.findViewById(R.id.datefrom);
					TextView txtdatefrom = (TextView) dialogView
							.findViewById(R.id.textfrom);

					dateto.setVisibility(View.VISIBLE);
					txtdateto.setVisibility(View.VISIBLE);
					datefrom.setVisibility(View.VISIBLE);
					txtdatefrom.setVisibility(View.VISIBLE);

					// Show the View Payments Dialog.
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked ok button
									// /////////////////////////////////////////////////////////////////////////////////
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle("View Payments");

									LayoutInflater inflater = getActivity()
											.getLayoutInflater();
									View dialogView = inflater.inflate(
											R.layout.payments, null);
									builder.setView(dialogView);

									payments = (ListView) dialogView
											.findViewById(R.id.listViewPayments);

									final String type = "Summary By Date Range";
									// datefrom.get

									final String datefroms = String
											.valueOf(datefrom.getYear())
											+ "-"
											+ String.valueOf(datefrom
													.getMonth() + 1)
											+ "-"
											+ String.valueOf(datefrom
													.getDayOfMonth());
									final String datetos = String
											.valueOf(dateto.getYear())
											+ "-"
											+ String.valueOf(dateto.getMonth()+ 1)
											+ "-"
											+ String.valueOf(dateto
													.getDayOfMonth());
									final String datesfrom =String.valueOf(datefrom.getDayOfMonth())+ "-"+ String.valueOf(datefrom.getMonth() + 1)+ "-"+String.valueOf(+datefrom.getYear());
									final String datesto =String.valueOf(dateto.getDayOfMonth())+ "-"+ String.valueOf(dateto.getMonth() + 1)+ "-"+String.valueOf(+dateto.getYear());
									
									builder.setTitle("Payment Summary from : "+ datesfrom + " to : "+datesto);
									// long dates = Date.parse(datefroms);
									search = (EditText) dialogView.findViewById(R.id.txtsearch);
									search.setVisibility(View.GONE);
									
									ArrayAdapter adapterp = new ArrayAdapter(
											getActivity(),
											android.R.layout.simple_list_item_2,
											db.reportpaymenttaginfo(datefroms,
													datetos, type, "2", "")) {
										@SuppressLint("InflateParams")
										@Override
										public View getView(int position,
												View convertView,
												ViewGroup parent) {
											TwoLineListItem row;
											if (convertView == null) {
												LayoutInflater inflater = (LayoutInflater) getActivity()
														.getApplicationContext()
														.getSystemService(
																Context.LAYOUT_INFLATER_SERVICE);
												row = (TwoLineListItem) inflater
														.inflate(
																android.R.layout.simple_list_item_2,
																null);
											} else {
												row = (TwoLineListItem) convertView;
											}
											PaymentTag data = db
													.reportpaymenttaginfo(
															datefroms, datetos,
															type, "2", "").get(
															position);
											if(data.getPolicy_no()!=null){
											row.getText1().setText(
													data.getPolicy_no());
											row.getText2().setText(
													data.getAmount());
											row.getText1()
													.setTextColor(
															Color.parseColor("#545454"));
											}else{
												row.getText1().setText("No Payments Done");
												row.getText1().setTextColor(Color.parseColor("#545454"));
												row.getText2().setTextColor(Color.parseColor("#545454"));
											}

											return row;
										}
									};

									payments.setAdapter(adapterp);

									payments.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											viewpaymentdetails(parent, view,
													position, id);

										}

									});

									// Add the buttons
									builder.setPositiveButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int id) {
													// User clicked sync button
													// /display the value of the
													// textbox
													/*
													 * Toast.makeText(getActivity
													 * (),testtext,
													 * Toast.LENGTH_LONG
													 * ).show();
													 */

												}
											});

									// Create the AlertDialog
									AlertDialog dialog1 = builder.create();
									dialog1.show();
									// ////////////////////////////////////////////////////////////////////////////////

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog, int id) {
									// User clicked cancel
									// button

								}
							});
					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();
				} else if (menus[position] == "Daily Summaries") {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Set Date Range");

					LayoutInflater inflater = getActivity().getLayoutInflater();
					View dialogView = inflater.inflate(R.layout.reportdate,
							null);
					builder.setView(dialogView);
					// builder.setMessage("View Details");
					final DatePicker dateto = (DatePicker) dialogView
							.findViewById(R.id.dateto);
					TextView txtdateto = (TextView) dialogView
							.findViewById(R.id.textto);
					final DatePicker datefrom = (DatePicker) dialogView
							.findViewById(R.id.datefrom);
					TextView txtdatefrom = (TextView) dialogView
							.findViewById(R.id.textfrom);

					dateto.setVisibility(View.VISIBLE);
					txtdateto.setVisibility(View.VISIBLE);
					datefrom.setVisibility(View.VISIBLE);
					txtdatefrom.setVisibility(View.VISIBLE);

					// Show the View Payments Dialog.
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked ok button
									// /////////////////////////////////////////////////////////////////////////////////
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle("View Payments");

									LayoutInflater inflater = getActivity()
											.getLayoutInflater();
									View dialogView = inflater.inflate(
											R.layout.payments, null);
									builder.setView(dialogView);

									payments = (ListView) dialogView
											.findViewById(R.id.listViewPayments);

									final String type = "Daily Summaries";
									// datefrom.get

									final String datefroms = String
											.valueOf(datefrom.getYear())
											+ "-"
											+ String.valueOf(datefrom
													.getMonth() + 1)
											+ "-"
											+ String.valueOf(datefrom
													.getDayOfMonth());
									final String datetos = String
											.valueOf(dateto.getYear())
											+ "-"
											+ String.valueOf(dateto.getMonth()+ 1)
											+ "-"
											+ String.valueOf(dateto
													.getDayOfMonth());
									
									final String datesfrom =String.valueOf(datefrom.getDayOfMonth())+ "-"+ String.valueOf(datefrom.getMonth() + 1)+ "-"+String.valueOf(+datefrom.getYear());
									final String datesto =String.valueOf(dateto.getDayOfMonth())+ "-"+ String.valueOf(dateto.getMonth() + 1)+ "-"+String.valueOf(+dateto.getYear());
									
									builder.setTitle("Daily Summary \n"+ datesfrom + " to : "+datesto);
									// long dates = Date.parse(datefroms);
									search = (EditText) dialogView.findViewById(R.id.txtsearch);
									search.setVisibility(View.GONE);

									ArrayAdapter adapterp = new ArrayAdapter(
											getActivity(),
											android.R.layout.simple_list_item_2,
											db.reportpaymenttaginfo(datefroms,
													datetos, type, "2", "")) {
										@SuppressLint("InflateParams")
										@Override
										public View getView(int position,
												View convertView,
												ViewGroup parent) {
											TwoLineListItem row;
											if (convertView == null) {
												LayoutInflater inflater = (LayoutInflater) getActivity()
														.getApplicationContext()
														.getSystemService(
																Context.LAYOUT_INFLATER_SERVICE);
												row = (TwoLineListItem) inflater
														.inflate(
																android.R.layout.simple_list_item_2,
																null);
											} else {
												row = (TwoLineListItem) convertView;
											}
											PaymentTag data = db
													.reportpaymenttaginfo(
															datefroms, datetos,
															type, "2", "").get(
															position);

											row.getText1().setText(
													data.getPolicy_no());
											row.getText2().setText(
													data.getAmount());
											row.getText1()
													.setTextColor(
															Color.parseColor("#545454"));
											row.getText2().setTextColor(Color.parseColor("#545454"));

											return row;
										}
									};

									payments.setAdapter(adapterp);

									payments.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											viewpaymentdetails(parent, view,
													position, id);

										}

									});

									// Add the buttons
									builder.setPositiveButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int id) {
													// User clicked sync button
													// /display the value of the
													// textbox
													/*
													 * Toast.makeText(getActivity
													 * (),testtext,
													 * Toast.LENGTH_LONG
													 * ).show();
													 */

												}
											});

									// Create the AlertDialog
									AlertDialog dialog1 = builder.create();
									dialog1.show();
									// ////////////////////////////////////////////////////////////////////////////////

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog, int id) {
									// User clicked cancel
									// button

								}
							});
					// Create the AlertDialog
					AlertDialog dialog = builder.create();
					dialog.show();
				}

			}

		});

	}

	int clientsize = 0;
	Object[] clientlist = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void viewclientlist() {
		// clientsize = db.clientlistpolno().size();
		// clientlist = new String[clientsize];
		// clientlist = (String[])db.clientlistpolno().toArray();
		// display the client list
		//clientlist = db.clientlistpolno().toArray()
		clientsize = (int)db.clientlistpolno().size();
		clientlist = new Object[clientsize];

		ArrayAdapter adapter = new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_2, db.clientlistpolno()) {

			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TwoLineListItem row;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getApplicationContext().getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
					row = (TwoLineListItem) inflater.inflate(
							android.R.layout.simple_list_item_2, null);
				} else {
					row = (TwoLineListItem) convertView;
				}
				ClientTag data = db.clientlistpolno().get(position);
				row.getText1().setText(data.getPolicy_no());
				row.getText2().setText(data.getClient());
				row.getText1().setTextColor(Color.parseColor("#545454"));
				row.getText2().setTextColor(Color.parseColor("#545454"));
				clientlist[position] = data.getClient_no();
				
				
				if(data.getPolicy_no().equals("No Clients found. Please sync")){
					search.setVisibility(View.GONE);
				}

				return row;
			}
		};
		clients.setAdapter(adapter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void viewsearchclientlist(final String search) {
		// clientsize = db.clientlistpolno().size();
		// clientlist = new String[clientsize];
		// clientlist = (String[])db.clientlistpolno().toArray();
		// display the client list

		ArrayAdapter adapter = new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_2,
				db.searchclientlistpolno(search)) {

			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TwoLineListItem row;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getApplicationContext().getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
					row = (TwoLineListItem) inflater.inflate(
							android.R.layout.simple_list_item_2, null);
				} else {
					row = (TwoLineListItem) convertView;
				}
				ClientTag data = db.searchclientlistpolno(search).get(position);
				row.getText1().setText(data.getPolicy_no());
				row.getText2().setText(data.getClient());
				row.getText1().setTextColor(Color.parseColor("#545454"));
				row.getText2().setTextColor(Color.parseColor("#545454"));

				return row;
			}
		};
		clients.setAdapter(adapter);
	}

	/*@SuppressWarnings("unchecked")
	public void searchclient(String textToSearch) {

		for (String item : clientlist) {
			if (!item.contains(textToSearch)) {
				((List<com.softclans.agents.helper.ClientTag>) clients)
						.remove(item);
			}
		}

		// adapter.notifyDataSetChanged();

	}*/
	
	String smsclientno = null;
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void filldata2(View v, int position) {
		// TODO Auto-generated method stub
		/*
		 * db.checki(); if (Index.sync.isChecked()) { nameValuePairs = new
		 * ArrayList<NameValuePair>(3); nameValuePairs.add(new
		 * BasicNameValuePair("Host", host.trim())); nameValuePairs.add(new
		 * BasicNameValuePair("Name", name.trim())); nameValuePairs.add(new
		 * BasicNameValuePair("Pass", pass.trim())); String url_select =
		 * globalVariable.getUrl() + "clients.php"; new
		 * MyAsyncTaskcr().execute(url_select, nameValuePairs, 2); }
		 */

		clients = (ListView) v.findViewById(R.id.listViewClients);
		
		//View actionBarView = getActionBar().getCustomView();
		search = (EditText) v.findViewById(R.id.txtsearch);
		//getActionBar().setCustomView(search);
		// populate the client list.
		viewclientlist();

		// /implement a listener to filter the list view values
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO reset listview
				if (s.toString().equals("")) {
					// reset the listview
					viewclientlist();
				} else {
					// perform search
					// searchclient(s.toString());
					viewsearchclientlist(s.toString());
				}

			}

		});

		clients.setOnItemClickListener(new OnItemClickListener() {

			@Override
			@SuppressLint("InflateParams")
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				TwoLineListItem list_item = (TwoLineListItem) view;
				TextView text1 = list_item.getText1();
				TextView text2 = list_item.getText2();
				final String cliName = text2.getText().toString();
				final String cliPolicyNo = text1.getText().toString();
				String[] cdetails = db.clientDetails(cliPolicyNo);
				final String cliemail = cdetails[0];
				final String clitelephone = cdetails[1];
				
				String clientno = (String) clientlist[position];
				//ClientTag d = db.getclientnos(mid).get(position);
				//String clientno = d.getClient_no();
				final String[] clientnamepolno = new String[3];
				clientnamepolno[0] = cliName;
				clientnamepolno[1] = cliPolicyNo;
				clientnamepolno[2] = clientno;
				
				smsclientno = clientno;

				// Creating the instance of PopupMenu
				PopupMenu popup = new PopupMenu(getActivity(), view);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.poupup_menu,
						popup.getMenu());

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Log.e("item ID", "" + item.getTitle());
						if (item.getTitle().equals("View")) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setTitle("Client Details");

							LayoutInflater inflater = getActivity()
									.getLayoutInflater();
							View dialogView = inflater.inflate(
									R.layout.clientdetails, null);
							builder.setView(dialogView);
							// builder.setMessage("View Details");
							TextView cname = (TextView) dialogView
									.findViewById(R.id.textViewClientNamepopup);
							TextView cpolicyno = (TextView) dialogView
									.findViewById(R.id.TextViewPolicyNopopup);
							TextView cemail = (TextView) dialogView
									.findViewById(R.id.TextViewEmailpopup);
							TextView ctelephone = (TextView) dialogView
									.findViewById(R.id.TextViewTelephoneNopopup);
							cpolicyno.setText(cliPolicyNo);
							cname.setText(cliName);
							cemail.setText(cliemail);
							ctelephone.setText(clitelephone);

							// Add the buttons
							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// User clicked OK button

										}
									});
							builder.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// User cancelled the dialog
										}
									});

							// Create the AlertDialog
							AlertDialog dialog = builder.create();
							dialog.show();
						} else if (item.getTitle().equals("View Payments")) {
							// TODO Create a dialog to view payments done by
							// client to that proposal
							AlertDialog.Builder builder = new AlertDialog.Builder(
									getActivity());
							builder.setTitle("Payments of " + cliName);

							LayoutInflater inflater = getActivity()
									.getLayoutInflater();
							View dialogView = inflater.inflate(
									R.layout.payments, null);
							builder.setView(dialogView);

							// create an array to pass as a parameter
							final String[] parameters = new String[2];
							parameters[0] = cliName;
							parameters[1] = cliPolicyNo;
							payments = (ListView) dialogView
									.findViewById(R.id.listViewPayments);
							
							search = (EditText) dialogView.findViewById(R.id.txtsearch);
							search.setVisibility(View.GONE);

							ArrayAdapter adapterp = new ArrayAdapter(
									getActivity(),
									android.R.layout.simple_list_item_2, db
											.clientpaymenttaginfo(parameters)) {
								@SuppressLint("InflateParams")
								@Override
								public View getView(int position,
										View convertView, ViewGroup parent) {
									TwoLineListItem row;
									if (convertView == null) {
										LayoutInflater inflater = (LayoutInflater) getActivity()
												.getApplicationContext()
												.getSystemService(
														Context.LAYOUT_INFLATER_SERVICE);
										row = (TwoLineListItem) inflater
												.inflate(
														android.R.layout.simple_list_item_2,
														null);
									} else {
										row = (TwoLineListItem) convertView;
									}
									PaymentTag data = db.clientpaymenttaginfo(
											parameters).get(position);
									if (data.getPolicy_no().equals(
											"No Payments Done")) {
										row.getText1().setText(
												data.getPolicy_no());
										row.getText2()
												.setText(data.getAmount());
										row.getText1().setTextColor(
												Color.parseColor("#545454"));
									} else {
										row.getText1().setText(
												data.getPolicy_no());
										row.getText2()
												.setText(data.getAmount());
										row.getText1().setTextColor(
												Color.parseColor("#545454"));
									}
									return row;
								}
							};

							payments.setAdapter(adapterp);

							// Add the buttons
							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// User clicked sync button

										}
									});
							// Create the AlertDialog
							AlertDialog dialog1 = builder.create();
							dialog1.show();

						} else if (item.getTitle().equals("Create Payments")) {
							LayoutInflater inflater = getActivity()
									.getLayoutInflater();
							View dialogView = inflater.inflate(
									R.layout.paymentedit, null);
							//get
							//if markets markets are not saved then alert
							marketlists = db.marketlist().toArray();
							if(marketlists[0] == "No market found. Please Sync"){
								search.setVisibility(View.GONE);
							}
							if(marketlists[0] == "No market found. Please Sync"){
								//alert
								//Create an exit pop up
								AlertDialog.Builder adglobals1 = new AlertDialog.Builder(getActivity());
								//adglobals.setTitle("Alert!");
								
								adglobals1.setMessage("Please sync markets first in order to create a payment");
								//adglobal.setIcon(R.drawable.tick);
								
								
								adglobals1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								});		
								// Showing Alert Message
								adglobals1.show();
								
							}else{
								adglobal = new AlertDialogGlobal(getActivity(),
									"Payment", dialogView, clientnamepolno, 2,false);
							}
						} else if (item.getTitle().equals("Call")) {

							// TODO call get the phone number and make a call to
							// the phone number.

							try {
								// set the data

								// call directly
								String uri = "tel:" + clitelephone;
								if(!clitelephone.equals("")){
									Intent callIntent = new Intent(
											Intent.ACTION_CALL, Uri.parse(uri));
									startActivity(callIntent);
								}else{
									AlertDialog.Builder adglobals = new AlertDialog.Builder(getActivity());
									adglobals.setTitle("Your call has failed!");

									adglobals.setMessage("Client's phone number isn't in the records.");
									// adglobals.setIcon(R.drawable.delete);ge

									adglobals.setPositiveButton("Ok",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// Write your code here to
													// execute after dialog closed
													// finish();
												}
											});

									// Showing Alert Message
									adglobals.show();
								}

							} catch (Exception e) {
								// Alert that phone is not registered.
								// Create an exit pop up
								AlertDialog.Builder adglobals = new AlertDialog.Builder(getActivity());
								adglobals.setTitle("Your call has failed!");

								adglobals.setMessage("Client's phone number isn't in the records.");
								// adglobals.setIcon(R.drawable.delete);ge

								adglobals.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// Write your code here to
												// execute after dialog closed
												// finish();
											}
										});

								// Showing Alert Message
								adglobals.show();

								/*
								 * Toast.makeText(getActivity(),
								 * "Your call has failed...There is no phone no"
								 * ,
								 * 
								 * Toast.LENGTH_LONG).show();
								 */

								e.printStackTrace();

							}

							/*
							 * Toast.makeText(getActivity(), "You Clicked : " +
							 * item.getTitle(), Toast.LENGTH_SHORT).show();
							 */
						} else if (item.getTitle().equals("SMS Pay Info")) {
							
							// send an sms to the client
							sms = true;
							phoneno = clitelephone;
							loadSomeStuff load = new loadSomeStuff();
							load.execute();
							
							/*Toast.makeText(getActivity(),
									"You Clicked : " + item.getTitle(),
									Toast.LENGTH_SHORT).show();*/
									
						}
						return true;
					}
				});

				popup.show();// showing popup menu
			}

		});
	}

	private void filldata5(View v, int position) {
		// TODO Auto-generated method stub
		spinnerOccupation = (Spinner) v.findViewById(R.id.spinnerOccupation);
		spinnerPlans = (Spinner) v.findViewById(R.id.spinnerPlan);
		age = (EditText) v.findViewById(R.id.editTextClAge);
		dateofbirth = (EditText) v.findViewById(R.id.editTextClientDOB);
		email = (EditText) v.findViewById(R.id.editTextClientEmail);
		phoneNumber = (EditText) v.findViewById(R.id.editTextClientPhone);
		address = (EditText) v.findViewById(R.id.editTextClientAddress);
		clientname = (EditText) v.findViewById(R.id.editTextClientName);
		send = (Button) v.findViewById(R.id.buttonClientSend);

		age.setEnabled(false);
		age.setFocusable(false);
		setCurrentDateOnView();

		inflater = getActivity().getLayoutInflater();
		layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) v.findViewById(R.id.toast_layout_root));
		text = (TextView) layout.findViewById(R.id.text);

		List<String> categories = new ArrayList<String>();
		categories.add("CLASS A");
		categories.add("CLASS B");
		categories.add("CLASS C");
		categories.add("CLASS D");
		categories.add("CLASS E");

		dataAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, categories);

		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerOccupation.setAdapter(dataAdapter);
		spinnerOccupation.setOnItemSelectedListener(this);

		spinnerPlans.setOnItemSelectedListener(new PlanItemSelectedListener());

		db = new DatabaseHelper(getActivity().getApplicationContext());
		// db.checki();
		// if (Index.sync.isChecked()) {
		// Log.e(tag, "sync is checked");
		// addItemsOnPlan();
		// } else {
		Log.e("not", "sync is checked");
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				db.planlist());
		dataAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPlans.setAdapter(dataAdapter2);
		// }
		db.closeDB();

		dateofbirth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View vdd) {
				// TODO Auto-generated method stub
				// getActivity().showDialog(DATE_DIALOG_ID);
				showDatePickerDialog(vdd);
			}
		});

		dateofbirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View vddd, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					// getActivity().showDialog(DATE_DIALOG_ID);
					showDatePickerDialog(vddd);
				}
			}
		});

		address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			@SuppressLint("InflateParams")
			public void onFocusChange(View vddd, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					// get prompts.xml view
					LayoutInflater layoutInflater = LayoutInflater
							.from(getActivity());
					View promptView = layoutInflater.inflate(R.layout.address,
							null);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());

					// set prompts.xml to be the layout file of the alertdialog
					// builder
					alertDialogBuilder.setView(promptView);
					final EditText box = (EditText) promptView
							.findViewById(R.id.editTextAddressBox);
					final EditText code = (EditText) promptView
							.findViewById(R.id.editTextAddressCode);
					final EditText town = (EditText) promptView
							.findViewById(R.id.editTextAddressTown);
					final EditText country = (EditText) promptView
							.findViewById(R.id.editTextAddressCountry);

					// setup a dialog window
					alertDialogBuilder
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											// get user input and set it to
											// result

											if (box.getText().toString()
													.matches("")) {
												text.setText("Please use box Number");
												Toast toast = new Toast(
														getActivity()
																.getApplicationContext());
												toast.setGravity(
														Gravity.CENTER_VERTICAL,
														0, 0);
												toast.setDuration(Toast.LENGTH_LONG);
												toast.setView(layout);
												toast.show();
											} else if (town.getText()
													.toString().matches("")) {
												text.setText("Please fill the town field");
												Toast toast = new Toast(
														getActivity()
																.getApplicationContext());
												toast.setGravity(
														Gravity.CENTER_VERTICAL,
														0, 0);
												toast.setDuration(Toast.LENGTH_LONG);
												toast.setView(layout);
												toast.show();
											} else if (country.getText()
													.toString().matches("")) {
												text.setText("Please fill the country field");
												Toast toast = new Toast(
														getActivity()
																.getApplicationContext());
												toast.setGravity(
														Gravity.CENTER_VERTICAL,
														0, 0);
												toast.setDuration(Toast.LENGTH_LONG);
												toast.setView(layout);
												toast.show();
											} else {
												if (code.getText().toString()
														.matches("")) {
													String clAddress = "P.O. Box "
															+ box.getText()
																	.toString()
															+ ", "
															+ town.getText()
																	.toString()
															+ ", "
															+ country.getText()
																	.toString();
													address.setText(clAddress);
												} else {
													String clAddress = "P.O. Box "
															+ box.getText()
																	.toString()
															+ "-"
															+ code.getText()
																	.toString()
															+ ", "
															+ town.getText()
																	.toString()
															+ ", "
															+ country.getText()
																	.toString();
													address.setText(clAddress);
												}
											}
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});

					// create an alert dialog
					AlertDialog alertD = alertDialogBuilder.create();

					alertD.show();
				}
			}
		});

		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				//

				String sUsername = clientname.getText().toString();
				if (sUsername.matches("")) {
					text.setText("Please Fill the client Name");

					Toast toast = new Toast(getActivity()
							.getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(layout);
					toast.show();
				} else if (isEmailValid(email.getText().toString()) == false) {
					email.setText("");

					text.setText("Please use a valid Email");

					Toast toast = new Toast(getActivity()
							.getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(layout);
					toast.show();

				} else if (isValidPhoneNumber(phoneNumber.getText().toString()) == false) {
					text.setText("Please use a valid Phone Number");

					Toast toast = new Toast(getActivity()
							.getApplicationContext());
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(layout);
					toast.show();
				} else {
					Intent myIntent = new Intent(getActivity(), Client2.class);
					getActivity().finish();
					startActivity(myIntent);
				}

			}
		});

	}
	Object[] marketlists;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void viewmarketlist() {
		markets.setAdapter(new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_1, db.marketlist()));
		marketlists = db.marketlist().toArray();
		if(marketlists[0] == "No market found. Please Sync"){
			search.setVisibility(View.GONE);
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void viewmarketclientlist(final String search) {
		// clientsize = db.clientlistpolno().size();
		// clientlist = new String[clientsize];
		// clientlist = (String[])db.clientlistpolno().toArray();
		// display the client list

		markets.setAdapter(new ArrayAdapter(getActivity(),
				android.R.layout.simple_list_item_1, db.searchmarketlist(search)));
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void filldata(View v, int position) {

		// searchmarketlist();

		// TODO Auto-generated method stub

		markets = (ListView) v.findViewById(R.id.listViewMarkets);
		search = (EditText) v.findViewById(R.id.txtsearch);
		/*
		 * if (Index.sync.isChecked()) { nameValuePairs = new
		 * ArrayList<NameValuePair>(3); nameValuePairs.add(new
		 * BasicNameValuePair("Host", host.trim())); nameValuePairs.add(new
		 * BasicNameValuePair("Name", name.trim())); nameValuePairs.add(new
		 * BasicNameValuePair("Pass", pass.trim())); String url_select =
		 * globalVariable.getUrl() + "markets.php";
		 * 
		 * try { new MyAsyncTaskc() .execute(url_select, nameValuePairs,
		 * position); Log.e("complete ", "MyAsyncTaskc"); } catch (Exception e)
		 * { Log.e("error", e.toString()); } }
		 */

		// view the market list.
		viewmarketlist();

		// /implement a listener to filter the list view values
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO reset listview
				if (s.toString().equals("")) {
					// reset the listview
					viewmarketlist();
				} else {


					viewmarketclientlist(s.toString());
				}

			}

		});

		/*markets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity().getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();
			}

		});*/
	}

	class MyAsyncTaskc extends AsyncTask<Object, String, Void> {

		private ProgressDialog progressDialog = new ProgressDialog(
				getActivity());
		InputStream inputStream = null;
		int position = 0;

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading...");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					MyAsyncTaskc.this.cancel(true);
				}
			});
		}

		@Override
		protected Void doInBackground(Object... params) {

			// Add your data

			String url = (String) params[0];
			@SuppressWarnings("unchecked")
			ArrayList<NameValuePair> namepairvalue = (ArrayList<NameValuePair>) params[1];
			position = (Integer) params[2];
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
				Log.e("Unsupported", e1.toString());
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
				}

				inputStream.close();
				result = sBuilder.toString();

			} catch (Exception e) {
				Log.e("StringBuilding",
						"Error converting result " + e.toString());
			}
			return null;
		} // protected Void doInBackground(String... params)

		@Override
		protected void onPostExecute(Void v) {
			
			try {
				//add an alert to show that sync for markets is complete.
				//Alert that phone is not registered.
				//Create an exit pop up
				AlertDialog.Builder adglobals = new AlertDialog.Builder(getActivity());
				adglobals.setTitle("Success!");				
				adglobals.setMessage("Payments successfully synchronized");
				//adglobal.setIcon(R.drawable.tick);				
				adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed		
						
						
					}
				});
				

				// Showing Alert Message
				adglobals.show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.progressDialog.dismiss();
		} // protected void onPostExecute(Void v)

	}

	public void marketToDb(String market_code, String market_name) {
		// TODO Auto-generated method stub
		Log.d("mkt todb", "Tag todb: " + market_name);
		MarketTag tag1 = new MarketTag(market_name, market_code);
		db.createMarketTag(tag1);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long id) {
		// TODO Auto-generated method stub
		paclass = "00" + (parent.getItemIdAtPosition(pos) + 1);
		paclassDesc = "" + parent.getItemAtPosition(pos);
		/*
		 * text.setText(parent.getItemAtPosition(pos) + " ID: " +
		 * parent.getItemIdAtPosition(pos));
		 * 
		 * Toast toast = new Toast(getApplicationContext());
		 * toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		 * toast.setDuration(Toast.LENGTH_LONG); toast.setView(layout);
		 * toast.show();
		 */
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	private void addItemsOnPlan() {
		/*
		 * TODO Auto-generated method stub Log.e("host",host);
		 * Log.e("name",name); Log.e("pass",pass);
		 * 
		 * 
		 * nameValuePairs = new ArrayList<NameValuePair>(3);
		 * nameValuePairs.add(new BasicNameValuePair("Host", host.trim()));
		 * nameValuePairs.add(new BasicNameValuePair("Name", name.trim()));
		 * nameValuePairs.add(new BasicNameValuePair("Pass", pass.trim()));
		 * String url_select = globalVariable.getUrl() + "plan.php";
		 * 
		 * new MyAsyncTaskcr().execute(url_select, nameValuePairs, 1);
		 */
	}

	class MyAsyncTaskcr extends AsyncTask<Object, String, Void> {

		private ProgressDialog progressDialog = new ProgressDialog(
				getActivity());
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
				Log.e("Unsupport", e1.toString());
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
				Log.e("Strin","Error converting result " + e.toString());
			}
			return null;
		} // protected Void doInBackground(String... params)

		@Override
		protected void onPostExecute(Void v) {
			if (n == 1) {
				try {
					Log.e("result", result);
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0, count = jsonArray.length(); i < count; i++) {
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// jsonObject.opt("Description");
							String description = jsonObject.opt("description")
									.toString();
							String plan_code = jsonObject.opt("plan_code")
									.toString();
							// stringArray.add(description);
							// stringArray.add(jsonObject.toString());
							// add INFO TO DATABASE
							Log.e("", plan_code);
							// db.checki();
							planToDb(description, plan_code);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_spinner_item, db.planlist());
					dataAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerPlans.setAdapter(dataAdapter);
					/**/
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (n == 2) {
				try {
					Log.e("result", result);
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0, count = jsonArray.length(); i < count; i++) {
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// jsonObject.opt("Description");
							String policy_no = jsonObject.opt("policy_no")
									.toString();
							String client_no = jsonObject.opt("client_no")
									.toString();
							String name = jsonObject.opt("name").toString();
							String email = jsonObject.opt("email").toString();
							String telephone = jsonObject.opt("telephone")
									.toString();
							// add INFO TO DATABASE
							Log.e("", name);
							// db.checki();
							// clientToDb(policy_no,client_no,name);
							clientToDb(policy_no, client_no, name, email,
									telephone);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					// ArrayAdapter<String> dataAdapter = new
					// ArrayAdapter<String>(
					// getActivity(),
					// android.R.layout.simple_spinner_item, db.planlist());
					// dataAdapter
					// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// spinnerPlans.setAdapter(dataAdapter);
					/**/
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (n == 3) {
				try {
					Log.e("result", result);
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0, count = jsonArray.length(); i < count; i++) {
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// jsonObject.opt("Description");
							String policy_no = jsonObject.opt("policy_no")
									.toString();
							String client_no = jsonObject.opt("client_no")
									.toString();
							String amount = jsonObject.opt("amount").toString();
							// stringArray.add(description);
							// stringArray.add(jsonObject.toString());
							// add INFO TO DATABASE
							Log.e("Policy number", "" + policy_no);
							Log.e("Client Number", "" + client_no);
							Log.e("Amount", "" + amount);
							// db.checki();
							PaymentTag(policy_no, client_no, amount);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					// ArrayAdapter<String> dataAdapter = new
					// ArrayAdapter<String>(
					// getActivity(),
					// android.R.layout.simple_spinner_item, db.planlist());
					// dataAdapter
					// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// spinnerPlans.setAdapter(dataAdapter);
					/**/
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// getArray(result);
			this.progressDialog.dismiss();
		} // protected void onPostExecute(Void v)

	}

	public void planToDb(String description, String plan_code) {
		// TODO Auto-generated method stub
		Log.d("Scheme todb", "Tag todb: " + description);
		PlanTag tag1 = new PlanTag(description, plan_code);
		db.createPlanTag(tag1);
	}

	@SuppressWarnings("unused")
	public void PaymentTag(String policy_no, String client_no, String amount) {
		// TODO Auto-generated method stub
		// Log.d("Payment todb", "Tag todb: " + client_no + "---" + amount);
		PaymentTag tag1 = new PaymentTag(client_no, policy_no, amount);
		long tag2_id = db.createPaymentTag(tag1);
	}

	public void clientToDb(String policy_no, String client_no, String client,
			String email2, String telephone) {
		// TODO Auto-generated method stub
		Log.d("Scheme todb", "Tag todb: " + client);
		ClientTag tag1 = new ClientTag(client_no, policy_no, client, email2,
				telephone);
		db.createClientTag(tag1);
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getChildFragmentManager(), "datePicker");
	}

	private void setCurrentDateOnView() {
		// TODO Auto-generated method stub
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		dateofbirth.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(year).append("-").append(month + 1).append("-")
				.append(day).append(" "));
	}

	// email validation
	public boolean isEmailValid(String email) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
	}

	// phone number validation
	public boolean isValidPhoneNumber(CharSequence phoneNumber) {
		if (!TextUtils.isEmpty(phoneNumber)) {
			return Patterns.PHONE.matcher(phoneNumber).matches();
		}
		return false;
	}
}

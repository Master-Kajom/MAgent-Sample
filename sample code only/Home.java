package com.softclans.agents;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.softclans.agents.Registration.loadSomeStuff;
import com.softclans.agents.helper.AlertDialogGlobal;
import com.softclans.agents.helper.AsyncTaskActivity;
import com.softclans.agents.helper.ClientTag;
import com.softclans.agents.helper.DatabaseHelper;
import com.softclans.agents.helper.DetailFragment;
import com.softclans.agents.helper.Globalvar;
import com.softclans.agents.helper.PaymentTag;

public class Home extends ActionBarActivity {

	int mPosition = -1;
	String mTitle = "";
	Globalvar globalVariable;
	boolean pulled_client = false;
	public static String Agent_Name;
	public static String Agent_Market;
	public static String Agent_MarketCode;
	String searchpolno, policyno, clientno, clientname;
	String[] clientnames, policy_nos, clientnos, emails, telephones;
	ListView clients;
	String[] searchresults;

	// Array of strings storing country names
	String[] mMenuItems;
	DatabaseHelper db;

	// Array of integers points to images stored in /res/drawable-ldpi/
	int[] mFlags = new int[]{R.drawable.makt, R.drawable.trafficlights,
			R.drawable.accident, R.drawable.achieve, R.drawable.mufflersicon,
			R.drawable.useractive};
	public final static String EXTRA_MESSAGE = "com.softclans.agents";

	// Array of strings to initial counts
	String[] mCount = new String[]{"", "", "", "", "", ""};
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private LinearLayout mDrawer;
	private List<HashMap<String, String>> mList;
	private SimpleAdapter mAdapter;
	final private String COUNTRY = "Details";
	final private String FLAG = "flag";
	final private String COUNT = "count";
	@SuppressWarnings("unused")
	private AlertDialogGlobal adglobal;
	private SearchView searchView;
	ImageView logo;
	TextView tvagentno, tvagentname, tvsubheader;
	EditText txtagentno, txtagentname;
	String agentname, agent_market, market, marketcode;
	String agentfile = "MAGENT";
	FileOutputStream fos;

	boolean draweropened = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		getSupportActionBar().setTitle("M-Agent");
		//get the variables from the AsyncTaskclass
		Intent intent = getIntent();
		String menuItem = intent.getStringExtra(AsyncTaskActivity.EXTRA_MESSAGE);
		if (menuItem == null) {
			menuItem = "";
		}

		db = new DatabaseHelper(getApplicationContext());
		// Getting an array of country names
		mMenuItems = getResources().getStringArray(R.array.countries);

		// Title of the activity
		mTitle = (String) getTitle();

		// Getting a reference to the drawer listview
		mDrawerList = (ListView) findViewById(R.id.drawer_list);

		// Getting a reference to the sidebar drawer ( Title + ListView )
		mDrawer = (LinearLayout) findViewById(R.id.drawer);

		// Each row in the list stores country name, count and flag
		mList = new ArrayList<HashMap<String, String>>();

		for (int i = 0; i < 6; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put(COUNTRY, mMenuItems[i]);
			hm.put(COUNT, mCount[i]);
			hm.put(FLAG, Integer.toString(mFlags[i]));
			mList.add(hm);
		}

		// Keys used in Hashmap
		String[] from = {FLAG, COUNTRY, COUNT};

		// Ids of views in listview_layout
		int[] to = {R.id.flag, R.id.country, R.id.count};

		// Instantiating an adapter to store each items
		// R.layout.drawer_layout defines the layout of each item
		mAdapter = new SimpleAdapter(this, mList, R.layout.drawer_layout, from,
				to);

		// Getting reference to DrawerLayout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// Creating a ToggleButton for NavigationDrawer with drawer event
		// listener
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when drawer is closed */
			@Override
			public void onDrawerClosed(View view) {
				highlightSelectedCountry();
				supportInvalidateOptionsMenu();
				if (mPosition == -1) {
					showFragment(0);
					// Closing the drawer
					mDrawerLayout.closeDrawer(mDrawer);
				}
			}

			/** Called when a drawer is opened */
			@Override
			public void onDrawerOpened(View drawerView) {
				draweropened = true;
				getSupportActionBar().setTitle("Select an option");
				supportInvalidateOptionsMenu();
			}

		};

		// Setting event listener for the drawer
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// ItemClick event handler for the drawer items
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {

				// text.setVisibility(View.GONE);
				// s.setVisibility(View.GONE);

				// Increment hit count of the drawer list item
				//incrementHitCount(position);


				if (position < 6) { // Show fragment for countries : 0 to 5
					if (position == 5) {
						finish();
						//return;
					} else {
						showFragment(position);
					}
				} else { // Show message box for countries : 5 to 9
					Toast.makeText(getApplicationContext(),
							mMenuItems[position], Toast.LENGTH_LONG).show();
				}

				// Closing the drawer
				mDrawerLayout.closeDrawer(mDrawer);
			}
		});

		// Enabling Up navigation
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		logo = (ImageView) findViewById(R.id.imageView1);
		//variable for the agents details.
		tvagentno = (TextView) findViewById(R.id.textView1);
		tvagentname = (TextView) findViewById(R.id.textView2);
		txtagentno = (EditText) findViewById(R.id.txtagentno);
		txtagentname = (EditText) findViewById(R.id.txtagentname);
		//tvheader = (TextView)findViewById(R.id.textView3);
		tvsubheader = (TextView) findViewById(R.id.textView4);

		// Setting the adapter to the listView
		mDrawerList.setAdapter(mAdapter);

		//if a value exists then start the listview
		if (menuItem.equals("Markets")) {
			logo.setVisibility(View.GONE);
			tvagentno.setVisibility(View.GONE);
			tvagentname.setVisibility(View.GONE);
			txtagentno.setVisibility(View.GONE);
			txtagentname.setVisibility(View.GONE);
			//tvheader.setVisibility(View.GONE);
			tvsubheader.setVisibility(View.GONE);
			getSupportActionBar().setTitle("Markets");
			//call the fragment
			showFragment(1);
			// Closing the drawer
			mDrawerLayout.closeDrawer(mDrawer);

		} else if (menuItem.equals("Clients")) {
			logo.setVisibility(View.GONE);
			tvagentno.setVisibility(View.GONE);
			tvagentname.setVisibility(View.GONE);
			txtagentno.setVisibility(View.GONE);
			txtagentname.setVisibility(View.GONE);
			//tvheader.setVisibility(View.GONE);
			tvsubheader.setVisibility(View.GONE);
			getSupportActionBar().setTitle("Clients");
			//call the fragment
			showFragment(2);
			// Closing the drawer
			mDrawerLayout.closeDrawer(mDrawer);

		}

		globalVariable = (Globalvar) getApplicationContext();
		//populate the agents details.

		//read from file and display the name
		readfile();


	}

	public void readfile() {

		txtagentno.setText(globalVariable.getAgentno());
		//
		FileInputStream fis = null;
		// String agentname;
		try {
			fis = openFileInput("MAGENT");
			// Read all the byte data
			byte[] dataArray = new byte[fis.available()];
			while (fis.read(dataArray) != -1) {
				// read all the data
				//agentname,agent_market,market;
				this.agent_market = new String(dataArray);
				this.agent_market = this.agent_market.toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				String[] stringarrays = agent_market.split("=", 3);
				//if any of the strings is null then call class loadsome stuff and write some values into the file
				if (stringarrays.length < 3) {
					//write call to the server and write the values into the file
					//UpdateSomeStuff
					UpdateSomeStuff load = new UpdateSomeStuff();
					load.execute();
					//readfile();
					/*this.agentname = stringarrays[0];
					this.market = stringarrays[1];
					this.marketcode = stringarrays[2];
					
					Agent_Name = this.agentname;
					Agent_Market = this.market;
					Agent_MarketCode = this.marketcode;
					txtagentname.setText(this.agentname);*/

				} else {

					this.agentname = stringarrays[0];
					this.market = stringarrays[1];
					this.marketcode = stringarrays[2];

					Agent_Name = this.agentname;
					Agent_Market = this.market;
					Agent_MarketCode = this.marketcode;
					txtagentname.setText(this.agentname);
				}
				// return collected;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	////////////////////////////////////////////


	public void write_file() {

		//create a file and write the agent's name to it.
		try {
			fos = openFileOutput(this.agentfile, Context.MODE_PRIVATE);
			//write the data which is converted into an array of bytes
			try {
				if (agentname.equals("")) {

					//Create an exit pop up
					AlertDialog.Builder adglobals = new AlertDialog.Builder(Home.this);
					adglobals.setTitle("Alert");

					adglobals.setMessage("Agent name not found from server.Please Try again");
					//adglobals.setIcon(R.drawable.delete);
					adglobals.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog closed
							//should clear the data present that stores agent info
							db.checki(3);
							finish();
						}
					});


					adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog closed
							loadSomeStuff load = new loadSomeStuff();
							load.execute();
						}
					});


					// Showing Alert Message
					adglobals.show();

				} else {
					String agent_market = agentname + "=" + market + "=" + marketcode;
					fos.write(agent_market.getBytes());
					fos.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//alert to turn on internet and proceed


			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	public class UpdateSomeStuff extends AsyncTask<String, Integer, String> {
		final ProgressDialog ringProgressDialog = ProgressDialog.show(
				Home.this, "Please wait..", "Sending...", true);

		//String type = String;
		String urls = globalVariable.getUrl() + "agentinfo.php";
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
			if (isNetworkAvailable(Home.this)) {
				try {
					internet = true;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(urls);
					StringEntity Entity = null;
					Entity = new StringEntity("agentdetails=true" + "&AgentNo=" + globalVariable.getAgentno());

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

					JSONArray jArray = new JSONArray(result);
					JSONObject cred = new JSONObject();

					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json = jArray.getJSONObject(i);

						//make it display only what you wanna see
						agentname = json.getString("name");
						market = json.getString("unitname");
						marketcode = json.getString("unit_code");

					}


				} catch (Exception e) {
					//TODO handle exception
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


			if (internet == false) {

				//Create an exit pop up
				AlertDialog.Builder adglobals1 = new AlertDialog.Builder(Home.this);
				//adglobals.setTitle("Alert!");

				adglobals1.setMessage("Please enable internet so as to update your phone.\n Login again");
				//adglobal.setIcon(R.drawable.tick);


				adglobals1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				// Showing Alert Message
				adglobals1.show();

			} else {
					/*AlertDialog.Builder adglobals = new AlertDialog.Builder(Registration.this);
					adglobals.setTitle("Success!");
					adglobals.setMessage("Email Sent");						
					//adglobal.setIcon(R.drawable.tick);					
					adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					// Showing Alert Message
					adglobals.show();*/


				//write the agents name on a file.
				write_file();

				Agent_Name = agentname;
				Agent_Market = market;
				Agent_MarketCode = marketcode;
				txtagentname.setText(agentname);
				//readfile();
			}
		}


	}


	//////////////////////////////////////////////

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Process.killProcess(Process.myPid());

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} else {
			switch (item.getItemId()) {
				case R.id.action_pullclient:
					// newGame();
					pullclient();

					return true;
				case R.id.action_sync:
					sync();
					return true;
				//case R.id.action_search:
				//search();
				//if(mPosition == 0){
				// /implement a listener to filter the list view values
				//TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
					
					/*View actionBarView = getActionBar().getCustomView();
					EditText editText = (EditText) actionBarView.findViewById(R.id.txtsearch);
					editText.setVisibility(View.GONE);*/
					
					/*editText.addTextChangedListener(new TextWatcher() {

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
								//viewmarketlist();
							} else {


								//viewmarketclientlist(s.toString());
							}

						}

					});*/
				//}
				//return true;
				// default:
				// return super.onOptionsItemSelected(item);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void pullclient() {
		//create and alert dialog to insert the policy number 
		//Create an exit pop up
		AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
		builder.setTitle("Pull Client From Glico Server");

		//adglobals1.setMessage("Network Error");
		//adglobal.setIcon(R.drawable.tick);

		LayoutInflater inflater = Home.this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.pullclient, null);
		builder.setView(dialogView);
		// builder.setMessage("View Details");
		final EditText search = (EditText) dialogView
				.findViewById(R.id.txtpolicyno);


		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				searchpolno = search.getText().toString();
				//call the list view
				loadSomeStuff load = new loadSomeStuff();
				load.execute();


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

	private void search() {
		// TODO Auto-generated method stub
		Log.e("search", "button pressed ");

		searchView.setQueryHint("Type something...");
		int searchPlateId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlate = searchView.findViewById(searchPlateId);
		int searchTextId = searchPlate.getContext().getResources()
				.getIdentifier("android:id/search_src_text", null, null);
		TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub
				Log.e("Changing", "this is changing the text");
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@SuppressLint("InflateParams")
	private void sync() {
		// TODO Auto-generated method stub
		Log.e("sync", "trueeee");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.sync_menu, null);
		adglobal = new AlertDialogGlobal(Home.this, "Sync", dialogView, null, 1, false);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		//MenuItem menuItem = menu.findItem(R.id.action_search);
		//searchView = (SearchView) menuItem.getActionView();
		return true;
	}

	public void incrementHitCount(int position) {
		HashMap<String, String> item = mList.get(position);
		String count = item.get(COUNT);
		item.remove(COUNT);
		if (count.equals("")) {
			count = "  1  ";
		} else {
			int cnt = Integer.parseInt(count.trim());
			cnt++;
			count = "  " + cnt + "  ";
		}
		//item.put(COUNT, count);
		mAdapter.notifyDataSetChanged();
	}

	public void showFragment(int position) {

		// Currently selected country

		mTitle = mMenuItems[position];


		// Creating a fragment object
		DetailFragment dFragment = new DetailFragment();

		// Creating a Bundle object
		Bundle data = new Bundle();

		// Setting the index of the currently selected item of mDrawerList
		data.putInt("position", position);

		// Setting the position to the fragment
		dFragment.setArguments(data);

		// Getting reference to the FragmentManager
		FragmentManager fragmentManager = getSupportFragmentManager();

		// Creating a fragment transaction
		FragmentTransaction ft = fragmentManager.beginTransaction();

		// Adding a fragment to the fragment transaction
		ft.replace(R.id.content_frame, dFragment);

		// Committing the transaction
		ft.commit();

		//if(position == 0){
		//readfile();
		//}
	}

	public void refreshFragment(int position) {

		// Currently selected country
		if (position == 0) {
			mTitle = "Markets";
		}

		// Creating a fragment object
		DetailFragment dFragment = new DetailFragment();

		// Creating a Bundle object
		Bundle data = new Bundle();

		// Setting the index of the currently selected item of mDrawerList
		data.putInt("position", position);

		// Setting the position to the fragment
		dFragment.setArguments(data);

		// Getting reference to the FragmentManager
		FragmentManager fragmentManager = getSupportFragmentManager();

		// Creating a fragment transaction
		FragmentTransaction ft = fragmentManager.beginTransaction();

		// Adding a fragment to the fragment transaction
		ft.replace(R.id.content_frame, dFragment);

		// Committing the transaction
		ft.commit();


	}

	// Highlight the selected country : 0 to 5
	public void highlightSelectedCountry() {
		int selectedItem = mDrawerList.getCheckedItemPosition();

		if (selectedItem > 6) {
			mDrawerList.setItemChecked(mPosition, true);
			logo.setVisibility(View.VISIBLE);
		} else {
			//else{
			mPosition = selectedItem;
			logo.setVisibility(View.GONE);
			tvagentno.setVisibility(View.GONE);
			tvagentname.setVisibility(View.GONE);
			txtagentno.setVisibility(View.GONE);
			txtagentname.setVisibility(View.GONE);
			//tvheader.setVisibility(View.GONE);
			tvsubheader.setVisibility(View.GONE);
			//}
		}
		if (mPosition != -1) {
			getSupportActionBar().setTitle(mMenuItems[mPosition]);
			//logo.setVisibility(View.VISIBLE);
		}

	}

	boolean exit = false;

	@Override
	public void onBackPressed() {

		// TODO Auto-generated method stub	
		/*if(mPosition != -1){
			//then start the activity
			//startActivity(this);
			/*Intent intent = new Intent(getApplicationContext(), Home.class);
			getApplicationContext().startActivity(intent);
			showFragment(0);
			// Closing the drawer
			mDrawerLayout.closeDrawer(mDrawer);	
			
			
			
		}else{
				*/
		//Create an exit pop up
		AlertDialog.Builder adglobals = new AlertDialog.Builder(Home.this);
		//adglobals.setTitle("Alert");

		adglobals.setMessage("Are you sure you want to exit?");
		//adglobals.setIcon(R.drawable.delete);

		adglobals.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				//Home.super.onBackPressed();
				exit = true;
				finish();

				//finish();
				//Process.killProcess(Process.myPid());
				//showFragment(5)
				// Closing the drawer
				//mDrawerLayout.closeDrawer(mDrawer);
			}
		});

		adglobals.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing

			}
		});
		// Showing Alert Message
		adglobals.show();

		if (exit == true) {
			super.onBackPressed();
			exit = false;
		} else {
			//do nothing
		}

		//}


	}

	public static boolean isNetworkAvailable(Context context) {
		return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}

	//class to post emailaddress
	public class loadSomeStuff extends AsyncTask<String, Integer, String> {
		final ProgressDialog ringProgressDialog = ProgressDialog.show(
				Home.this, "Please wait..", "Sending...", true);

		//String type = String;
		String urls = globalVariable.getUrl() + "agentinfo.php";
		String type;
		boolean internet = false;
		JSONArray jArray;
		String policy_no;
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
			if (isNetworkAvailable(Home.this)) {
				try {
					internet = true;
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(urls);
					StringEntity Entity = null;
					Entity = new StringEntity("policyno=" + searchpolno + "&searchpolicyno=" + "true");
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

						searchresults = new String[jArray.length()];
						clientnames = new String[jArray.length()];
						policy_nos = new String[jArray.length()];
						clientnos = new String[jArray.length()];
						emails = new String[jArray.length()];
						telephones = new String[jArray.length()];
						notempty = true;


						/* TO DO insert the client into the client list */


					} else {

						notempty = false;
						searchresults = new String[1];
						clientnames = new String[0];
						policy_nos = new String[0];
						clientnos = new String[0];
						emails = new String[0];
						telephones = new String[0];

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
				AlertDialog.Builder adglobals1 = new AlertDialog.Builder(Home.this);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
				builder.setTitle("Pull Results From Glico Server");

				//adglobals1.setMessage("Network Error");
				//adglobal.setIcon(R.drawable.tick);

				LayoutInflater inflater = Home.this.getLayoutInflater();
				View dialogView = inflater.inflate(R.layout.clients, null);
				builder.setView(dialogView);
				// builder.setMessage("View Details");
				clients = (ListView) dialogView.findViewById(R.id.listViewClients);
				EditText search = (EditText) dialogView.findViewById(R.id.txtsearch);
				search.setVisibility(View.GONE);

				//searchpolno = search.getText().toString();
				@SuppressWarnings({"unchecked", "rawtypes"})
				ArrayAdapter adapter = new ArrayAdapter(Home.this,
						android.R.layout.simple_list_item_2, searchresults) {

					@SuppressWarnings("deprecation")
					@SuppressLint("InflateParams")
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						TwoLineListItem row;
						if (convertView == null) {
							LayoutInflater inflater = (LayoutInflater) Home.this
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
								policy_no = json.getString("policy_no");
								clientno = json.getString("client_no");
								clientname = json.getString("name");
								email = json.getString("email");
								telephone = json.getString("telephone");
								//notempty = false;
								//pass the variables to the array
								policy_nos[position] = policy_no;
								clientnos[position] = clientno;
								clientnames[position] = clientname;
								emails[position] = email;
								telephones[position] = telephone;

							} else {
								policy_no = "No Record Found";
								clientno = "";
								clientname = "";
							}
						} catch (Exception e) {
							// TODO handle exception
							Log.e("log.tag", "Error Parsing Data " + e.toString());
						}

						row.getText1().setText(policy_no);
						row.getText2().setText(clientname);
						row.getText1().setTextColor(Color.parseColor("#545454"));
						row.getText2().setTextColor(Color.parseColor("#545454"));

						return row;
					}
				};
				clients.setAdapter(adapter);

				////////////////////////////////////////////////////////////////////////////////////////

				clients.setOnItemClickListener(new OnItemClickListener() {

					@Override
					@SuppressLint("InflateParams")
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {

						final String[] clientnamepolno = new String[5];

						clientnamepolno[0] = clientnames[position];
						clientnamepolno[1] = policy_nos[position];
						clientnamepolno[2] = clientnos[position];
						clientnamepolno[3] = emails[position];
						clientnamepolno[4] = telephones[position];


						// Creating the instance of PopupMenu
						PopupMenu popup = new PopupMenu(Home.this, view);
						// Inflating the Popup using xml file
						popup.getMenuInflater().inflate(R.menu.popup_server,
								popup.getMenu());

						// registering popup with OnMenuItemClickListener
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								Log.e("item ID", "" + item.getTitle());
								if (item.getTitle().equals("Create Payment")) {
									LayoutInflater inflater = Home.this
											.getLayoutInflater();
									View dialogView = inflater.inflate(
											R.layout.paymentedit, null);
									//get

									adglobal = new AlertDialogGlobal(Home.this,
											"Payment", dialogView, clientnamepolno, 2, true);
								}
								return true;
							}
						});

						popup.show();// showing popup menu
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


}

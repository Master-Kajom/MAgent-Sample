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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.softclans.agents.helper.*;

public class MainActivity extends ActionBarActivity {
	//principalAge
	//principalAge

	protected ArrayList<NameValuePair> nameValuePairs;
	private String fname = "";
	private Spinner scheme;
	private Button calculatePrems;
	private EditText principalAge;
	private EditText spouseAge;
	private EditText noOfChildren;

	private String page;
	private String sage;
	private String dep;
	public static Integer monthlyPrem = 0;
	int mschemeid;
	
	// Database Helper
	DatabaseHelper db;
	private String schemeid;
	
	//for multiselect
	private PopupWindow pw;
	private boolean expanded; 		//to  store information whether the selected values are displayed completely or in shortened representatn
	public static boolean[] checkSelected;	// store select/unselect information about the values in the list
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize();

		db = new DatabaseHelper(getApplicationContext()); 
		
		SchemeTag tag1 = null;
		scheme = (Spinner) findViewById(R.id.spinnerScheme);
		calculatePrems = (Button) findViewById(R.id.calcPrems);
		principalAge = (EditText) findViewById(R.id.principalAge);
		spouseAge = (EditText) findViewById(R.id.spouseAge);
		noOfChildren = (EditText) findViewById(R.id.dependants);
		 
		addItemsOnScheme();
		db.closeDB();
		
		scheme.setOnItemSelectedListener(new CustomOnItemSelectListener());
		
		//button click code
		calculatePrems.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mschemeid = CustomOnItemSelectListener.idscheme;
				schemeid = String.valueOf(scheme.getSelectedItem());
				page = principalAge.getText().toString();
				sage = spouseAge.getText().toString();
				dep = noOfChildren.getText().toString();
				
				addItemsOnSchemeRates();
			}
		});
		
	}

	private void initialize() {
		// TODO Auto-generated method stub
		//data source for drop-down list
        final ArrayList<String> items = new ArrayList<String>();
    	items.add("Item 1");
    	items.add("Item 2");
    	items.add("Item 3");
    	items.add("Item 4");
    	items.add("Item 5");

    	checkSelected = new boolean[items.size()];
    	//initialize all values of list to 'unselected' initially
    	for (int i = 0; i < checkSelected.length; i++) {
    		checkSelected[i] = false;
    	}

	/*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
    	 * When this selectBox is clicked it will display all the selected values 
    	 * and when clicked again it will display in shortened representation as before.
    	 * */
    	final TextView tv = (TextView) findViewById(R.DropDownList.SelectBox);
    	tv.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			if(!expanded){
    				//display all selected values
    			String selected = "";
    			int flag = 0;
    			for (int i = 0; i < items.size(); i++) {
    				if (checkSelected[i] == true) {
    					 selected += items.get(i);
    					 selected += ", ";
    					flag = 1;
    				}
    			}
    			if(flag==1)
    			tv.setText(selected);
    			expanded =true;
    			}
    			else{
    				//display shortened representation of selected values
    				tv.setText(DropDownListAdapter.getSelected());
    				expanded = false;
    			}
    		}
    	});
        
        //onClickListener to initiate the dropDown list
        Button createButton = (Button)findViewById(R.DropDownList.create);
        createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initiatePopUp(items,tv);
			}
		});
	}

	protected void initiatePopUp(ArrayList<String> items, TextView tv) {
    	LayoutInflater inflater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	//get the pop-up window i.e.  drop-down layout
    	LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));
    	
    	//get the view to which drop-down layout is to be anchored
    	RelativeLayout layout1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
    	pw = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
    	
    	//Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
    	pw.setBackgroundDrawable(new BitmapDrawable());
    	pw.setTouchable(true);
    	
    	//let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
    	pw.setOutsideTouchable(true);
    	pw.setHeight(LayoutParams.WRAP_CONTENT);
    	
    	//dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
    	pw.setTouchInterceptor(new OnTouchListener() {
    		
    		@Override
			public boolean onTouch(View v, MotionEvent event) {
    			// TODO Auto-generated method stub
    			if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
    				pw.dismiss();
        			return true;    				
    			}
    			return false;
    		}
    	});
    	
    	//provide the source layout for drop-down
    	pw.setContentView(layout);
    	
    	//anchor the drop-down to bottom-left corner of 'layout1'
    	pw.showAsDropDown(layout1);
    	
    	//populate the drop-down list
    	final ListView list = (ListView) layout.findViewById(R.DropDownList.dropDownList);
    	DropDownListAdapter adapter = new DropDownListAdapter(this, items, tv);
    	list.setAdapter(adapter);
		// TODO Auto-generated method stub
		
	}

	protected void addItemsOnSchemeRates() {
		// TODO Auto-generated method stub
		nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("Scheme",
				Integer.toString(mschemeid)));
		nameValuePairs.add(new BasicNameValuePair("PrincipalAge",
				page.trim()));
		nameValuePairs.add(new BasicNameValuePair("SpouseAge",
				sage.trim())); 
		nameValuePairs.add(new BasicNameValuePair("Dependants",
				dep.trim()));  
		Log.d("scheme", ""+mschemeid);
		String url_select = "http://192.168.1.110:99/Agent/rates.php";
		//String url_select = "http://10.0.2.2:91/Agent/schemes.php";
		//String url_select = "http://www.softclans.co.ke/Agent/schemes.php";
		//String url_select = "http://agents.sobust.com/schemes.php";

		new MyAsyncTaskcr().execute(url_select,nameValuePairs,2);
		
		
	}

	private void addItemsOnScheme() {
		// TODO Auto-generated method stub
		nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("Uname",
				fname .trim()));
		String url_select = "http://192.168.1.110:99/Agent/schemes.php";
		//String url_select = "http://10.0.2.2:91/Agent/schemes.php";
		//String url_select = "http://www.softclans.co.ke/Agent/schemes.php";
		//String url_select = "http://agents.sobust.com/schemes.php";

		new MyAsyncTaskcr().execute(url_select,nameValuePairs,1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
	
	private void schemeToDb(String s) {
		// TODO Auto-generated method stub
		//db = new DatabaseHelper(getApplicationContext());
		Log.d("Scheme todb", "Tag todb: " + s);
		SchemeTag tag1 = new SchemeTag(s);
		long tag2_id = db.createSchemeTag(tag1);
		//Log.d("Tag Count", "Tag Count: " + db.schemelist().size());
	}
	


	public void ratesToDB(String description, int inPatient, String bracket, String category,
			int amount, int to, int from, int child, int principal, int spouse, int m, int m1, int m2,
			int m3, int m4, int m5) {
		// TODO Auto-generated method stub
		RatesTag tag1 = new RatesTag(inPatient, description, bracket, category, amount,
				to, from, child, principal, spouse, m, m1, m2, m3, m4, m5);
		//long tag2_id = db.createRatesTag(tag1);
	}

	public void displayres() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this,
				ResultsDisplay.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		MainActivity.this.finish();
	}
	
	class MyAsyncTaskcr extends AsyncTask<Object, String, Void> {

		private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
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
				// HttpClient is more then less deprecated. Need to change to URLConnection
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
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sBuilder = new StringBuilder();

				String line = null;
				while ((line = bReader.readLine()) != null) {
					sBuilder.append(line + "\n");
				}/**/

				inputStream.close();
				result = sBuilder.toString();

			} catch (Exception e) {
				Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
			}
			return null ;
		} // protected Void doInBackground(String... params)


		@Override
		@SuppressLint("NewApi")
		protected void onPostExecute(Void v) {
			if(n == 1){
				try {
					ArrayList<String> stringArray = new ArrayList<String>();
					JSONArray jsonArray = new JSONArray(result);
					for(int i = 0, count = jsonArray.length(); i< count; i++)
					{
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// jsonObject.opt("Description");
							String description = jsonObject.opt("Description").toString();
							stringArray.add(description);
							// stringArray.add(jsonObject.toString());
							//add INFO TO DATABASE
							schemeToDb(description);
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
					}

					//ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stringArray);
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item,db.schemelist());
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					scheme.setAdapter(dataAdapter);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if(n == 2){
				try {
					//ArrayList<String> stringArray = new ArrayList<String>();
					JSONArray jsonArray = new JSONArray(result);
					for(int i = 0, count = jsonArray.length(); i< count; i++)
					{
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// jsonObject.opt("Description");
							Integer description = jsonObject.optInt("amount");
							/*Integer inPatient = jsonObject.optInt("InPatient");
							String bracket = jsonObject.opt("Bracket").toString();
							String category = jsonObject.opt("Category").toString();
							Integer amount = jsonObject.optInt("Amount");
							Integer to = jsonObject.optInt("To");
							Integer from = jsonObject.optInt("From");
							Integer child = jsonObject.optInt("Child");
							Integer principal = jsonObject.optInt("Principal");
							Integer spouse = jsonObject.optInt("Spouse");
							Integer m = jsonObject.optInt("M");
							Integer m1 = jsonObject.optInt("M1");
							Integer m2 = jsonObject.optInt("M2");
							Integer m3 = jsonObject.optInt("M3");
							Integer m4 = jsonObject.optInt("M4");
							Integer m5 = jsonObject.optInt("M5");/**/
							
							//Log.d( "description", description);
							monthlyPrem = description;
							
							displayres();
							
							//add INFO TO DATABASE
							/*ratesToDB(description, inPatient, bracket, category, amount, to, from,
									child, principal, spouse, m, m1, m2, m3, m4, m5);*/
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
					}

					//ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stringArray);
					//ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ClientRegistration.this, android.R.layout.simple_spinner_item,db.countylist());
					//dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					//countys.setAdapter(dataAdapter);

				} catch (JSONException e) {
					e.printStackTrace();
				}/**/
			}else if(n == 3){
				/*if (result.charAt(0) == 'Y'){
					Toast.makeText(ClientRegistration.this,
							"Registration successfull", Toast.LENGTH_LONG)
							.show();
					menu.getItem(0).setVisible(true);
					((MyApplication) getApplication()).setImagetrigger("1");
					((MyApplication) getApplication()).setPrincipleidno(idno.getText().toString());
				}else {
					Toast.makeText(ClientRegistration.this,
							"Please try Again "+ result, Toast.LENGTH_LONG)
							.show();
				}*/
			}
			//	getArray(result);
			this.progressDialog.dismiss();
		} // protected void onPostExecute(Void v)

	}
}

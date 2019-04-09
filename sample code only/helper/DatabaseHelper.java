package com.softclans.agents.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.softclans.agents.R;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import com.softclans.agents.helper.Globalvar;

public class DatabaseHelper extends SQLiteOpenHelper {
	Globalvar globalVariable;
	// Logcat tag
	private static final String LOG = "DatabaseHelper";
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "slamsglife1";

	// Table Names
	// private static final String TABLE_CLIENTINFO = "clientsinfo";
	private static final String TABLE_SCHEMES = "schemes";
	private static final String TABLE_SC = "scheme";
	private static final String TABLE_PLANS = "plans";
	private static final String TABLE_RATES = "rates";
	private static final String TABLE_BRANCH = "branches";
	private static final String TABLE_MARKETS = "markets";
	private static final String TABLE_CLIENTS = "clients";
	private static final String TABLE_PAYMENTS = "payments";
	private static final String TABLE_AUTH = "auth";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";

	// table auth columns
	private static final String KEY_IMEI = "imei";
	private static final String KEY_AGENTNO = "agentno";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_REGISTERED = "registered";
	private static final String KEY_INITIAL = "initial";

	// payments table columns
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_CLIENT_NAME = "client_name";
	private static final String KEY_PAYMENT_DATE = "payment_date";
	private static final String KEY_PAYMENT_TYPE = "payment_type";
	private static final String KEY_SYSTIME = "systime";

	// client table columns
	private static final String KEY_POLICYNO = "policy_no";
	private static final String KEY_CLIENTNO = "client_no";
	private static final String KEY_CLIENT = "client";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_TELEPHONE = "telephone";

	// markets table columns
	private static final String KEY_MARKET_CODE = "market_code";
	private static final String KEY_MARKET_NAME = "market_name";

	// schemes column names
	private static final String KEY_DESCRIPTION = "description";

	private static final String KEY_PLANCODE = "plan_code";

	// schemes column names
	private static final String KEY_BRANCHNAME = "branchname";
	private static final String KEY_BRANCH = "branch";

	// rates column names
	private static final String KEY_BRACKET = "bracket";
	private static final String KEY_CATEGORY = "category";
	private static final String KEY_INPATIENT = "inpatient";
	// private static final String KEY_Amount = "Amount";
	private static final String KEY_To = "To";
	private static final String KEY_From = "From";
	private static final String KEY_Child = "Child";
	private static final String KEY_Principal = "Principal";
	private static final String KEY_Spouse = "Spouse";
	private static final String KEY_m = "m";
	private static final String KEY_m1 = "m1";
	private static final String KEY_m2 = "m2";
	private static final String KEY_m3 = "m3";
	private static final String KEY_m4 = "m4";
	private static final String KEY_m5 = "m5";
	// private static final String KEY_Principal = "Principal";

	// Table Create Statements
	// Todo table create statement
	@SuppressWarnings("unused")
	private static final String CREATE_TABLE_RATES = "CREATE TABLE "
			+ TABLE_RATES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_DESCRIPTION + " TEXT , " + KEY_CREATED_AT + " DATETIME,"
			+ KEY_BRACKET + " TEXT ," + KEY_CATEGORY + " TEXT ,"
			+ KEY_INPATIENT + " INTEGER ," + KEY_AMOUNT + " INTEGER ," + KEY_To
			+ " INTEGER ," + KEY_From + " INTEGER ," + KEY_Child + " INTEGER ,"
			+ KEY_Principal + " INTEGER ," + KEY_Spouse + " INTEGER ," + KEY_m
			+ " INTEGER ," + KEY_m1 + " INTEGER ," + KEY_m2 + " INTEGER ,"
			+ KEY_m3 + " INTEGER ," + KEY_m4 + " INTEGER ," + KEY_m5
			+ " INTEGER " + ")";

	private static final String CREATE_TABLE_SCHEMES = "CREATE TABLE "
			+ TABLE_SCHEMES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_DESCRIPTION + " TEXT UNIQUE, " + KEY_CREATED_AT + " DATETIME"
			+ ")";

	private static final String CREATE_TABLE_PLANS = "CREATE TABLE "
			+ TABLE_PLANS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_DESCRIPTION + " TEXT UNIQUE, " + KEY_PLANCODE
			+ " TEXT UNIQUE" + ")";

	private static final String CREATE_TABLE_MARKETS = "CREATE TABLE "
			+ TABLE_MARKETS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_MARKET_CODE + " TEXT UNIQUE, " + KEY_MARKET_NAME
			+ " TEXT UNIQUE" + ")";

	@SuppressWarnings("unused")
	private static final String CREATE_TABLE_S = "CREATE TABLE " + TABLE_SC
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DESCRIPTION
			+ " TEXT UNIQUE," + KEY_CREATED_AT + " DATETIME" + ")";

	private static final String CREATE_TABLE_BRANCH = "CREATE TABLE "
			+ TABLE_BRANCH + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_BRANCH + " TEXT UNIQUE, " + KEY_BRANCHNAME + " TEXT UNIQUE"
			+ ")";

	private static final String CREATE_TABLE_CLIENTS = "CREATE TABLE "
			+ TABLE_CLIENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_POLICYNO + " TEXT UNIQUE, " + KEY_CLIENTNO + " TEXT, "
			+ KEY_CLIENT + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_TELEPHONE
			+ " TEXT" + ")";

	private static final String CREATE_TABLE_PAYMENTS = "CREATE TABLE "
			+ TABLE_PAYMENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_POLICYNO + " TEXT, " + KEY_CLIENTNO + " TEXT,  "
			+ KEY_CLIENT_NAME + " TEXT, " + KEY_PAYMENT_DATE + " TEXT, "
			+ KEY_PAYMENT_TYPE + " TEXT," + KEY_SYSTIME + " TEXT," + KEY_IMEI
			+ " TEXT," + KEY_MARKET_CODE + " TEXT, " + KEY_AGENTNO + " TEXT, "
			+ KEY_AMOUNT + " TEXT" + ")";

	private static final String CREATE_TABLE_AUTH = "CREATE TABLE "
			+ TABLE_AUTH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IMEI
			+ " TEXT, " + KEY_AGENTNO + " TEXT, " + KEY_REGISTERED
			+ " INTEGER, " + KEY_INITIAL + " INTEGER, " + KEY_PASSWORD
			+ " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		//globalVariable = (Globalvar) getApplicationContext();

		// creating required tables
		db.execSQL(CREATE_TABLE_PLANS);
		db.execSQL(CREATE_TABLE_MARKETS);
		db.execSQL(CREATE_TABLE_SCHEMES);
		db.execSQL(CREATE_TABLE_BRANCH);
		db.execSQL(CREATE_TABLE_CLIENTS);
		db.execSQL(CREATE_TABLE_PAYMENTS);
		db.execSQL(CREATE_TABLE_AUTH);
		// Log.e(LOG, CREATE_TABLE_SCHEMES);

		/* db.execSQL(CREATE_TABLE_TODO_TAG); */
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKETS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
		onCreate(db);
	}

	public void checkifexists(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEMES);
	}

	public void checki(int table) {
		SQLiteDatabase db = this.getWritableDatabase();
		if (table == 1) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
			db.execSQL(CREATE_TABLE_CLIENTS);
			Log.e("table auth", "msg");
		} else if (table == 0) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
			db.execSQL(CREATE_TABLE_PAYMENTS);
			Log.e("table msg", "payments table created");
		} else if (table == 2) {
			db.execSQL("DELETE FROM " + TABLE_PAYMENTS);
		}else if (table == 3) {
			db.execSQL("DELETE FROM " + TABLE_AUTH);
		}
	}
	
	//delete the payment
	public void deletepayment(String mid) {
		SQLiteDatabase db = this.getWritableDatabase();
		//delete the payment use the mid
		db.execSQL("DELETE FROM " + TABLE_PAYMENTS+ " WHERE "+KEY_ID+ " = "+mid);
	}
	//update the payment
	public void updatepayment(String mid,String amount,String payment_date,String payment_type,String market_code) {
		SQLiteDatabase db = this.getWritableDatabase();
		//delete the payment use the mid
		//db.execSQL("DELETE FROM " + TABLE_PAYMENTS+ " WHERE "+KEY_ID+ " = "+mid);
		db.execSQL("UPDATE " + TABLE_PAYMENTS + " SET " + KEY_AMOUNT + "='" + amount + "'," + KEY_PAYMENT_DATE + "='" + payment_date + "'," + KEY_PAYMENT_TYPE + "='" + payment_type + "'," + KEY_MARKET_CODE + "='" + market_code + "' WHERE " + KEY_ID + " = " + mid);
	}
	
	
	public void checkis(ArrayList<NameValuePair> namepairvalue ) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sec = namepairvalue.toString();
		//private String arr[]=convert(namepairvalue);
		String []parts = sec.split("=");
		String []parts2 = parts[1].split("]");
		String parts3 = parts2[0].replace("[","");
		//parts3 = parts3.replace("{","");
		//parts3 = parts3.replace("}","");
		parts3 = "["+parts3+"]";
		
		//List<String> myList = new ArrayList<String>(Arrays.asList(parts3.split(",")));
		JSONArray jArray = null;
		try {
			jArray = new JSONArray(parts3);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	//namepairvalue.
		for(int i=0;i<jArray.length();i++){
			//TODO, Get the value of the mid only.
			
			try {
				
				JSONObject json = jArray.getJSONObject(i);
				//PaymentTag data = (PaymentTag) namepairvalue.get(i);
				String mid = json.getString("mid");
				
				db.execSQL("DELETE FROM " + TABLE_PAYMENTS+ " WHERE "+KEY_ID+ " = "+mid);
								
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	/*
	 * Creating auth
	 */
	public long createAuthTagTag(AuthTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IMEI, tag.getImei());
		values.put(KEY_AGENTNO, tag.getAgentno());
		values.put(KEY_PASSWORD, tag.getPassword());
		values.put(KEY_REGISTERED, tag.getRegistered());
		values.put(KEY_INITIAL, tag.getInitial());

		// insert row
		long tag_id = db.insert(TABLE_AUTH, null, values);

		return tag_id;
	}

	/*
	 * Creating scheme
	 */
	public long createSchemeTag(SchemeTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DESCRIPTION, tag.getDescription());
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row
		long tag_id = db.insert(TABLE_SCHEMES, null, values);

		return tag_id;
	}

	/*
	 * Creating scheme
	 */
	public long createBranchTag(BranchTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BRANCHNAME, tag.getBranchname());
		values.put(KEY_BRANCH, tag.getBranch());

		// insert row
		long tag_id = db.insert(TABLE_BRANCH, null, values);

		return tag_id;
	}

	/*
	 * Creating Plans
	 */
	public long createPlanTag(PlanTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DESCRIPTION, tag.getDescription());
		values.put(KEY_PLANCODE, tag.getPlan_code());

		// insert row
		long tag_id = db.insert(TABLE_PLANS, null, values);

		return tag_id;
	}

	/*
	 * Creating Markets
	 */
	public long createMarketTag(MarketTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARKET_CODE, tag.getMarket_code());
		values.put(KEY_MARKET_NAME, tag.getMarket_name());

		// insert row
		long tag_id = db.insert(TABLE_MARKETS, null, values);

		return tag_id;
	}

	/*
	 * Creating Clients
	 */
	public long createClientTag(ClientTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		long tag_id = 0;

		String selectQuery = "SELECT * FROM " + TABLE_CLIENTS+ " WHERE " + KEY_POLICYNO + " = '"
				+ tag.getPolicy_no() + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase rdb = this.getReadableDatabase();
		Cursor c = rdb.rawQuery(selectQuery, null);

		if(c.getCount() == 0){
			//return;
			values.put(KEY_POLICYNO, tag.getPolicy_no());
			values.put(KEY_CLIENTNO, tag.getClient_no());
			values.put(KEY_CLIENT, tag.getClient());
			values.put(KEY_EMAIL, tag.getEmail());
			values.put(KEY_TELEPHONE, tag.getTelephone());
			tag_id = db.insert(TABLE_CLIENTS, null, values);

		}else {




		}
		// insert row


		return tag_id;
	}

	/*
	 * Creating Clients
	 */
	public long createPaymentTag(PaymentTag tag) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_POLICYNO, tag.getPolicy_no());
		values.put(KEY_CLIENTNO, tag.getClient_no());
		values.put(KEY_AMOUNT, tag.getAmount());
		values.put(KEY_AGENTNO, tag.getAgent_no());
		values.put(KEY_MARKET_CODE, tag.getMarket_code());
		values.put(KEY_CLIENT_NAME, tag.getClient_name());
		values.put(KEY_PAYMENT_DATE, tag.getPayment_date());
		values.put(KEY_IMEI, tag.getImei());
		values.put(KEY_PAYMENT_TYPE, tag.getPayment_type());
		values.put(KEY_SYSTIME, tag.getSystime());

		// insert row
		long tag_id = db.insert(TABLE_PAYMENTS, null, values);
		// Log.e("kk", ""+tag_id);
		return tag_id;
	}

	/**
	 * getting all schemes
	 * */
	public List<String> schemelist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_DESCRIPTION + " FROM "
				+ TABLE_SCHEMES;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
			} while (c.moveToNext());
		}
		return tags;
	}

	public List<String> paymentlist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_POLICYNO + " FROM "
				+ TABLE_PAYMENTS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				String pol = c.getString(c.getColumnIndex(KEY_POLICYNO));
				Log.e("pol", pol);
				// adding to tags list
			} while (c.moveToNext());
		}
		return tags;
	}
	
	//create method to sync data based on date
	public List<PaymentTag> paymentreportlistupload(String datefrom,String dateto,String type,String imeins, String agentns) {
		List<PaymentTag> tags = new ArrayList<PaymentTag>();
		
		String selectQuery = "";
		
		if(type == "Payments By Date"){
			selectQuery = "SELECT * FROM " + TABLE_PAYMENTS + " WHERE " + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+datefrom+" 99:99:99' ";
		}
		if(type == "Payments By Date Range"){
			selectQuery = "SELECT * FROM " + TABLE_PAYMENTS+" WHERE " + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99' ";
		}

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);



		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				String clno = c.getString(c.getColumnIndex(KEY_CLIENTNO));
				String pol = c.getString(c.getColumnIndex(KEY_POLICYNO));
				String amt = c.getString(c.getColumnIndex(KEY_AMOUNT));
				String clname = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				String pdate = c.getString(c.getColumnIndex(KEY_PAYMENT_DATE));
				String ptype = c.getString(c.getColumnIndex(KEY_PAYMENT_TYPE));
				String systime = c.getString(c.getColumnIndex(KEY_SYSTIME));
				String mktcode = c.getString(c.getColumnIndex(KEY_MARKET_CODE));
				String imein = imeins;//"";//c.getString(c.getColumnIndex(KEY_IMEI));
				String agentn = agentns;//"";//c.getString(c.getColumnIndex(KEY_AGENTNO));
				String mid = c.getString(c.getColumnIndex(KEY_ID));

				// Log.e("pol", pol);
				// adding to tags list
				tags.add(new PaymentTag(clno, pol, amt, clname, pdate, ptype,
						systime, mktcode, imein, agentn, mid));

				Log.e("the jsonarray", mid);
			} while (c.moveToNext());
		}
		return tags;
	}

	public List<PaymentTag> paymentlistupload() {
		List<PaymentTag> tags = new ArrayList<PaymentTag>();
		String selectQuery = "SELECT * FROM " + TABLE_PAYMENTS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				String clno = c.getString(c.getColumnIndex(KEY_CLIENTNO));
				String pol = c.getString(c.getColumnIndex(KEY_POLICYNO));
				String amt = c.getString(c.getColumnIndex(KEY_AMOUNT));
				String clname = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				String pdate = c.getString(c.getColumnIndex(KEY_PAYMENT_DATE));
				String ptype = c.getString(c.getColumnIndex(KEY_PAYMENT_TYPE));
				String systime = c.getString(c.getColumnIndex(KEY_SYSTIME));
				String mktcode = c.getString(c.getColumnIndex(KEY_MARKET_CODE));
				String imein = c.getString(c.getColumnIndex(KEY_IMEI));
				String agentn = c.getString(c.getColumnIndex(KEY_AGENTNO));
				String mid = c.getString(c.getColumnIndex(KEY_ID));

				// Log.e("pol", pol);
				// adding to tags list
				tags.add(new PaymentTag(clno, pol, amt, clname, pdate, ptype,
						systime, mktcode, imein, agentn, mid));

				Log.e("the jsonarray", mid);
			} while (c.moveToNext());
		}
		return tags;
	}

	/**
	 * getting all branches
	 * */
	public List<String> branchlist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_BRANCHNAME + " FROM "
				+ TABLE_BRANCH + " order by " + KEY_BRANCHNAME + " asc";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_BRANCHNAME)));
			} while (c.moveToNext());
		}
		return tags;
	}

	/**
	 * getting all plans
	 * */
	public List<String> planlist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_DESCRIPTION + " FROM "
				+ TABLE_PLANS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
			} while (c.moveToNext());
		}
		return tags;
	}

	/**
	 * getting all rates
	 * */
	public List<String> rateslist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT * FROM " + TABLE_RATES;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
			} while (c.moveToNext());
		}
		return tags;
	}

	/**
	 * getting all clients
	 * */
	public List<String> clientlist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_CLIENT + " FROM " + TABLE_CLIENTS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));
				Log.e(LOG, c.getString(c.getColumnIndex(KEY_CLIENT)));
				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_CLIENT)));
			} while (c.moveToNext());
		}
		return tags;
	}
	//implement the search for market
	public List<String> searchmarketlist(String search) {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_MARKET_NAME + " FROM "
				+ TABLE_MARKETS+" WHERE " + KEY_MARKET_NAME + " LIKE '"
						+ search + "%'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));
				Log.e(LOG, c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
			} while (c.moveToNext());
		}
		return tags;
	}
	
	//implement the search for client.
		public List<PaymentTag> searchpaymentslistpolno(String search) {
			List<PaymentTag> tag = new ArrayList<PaymentTag>();
			
			String selectQuery = "SELECT * FROM " + TABLE_PAYMENTS+" WHERE " + KEY_POLICYNO + " LIKE '"
					+ search + "%'" +" ORDER BY "+KEY_PAYMENT_DATE+" DESC";

			Log.e(LOG, selectQuery);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			
			if(c.getCount() == 0){
				//return;
				tag.add(new PaymentTag("", "", ""));
				return tag;
			}

			// looping through all rows and adding to list
			if (c.moveToFirst()) {
				do {
					// Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
					String policy_no = c.getString(c.getColumnIndex(KEY_POLICYNO)) + " - " + c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
					String client_no = c.getString(c.getColumnIndex(KEY_CLIENTNO));
					String amount = c.getString(c.getColumnIndex(KEY_AMOUNT));
					// adding to tags list
					tag.add(new PaymentTag(client_no, policy_no, amount));
					// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
				} while (c.moveToNext());
			}
			return tag;
		}
public String[] getclientnoss(String mid) {
			
			String clientno = "";

			String selectQuery = "SELECT " + KEY_CLIENTNO + " FROM " + TABLE_CLIENTS+" WHERE " + KEY_ID + " = '"
							+ mid + "'";

			Log.e(LOG, selectQuery);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);

			if (c.moveToFirst()) {
				do {
					// adding to tags list
					clientno = c.getString(c.getColumnIndex(KEY_CLIENTNO));
					// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
					//Log.e(LOG, "id :" + telephone);
				} while (c.moveToNext());
			}
			// Log.e(LOG, "id :" + id_int);

			return new String[] {clientno};
		}
		
	public String[] getclientinfo(String clientno) {

		String email = "";
		String telephone = "";

		String selectQuery = "SELECT " + KEY_EMAIL + "," + KEY_TELEPHONE
				+ " FROM " + TABLE_CLIENTS + " WHERE " + KEY_CLIENTNO + " = '"
				+ clientno + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// adding to tags list
				email = c.getString(c.getColumnIndex(KEY_EMAIL));
				telephone = c.getString(c.getColumnIndex(KEY_TELEPHONE));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				// Log.e(LOG, "id :" + telephone);
			} while (c.moveToNext());
		}
		// Log.e(LOG, "id :" + id_int);

		return new String[] { email, telephone };
	}
	
	public String[] getmarketcode(String market) {
		
		String market_code = "";
		//String telephone = "";

		String selectQuery = "SELECT " + KEY_MARKET_CODE +" FROM " + TABLE_MARKETS+" WHERE " + KEY_MARKET_NAME + " = '"
						+ market + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// adding to tags list
				market_code = c.getString(c.getColumnIndex(KEY_MARKET_CODE));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				//Log.e(LOG, "id :" + telephone);
			} while (c.moveToNext());
		}
		// Log.e(LOG, "id :" + id_int);

		return new String[] { market_code};
	}
		
	//implement the search for client.

	
	//implement the search for client.
	public List<ClientTag> searchclientlistpolno(String search) {
		List<ClientTag> tags = new ArrayList<ClientTag>();
		String selectQuery = "SELECT " + KEY_POLICYNO + "," + KEY_CLIENT + ","
				+ KEY_CLIENTNO + " FROM " + TABLE_CLIENTS+" WHERE " + KEY_CLIENT + " LIKE '"
						+ search + "%' OR " + KEY_POLICYNO + " LIKE '" + search + "%'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//if empty
		//if c is empty then return;
		if(c.getCount() == 0){
			//return;
			tags.add(new ClientTag("", "", ""));
			return tags;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));
				Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				String client_no = c.getString(c.getColumnIndex(KEY_CLIENT));
				String policy_no = c.getString(c.getColumnIndex(KEY_POLICYNO));
				String client = c.getString(c.getColumnIndex(KEY_CLIENT));
				// adding to tags list
				tags.add(new ClientTag(client_no, policy_no, client));
				// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
			} while (c.moveToNext());
		}
		return tags;
	}

	public List<ClientTag> clientlistpolno() {
		List<ClientTag> tags = new ArrayList<ClientTag>();
		String selectQuery = "SELECT " + KEY_POLICYNO + "," + KEY_CLIENT + ","
				+ KEY_CLIENTNO + " FROM " + TABLE_CLIENTS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c.getCount() == 0){
			//return;
			tags.add(new ClientTag("", "No Clients found. Please sync", ""));
			return tags;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));
				Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				String client_no = c.getString(c.getColumnIndex(KEY_CLIENTNO));
				String policy_no = c.getString(c.getColumnIndex(KEY_POLICYNO));
				String client = c.getString(c.getColumnIndex(KEY_CLIENT));
				// adding to tags list
				tags.add(new ClientTag(client_no, policy_no, client));
				// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
			} while (c.moveToNext());
		}
		return tags;
	}
	
	public List<PaymentTag> reportpaymenttaginfo(String datefrom,String dateto,String type,String subtype,String clientname) {
		List<PaymentTag> tag = new ArrayList<PaymentTag>();
		
		String selectQuery = "";
		
		
		if(type == "Payments By Date"){
			selectQuery = "SELECT * FROM " + TABLE_PAYMENTS + " WHERE " + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+datefrom+" 99:99:99' ";
		}
		if(type == "Payments By Date Range"){
			selectQuery = "SELECT * FROM " + TABLE_PAYMENTS+" WHERE " + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99' ";
		}
		if(type == "Payments By Client" && subtype == "1"){
			selectQuery = "SELECT * FROM " + TABLE_PAYMENTS+" WHERE (" + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99') "
					+ " AND (" + KEY_CLIENT_NAME + " = '"+clientname+"')";
		}
		if(type == "Payments By Client" && subtype == "2"){
			selectQuery = "SELECT COUNT(payment_date) AS COUNT,SUM(amount) AS SUM, payment_date FROM payments WHERE (" + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99') "
					+ "AND (" + KEY_CLIENT_NAME + " = '"+clientname+"') GROUP BY payment_date";
		}
		if(type == "Payment By Market" && subtype == "1"){
			selectQuery = "SELECT p."+ KEY_POLICYNO+",p."+KEY_CLIENTNO+",p."+KEY_CLIENT_NAME+",p."+KEY_PAYMENT_DATE+",p."+KEY_PAYMENT_TYPE+",p."+KEY_MARKET_CODE+",p."+KEY_AGENTNO+",p."+KEY_AMOUNT+ ",d."+KEY_MARKET_NAME+" AS "+KEY_MARKET_CODE+" FROM payments p LEFT JOIN "+ TABLE_MARKETS +" d WHERE ((p." + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99') AND (d." + KEY_MARKET_NAME + " = '"+clientname+"'))";
		}
		if(type == "Payment By Market" && subtype == "2"){
			selectQuery = "SELECT COUNT(p.payment_date) AS COUNT,SUM(p.amount) AS SUM, p.payment_date FROM payments p LEFT JOIN "+ TABLE_MARKETS +" d WHERE (p." + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99') "
					+ "AND (d." + KEY_MARKET_NAME + " = '"+clientname+"') GROUP BY p.payment_date";
		}
		
		//TODO write the proper sql code.
		if(type == "Summary By Day"){
			selectQuery = "SELECT COUNT(payment_date) AS COUNT,SUM(amount) AS SUM, payment_date FROM payments WHERE (" + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+datefrom+" 99:99:99')"
					+ " GROUP BY payment_date";
		}
		if(type == "Summary By Date Range"){
			selectQuery = "SELECT SUM(amount) AS SUM FROM payments WHERE (" + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99')";
		}
		if(type == "Daily Summaries"){
			selectQuery = "SELECT COUNT(payment_date) AS COUNT,SUM(amount) AS SUM, payment_date FROM payments WHERE (" + KEY_PAYMENT_DATE + " BETWEEN '"+datefrom+"' AND '"+dateto+" 99:99:99') GROUP BY payment_date";
		}

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//if c is empty then return;
		if(c.getCount() == 0){
			//return;
			tag.add(new PaymentTag("", "No Payments Done", "","","","","","","",""));
			return tag;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				if(subtype == "0" || subtype == "1"){
				
					String ClientName = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
					String PolicyNo = c.getString(c.getColumnIndex(KEY_POLICYNO)) + " - " + c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
					String Amount = c.getString(c.getColumnIndex(KEY_AMOUNT));
					String PaymentDate = c.getString(c.getColumnIndex(KEY_PAYMENT_DATE));
					String PaymentType = c.getString(c.getColumnIndex(KEY_PAYMENT_TYPE));
					String Market = c.getString(c.getColumnIndex(KEY_MARKET_CODE));
					// adding to tags list
					/*String client_no, String policy_no, String amount,
					String client_name, String payment_date, String payment_type,
					String systime, String market_code, String imei, String agent_no*/
					tag.add(new PaymentTag("", PolicyNo, Amount,ClientName,PaymentDate,PaymentType,"",Market,"",""));
					// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
					
				}
				
				if(subtype == "2"){
					if(type == "Summary By Day" || type == "Summary By Date Range" ){
						//Display only amount.
						
						String PolicyNo = c.getString(c.getColumnIndex("SUM"));
						
						tag.add(new PaymentTag("", PolicyNo, "","","","","","","",""));
					}else{
						
						String PolicyNo = c.getString(c.getColumnIndex("payment_date"));
						String Amount = c.getString(c.getColumnIndex("SUM"));
						
						
						tag.add(new PaymentTag("", PolicyNo, Amount,"","","","","","",""));
					}
				}
				
				
				
				} while (c.moveToNext());
		}
		return tag;
	}
	
	
	
	public List<PaymentTag> clientpaymenttaginfo(String[] string) {
		List<PaymentTag> tag = new ArrayList<PaymentTag>();
		/*
		 * String selectQuery = "SELECT " + KEY_POLICYNO + ","+ KEY_CLIENTNO +
		 * ","+ KEY_AMOUNT + " FROM " + TABLE_PAYMENTS;
		 */
		
		/*String selectQuery = "SELECT " + KEY_POLICYNO + "," + KEY_CLIENT_NAME
				+ "," + KEY_AMOUNT + "," + KEY_PAYMENT_DATE + ","
				+ KEY_PAYMENT_TYPE + "," + KEY_MARKET_CODE + " FROM "
				+ TABLE_PAYMENTS + " WHERE " + KEY_POLICYNO + " = '"
				+ string[0] + "'" + " AND " + KEY_AMOUNT + " = '" + string[1]
				+ "'";*/
		
		String selectQuery = "SELECT * FROM " + TABLE_PAYMENTS+" WHERE " + KEY_CLIENT_NAME + " = '"
				+ string[0] + "'" + " AND " + KEY_POLICYNO + " = '" + string[1]
				+ "'"; 

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//if c is empty then return;
		if(c.getCount() == 0){
			//return;
			tag.add(new PaymentTag("", "empty", "","","","","","","",""));
			return tag;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				
				String ClientName = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				String PolicyNo = c.getString(c.getColumnIndex(KEY_POLICYNO));
				String Amount = c.getString(c.getColumnIndex(KEY_AMOUNT));
				String PaymentDate = c.getString(c.getColumnIndex(KEY_PAYMENT_DATE));
				String PaymentType = c.getString(c.getColumnIndex(KEY_PAYMENT_TYPE));
				String Market = c.getString(c.getColumnIndex(KEY_MARKET_CODE));
				// adding to tags list
				/*String client_no, String policy_no, String amount,
				String client_name, String payment_date, String payment_type,
				String systime, String market_code, String imei, String agent_no*/
				tag.add(new PaymentTag("", PolicyNo, Amount,ClientName,PaymentDate,PaymentType,"",Market,"",""));
				// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
			} while (c.moveToNext());
		}
		return tag;
	}
	
	
	public List<PaymentTag> getclientno(String mid) {
		List<PaymentTag> tag = new ArrayList<PaymentTag>();
		/*
		 * String selectQuery = "SELECT " + KEY_POLICYNO + ","+ KEY_CLIENTNO +
		 * ","+ KEY_AMOUNT + " FROM " + TABLE_PAYMENTS;
		 */
		String selectQuery = "SELECT * FROM "+TABLE_PAYMENTS+" WHERE "+KEY_ID+" = "+mid;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c.getCount() == 0){
			//return;
			tag.add(new PaymentTag("", "No record found", ""));
			return tag;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				String policy_no = c.getString(c.getColumnIndex(KEY_CLIENTNO));
				String client = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				String amount = c.getString(c.getColumnIndex(KEY_POLICYNO));
				// adding to tags list
				tag.add(new PaymentTag(client, policy_no, amount));
				// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
			} while (c.moveToNext());
		}
		return tag;
	}
	//implement the search for client.
	public List<ClientTag> getclientnos(String mid) {
		List<ClientTag> tag = new ArrayList<ClientTag>();
		/*
		 * String selectQuery = "SELECT " + KEY_POLICYNO + ","+ KEY_CLIENTNO +
		 * ","+ KEY_AMOUNT + " FROM " + TABLE_PAYMENTS;
		 */
		String selectQuery = "SELECT * FROM "+TABLE_CLIENTS+" WHERE "+KEY_CLIENTNO+" = "+mid;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c.getCount() == 0){
			//return;
			tag.add(new ClientTag("", "No record found", ""));
			return tag;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				String client_no = c.getString(c.getColumnIndex(KEY_CLIENTNO));
				//String client = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				//String amount = c.getString(c.getColumnIndex(KEY_POLICYNO));
				// adding to tags list
				tag.add(new ClientTag(client_no, "", ""));
				// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
			} while (c.moveToNext());
		}
		return tag;
	}
	
	

	public List<PaymentTag> paymenttaginfo() {
		List<PaymentTag> tag = new ArrayList<PaymentTag>();
		/*
		 * String selectQuery = "SELECT " + KEY_POLICYNO + ","+ KEY_CLIENTNO +
		 * ","+ KEY_AMOUNT + " FROM " + TABLE_PAYMENTS;
		 */
		String selectQuery = "SELECT * FROM " + TABLE_PAYMENTS +" ORDER BY "+KEY_PAYMENT_DATE+" DESC";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c.getCount() == 0){
			//return;
			tag.add(new PaymentTag("", "No record found", ""));
			return tag;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// Log.e(LOG, c.getString(c.getColumnIndex(KEY_POLICYNO)));
				
				//Join Policy and name KEY_CLIENT_NAME
				
				
				String policy_no = c.getString(c.getColumnIndex(KEY_POLICYNO)) + " - " + c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				String mid = c.getString(c.getColumnIndex(KEY_ID));
				String amount = c.getString(c.getColumnIndex(KEY_AMOUNT));
				// adding to tags list
				tag.add(new PaymentTag(mid, policy_no, amount));
				// tags.add(c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)),c.getString(c.getColumnIndex(KEY_POLICYNO)));
			} while (c.moveToNext());
		}
		return tag;
	}
	
	public String[] marketlists() {
		String[] tags;
		String[] markets;
		String selectQuery = "SELECT " + KEY_MARKET_NAME + " FROM "
				+ TABLE_MARKETS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		int rows = c.getCount();
		
		markets = new String[rows];
		/*if(c.getCount() == 0){
			//return;
			tags.add("No market found. Please Sync");
			return tags;
		}*/
		int i = 0;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));
				markets[i] =  c.getString(c.getColumnIndex(KEY_MARKET_NAME));
				i++;
				// adding to tags list
				//tags.add(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
			} while (c.moveToNext());
		}
		
		return markets;
		//return tags;
	}

	/**
	 * getting all markets
	 * */
	public List<String> marketlist() {
		List<String> tags = new ArrayList<String>();
		String selectQuery = "SELECT " + KEY_MARKET_NAME + " FROM "
				+ TABLE_MARKETS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c.getCount() == 0){
			//return;
			tags.add("No market found. Please Sync");
			return tags;
		}

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));
				Log.e(LOG, c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
				// adding to tags list
				tags.add(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
			} while (c.moveToNext());
		}
		return tags;
	}

	/*
	 * return scheme id
	 */
	public int idscheme(String s) {
		int id_int = 0;
		// String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES +
		// " WHERE "
		String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES
				+ " WHERE " + KEY_DESCRIPTION + " = '" + s + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				id_int = c.getInt(c.getColumnIndex(KEY_ID));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				Log.e(LOG, "id :" + id_int);
			} while (c.moveToNext());
		}
		Log.e(LOG, "id :" + id_int);
		return id_int;
	}

	/*
	 * return branch id
	 */
	public String idbranch(String s) {
		String id_int = "";
		// String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES +
		// " WHERE "
		String selectQuery = "SELECT " + KEY_BRANCH + " FROM " + TABLE_BRANCH
				+ " WHERE " + KEY_BRANCHNAME + " = '" + s + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				id_int = c.getString(c.getColumnIndex(KEY_BRANCH));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				Log.e(LOG, "id :" + id_int);
			} while (c.moveToNext());
		}
		Log.e(LOG, "id :" + id_int);
		return id_int;
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String idPlan(String string) {
		// TODO Auto-generated method stub
		String id_int = "";
		// String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES +
		// " WHERE "
		String selectQuery = "SELECT " + KEY_PLANCODE + " FROM " + TABLE_PLANS
				+ " WHERE " + KEY_ID + " = '" + string + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				id_int = c.getString(c.getColumnIndex(KEY_PLANCODE));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				Log.e(LOG, "id :" + id_int);
			} while (c.moveToNext());
		}
		Log.e(LOG, "id :" + id_int);
		return id_int;
	}

	public String[] paymentDetails(String[] string) {
		String ClientName = "";
		String PolicyNo = "";
		String Amount = "";
		String PaymentDate = "";
		String PaymentType = "";
		String Market = "";

		String selectQuery = "SELECT " + KEY_ID + "," + KEY_POLICYNO + "," + KEY_CLIENT_NAME
				+ "," + KEY_AMOUNT + "," + KEY_PAYMENT_DATE + ","
				+ KEY_PAYMENT_TYPE + "," + KEY_MARKET_CODE + " FROM "
				+ TABLE_PAYMENTS + " WHERE " + KEY_POLICYNO + " = '"
				+ string[0] + "'" + " AND " + KEY_AMOUNT + " = '" + string[1]
				+ "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// adding to tags list
				ClientName = c.getString(c.getColumnIndex(KEY_CLIENT_NAME));
				PolicyNo = c.getString(c.getColumnIndex(KEY_POLICYNO));
				Amount = c.getString(c.getColumnIndex(KEY_AMOUNT));
				PaymentDate = c.getString(c.getColumnIndex(KEY_PAYMENT_DATE));
				PaymentType = c.getString(c.getColumnIndex(KEY_PAYMENT_TYPE));
				Market = c.getString(c.getColumnIndex(KEY_ID));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				//Log.e(LOG, "id :" + telephone);
			} while (c.moveToNext());
		}
		// Log.e(LOG, "id :" + id_int);

		return new String[] { ClientName, PolicyNo, Amount, PaymentDate,PaymentType, Market };
	}

	public String[] clientDetails(String string) {
		// TODO Auto-generated method stub
		String email = "";
		String telephone = "";
		// String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES +
		// " WHERE "
		String selectQuery = "SELECT " + KEY_EMAIL + "," + KEY_TELEPHONE
				+ " FROM " + TABLE_CLIENTS + " WHERE " + KEY_POLICYNO + " = '"
				+ string + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				email = c.getString(c.getColumnIndex(KEY_EMAIL));
				telephone = c.getString(c.getColumnIndex(KEY_TELEPHONE));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				Log.e(LOG, "id :" + telephone);
			} while (c.moveToNext());
		}
		// Log.e(LOG, "id :" + id_int);
		return new String[] { email, telephone };
	}

	public String idMarket(String string) {
		// TODO Auto-generated method stub
		String id_int = "";
		// String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES +
		// " WHERE "
		String selectQuery = "SELECT " + KEY_MARKET_CODE + " FROM "
				+ TABLE_MARKETS + " WHERE " + KEY_ID + " = '" + string + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				id_int = c.getString(c.getColumnIndex(KEY_MARKET_CODE));
				// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
				Log.e(LOG, "id :" + id_int);
			} while (c.moveToNext());
		}
		Log.e(LOG, "id :" + id_int);
		return id_int;
	}

	public int reg(String string) {
		int reg_i = 0;
		String selectQuery = "SELECT " + KEY_REGISTERED + " FROM " + TABLE_AUTH
				+ " WHERE " + KEY_IMEI + " = '" + string + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.getCount() > 0) {
			if (c.moveToFirst()) {
				do {
					reg_i = c.getInt(c.getColumnIndex(KEY_REGISTERED));
					// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
					Log.e(LOG, "id :" + reg_i);
				} while (c.moveToNext());
			}
		} else {
			reg_i = 2;
		}
		Log.e(LOG, "id :" + reg_i);
		return reg_i;
	}

	public String[] phoneDetails(String imei2) {
		// TODO Auto-generated method stub
		String imeino = "";
		String registered = "";
		String agentno = "";
		// String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SCHEMES +
		// " WHERE "
		String selectQuery = "SELECT " + KEY_IMEI + "," + KEY_REGISTERED + ","
				+ KEY_AGENTNO + " FROM " + TABLE_AUTH + " WHERE " + KEY_IMEI
				+ " = '" + imei2 + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				// ClientTag t = new ClientTag();
				// t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				// t.setFName(c.getString(c.getColumnIndex(KEY_FNAME)));

				// adding to tags list
				imeino = c.getString(c.getColumnIndex(KEY_IMEI));
				registered = c.getString(c.getColumnIndex(KEY_REGISTERED));
				agentno = c.getString(c.getColumnIndex(KEY_AGENTNO));
				Log.e(LOG, "id :" + imeino);
			} while (c.moveToNext());
		}
		// Log.e(LOG, "id :" + id_int);
		return new String[] { imeino, registered, agentno };
	}

	/*
	 * Creating Plans
	 */
	public long register(String agno, String password, String imei) {
		// checkifexists(null);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_REGISTERED, "1");
		values.put(KEY_PASSWORD, password);

		// insert row
		long tag_id = db
				.update(TABLE_AUTH, values, KEY_IMEI + "=" + imei, null);

		return tag_id;
	}

	public Boolean authentification(String agno, String pass) {

		String selectQuery = "SELECT * " // + KEY_AGENTNO + "," + KEY_PASSWORD
				+ " FROM " + TABLE_AUTH + " WHERE " + KEY_PASSWORD
				+ " = '"
				+ pass + " AND " + KEY_PASSWORD + " = " + pass + "'";
		Log.e(LOG, "Query :" + selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor c = db.rawQuery(selectQuery, null);

		Cursor c = db.query(true, TABLE_AUTH, new String[] { KEY_AGENTNO,
				KEY_PASSWORD, KEY_IMEI, }, KEY_AGENTNO + "=?" + " and "
				+ KEY_PASSWORD + "=?", new String[] { agno, pass }, null, null,
				null, null);

		if (c.getCount() > 0) {
			if (c.moveToFirst()) {
				do {
					String reg_i = c.getString(c.getColumnIndex(KEY_AGENTNO));
					// tags.add(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
					Log.e(LOG,
							"agentno from db :"
									+ c.getString(c.getColumnIndex(KEY_AGENTNO)));
					Log.e(LOG,
							"Password from db :"
									+ c.getString(c
											.getColumnIndex(KEY_PASSWORD)));
				} while (c.moveToNext());
			}
			return true;
		} else {
			return false;
		}
	}
}

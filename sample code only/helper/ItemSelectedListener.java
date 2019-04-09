package com.softclans.agents.helper;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ItemSelectedListener implements OnItemSelectedListener {
	
	private static final String LOG = "log";
	DatabaseHelper db;
	public static String idbranch = "";

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
            long id) {
		db = new DatabaseHelper(view.getContext());
		idbranch = db.idbranch(parent.getItemAtPosition(pos).toString());
		Log.e(LOG, "id :" + idbranch);
    }
 
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
 
    }

}

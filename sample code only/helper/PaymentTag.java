package com.softclans.agents.helper;

import org.json.JSONException;
import org.json.JSONObject;
import com.softclans.agents.helper.Globalvar;

import android.util.Log;

public class PaymentTag {

	Globalvar globalVariable;
	String client_no,policy_no,amount,client_name,payment_date,payment_type,systime,market_code,imei,agent_no,mid;
	
	
	
	public PaymentTag(String client_no, String policy_no, String amount,
			String client_name, String payment_date, String payment_type,
			String systime, String market_code, String imei, String agent_no,
			String mid) {
		super();
		this.client_no = client_no;
		this.policy_no = policy_no;
		this.amount = amount;
		this.client_name = client_name;
		this.payment_date = payment_date;
		this.payment_type = payment_type;
		this.systime = systime;
		this.market_code = market_code;
		this.imei = imei;
		this.agent_no = agent_no;
		this.mid = mid;
	}
	
	public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("client_no", client_no);
            obj.put("policy_no", policy_no);
            obj.put("amount", amount);
            obj.put("client_name", client_name);
            obj.put("payment_date", payment_date);
            obj.put("payment_type", payment_type);
            obj.put("systime", systime);
            obj.put("market_code", market_code);
            obj.put("imei", imei);
            obj.put("agent_no", agent_no);
            obj.put("mid", mid);
        } catch (JSONException e) {
            Log.e("jsobj",("DefaultListItem.toString JSONException: "+e.getMessage()));
        }
        return obj;
    }
	
	public PaymentTag(String client_no, String policy_no, String amount,
			String client_name, String payment_date, String payment_type,
			String systime, String market_code, String imei, String agent_no) {
		super();
		this.client_no = client_no;
		this.policy_no = policy_no;
		this.amount = amount;
		this.client_name = client_name;
		this.payment_date = payment_date;
		this.payment_type = payment_type;
		this.systime = systime;
		this.market_code = market_code;
		this.imei = imei;
		this.agent_no = agent_no;
	}
	public PaymentTag(String client_no,String policy_no, String amount) {
		super();
		this.amount = amount;
		this.client_no = client_no;
		this.policy_no = policy_no;
	}
	
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getClient_no() {
		return client_no;
	}

	public void setClient_no(String client_no) {
		this.client_no = client_no;
	}

	public String getPolicy_no() {
		return policy_no;
	}

	public void setPolicy_no(String policy_no) {
		this.policy_no = policy_no;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getClient_name() {
		return client_name;
	}
	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
	public String getPayment_date() {
		return payment_date;
	}
	public void setPayment_date(String payment_date) {
		this.payment_date = payment_date;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getSystime() {
		return systime;
	}
	public void setSystime(String systime) {
		this.systime = systime;
	}
	public String getMarket_code() {
		return market_code;
	}
	public void setMarket_code(String market_code) {
		this.market_code = market_code;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getAgent_no() {
		return agent_no;
	}
	public void setAgent_no(String agent_no) {
		this.agent_no = agent_no;
	}

}

package com.softclans.agents.helper;

public class ClientTag {
	String client_no,policy_no,client,email,telephone;

	public ClientTag(String client_no,String policy_no, String client) {
		super();
		this.client = client;
		this.client_no = client_no;
		this.policy_no = policy_no;
	}
	
	public ClientTag(String client_no, String policy_no, String client,
			String email, String telephone) {
		super();
		this.client_no = client_no;
		this.policy_no = policy_no;
		this.client = client;
		this.email = email;
		this.telephone = telephone;
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

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}

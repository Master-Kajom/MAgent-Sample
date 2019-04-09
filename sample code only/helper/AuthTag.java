package com.softclans.agents.helper;

public class AuthTag {
	
	public AuthTag(String imei, String agentno, String password,
			String registered, String initial) {
		super();
		this.imei = imei;
		this.agentno = agentno;
		this.password = password;
		this.registered = registered;
		this.initial = initial;
	}

	String imei,agentno,password,registered,initial;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAgentno() {
		return agentno;
	}

	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegistered() {
		return registered;
	}

	public void setRegistered(String registered) {
		this.registered = registered;
	}

	public String getInitial() {
		return initial;
	}

	public void setInitial(String initial) {
		this.initial = initial;
	}

}

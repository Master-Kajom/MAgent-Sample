package com.softclans.agents.helper;

public class MarketTag {
	String market_name,market_code;

	public MarketTag(String market_name, String market_code) {
		super();
		this.market_name = market_name;
		this.market_code = market_code;
	}
	
	public String getMarket_name() {
		return market_name;
	}

	public void setMarket_name(String market_name) {
		this.market_name = market_name;
	}

	public String getMarket_code() {
		return market_code;
	}

	public void setMarket_code(String market_code) {
		this.market_code = market_code;
	}

}

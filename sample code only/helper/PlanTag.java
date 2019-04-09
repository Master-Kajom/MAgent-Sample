package com.softclans.agents.helper;

public class PlanTag {
	String description,plan_code;

	public PlanTag(String description, String plan_code) {
		super();
		this.description = description;
		this.plan_code = plan_code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlan_code() {
		return plan_code;
	}

	public void setPlan_code(String plan_code) {
		this.plan_code = plan_code;
	}

}

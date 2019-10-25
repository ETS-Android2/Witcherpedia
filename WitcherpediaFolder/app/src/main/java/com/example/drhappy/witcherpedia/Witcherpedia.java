package com.example.drhappy.witcherpedia;

import android.app.Application;

public class Witcherpedia extends Application {

	String current_view;
	String faction_selected;
	//String desc_selected;

	public Witcherpedia() {
		super();

		this.current_view = "Witcherpedia";
		this.faction_selected = "";
		//this.desc_selected = "";
	}

	public String getCurrent_view() {
		return current_view;
	}

	public void setCurrent_view(String current_view) { this.current_view = current_view; }

	public String getFaction_selected() {
		return faction_selected;
	}

	public void setFaction_selected(String faction_selected) { this.faction_selected = faction_selected; }

	//public String getDesc_selected() { return desc_selected; }

	//public void setDesc_selected(String desc_selected) { this.desc_selected = desc_selected; }

}

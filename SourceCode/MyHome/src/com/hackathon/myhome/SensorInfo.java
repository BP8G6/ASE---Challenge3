package com.hackathon.myhome;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("SensorInfo")
public class SensorInfo extends IBMDataObject{

	public static final String CLASS_NAME = "Item";
	private static final String NAME = "name";
	private static final String STATUS = "status";
	private static final String ID = "id";
	private static final String TYPE = "type";

	//SensorID - 100, 101, 102
	//Time? time at which event occurred
	//Type = 1-door, 2-window, 3-light
	
	public String getName() {
		return (String) getObject(NAME);
	}
	public String getStatus() {
		return (String) getObject(STATUS);
	}
	
	public String getId() {
		return (String) getObject(ID);
	}
	
	public String getType() {
		return (String) getObject(TYPE);
	}

	public void setName(String itemName) {
		setObject(NAME, (itemName != null) ? itemName : "");
	}

	public void setStatus(String itemValue) {
		setObject(STATUS, (itemValue != null) ? itemValue : "");
	}
	
	public void setId(String itemValue) {
		setObject(ID, (itemValue != null) ? itemValue : "");
	}
	
	public void setType(String itemValue) {
		setObject(TYPE, (itemValue != null) ? itemValue : "");
	}
	
	public String toString() {
		String theItemName = "";
		theItemName = getName();
		return theItemName;
	}

}
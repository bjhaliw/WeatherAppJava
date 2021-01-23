package model;

import java.util.ArrayList;


public class WeatherGridInformation {

	private String uom;
	private ArrayList<WeatherValues> values;
	
	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}
	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}
	/**
	 * @return the values
	 */
	public ArrayList<WeatherValues> getValues() {
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(ArrayList<WeatherValues> values) {
		this.values = values;
	}
	
	
	
	
}

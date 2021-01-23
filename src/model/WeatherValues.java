package model;

/**
 * Created with JSON file
 * @author Brenton Haliw
 *
 */
public class WeatherValues {
	private String validTime;
	private double value;
	private int intValue;
	
	/**
	 * @return the validTime
	 */
	public String getValidTime() {
		return validTime;
	}
	/**
	 * @param validTime the validTime to set
	 */
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	/**
	 * @return the raw value which is a double
	 */
	public double getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
	/**
	 * @return the intValue that might have been converted
	 */
	public int getIntValue() {
		return intValue;
	}
	/**
	 * @param intValue the intValue to set
	 */
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	
	
}

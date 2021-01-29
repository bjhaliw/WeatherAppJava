package model;

/**
 * This class is created when reading the NWS JSON files. Allows for the GUI to
 * display the weather details associated with a period during the day.
 * 
 * @author Brenton Haliw
 *
 */
public class WeatherPeriod {

	private String number, name, startTime, endTime;
	private String temperature, temperatureUnit;
	private String windSpeed, windDirection;
	private String shortForecast, detailedForecast;
	private String icon;
	private boolean isDayTime;

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the temperature
	 */
	public String getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the temperatureUnit
	 */
	public String getTemperatureUnit() {
		return temperatureUnit;
	}

	/**
	 * @param temperatureUnit the temperatureUnit to set
	 */
	public void setTemperatureUnit(String temperatureUnit) {
		this.temperatureUnit = temperatureUnit;
	}

	/**
	 * @return the windSpeed
	 */
	public String getWindSpeed() {
		return windSpeed;
	}

	/**
	 * @param windSpeed the windSpeed to set
	 */
	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}

	/**
	 * @return the windDirection
	 */
	public String getWindDirection() {
		return windDirection;
	}

	/**
	 * @param windDirection the windDirection to set
	 */
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	/**
	 * @return the shortForecast
	 */
	public String getShortForecast() {
		return shortForecast;
	}

	/**
	 * @param shortForecast the shortForecast to set
	 */
	public void setShortForecast(String shortForecast) {
		this.shortForecast = shortForecast;
	}

	/**
	 * @return the detailedForecast
	 */
	public String getDetailedForecast() {
		return detailedForecast;
	}

	/**
	 * @param detailedForecast the detailedForecast to set
	 */
	public void setDetailedForecast(String detailedForecast) {
		this.detailedForecast = detailedForecast;
	}

	/**
	 * @return the isDayTime
	 */
	public boolean isDayTime() {
		return isDayTime;
	}

	/**
	 * @param isDayTime the isDayTime to set
	 */
	public void setDayTime(boolean isDayTime) {
		this.isDayTime = isDayTime;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

}

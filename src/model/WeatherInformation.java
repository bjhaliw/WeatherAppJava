package model;

import java.util.ArrayList;
import java.util.HashMap;

import errors.WeatherJsonError;

/**
 * The WeatherInformation class is responsible for keeping track
 * of the details pulled from the NWS json files.
 * @author Brenton Haliw
 *
 */
/**
 * @author Brenton
 *
 */
public class WeatherInformation {

	// API for geocoding the locations passed in
	public static final String GEOCODE_URL = "https://www.mapquestapi.com/geocoding/v1/address?key=" + Config.API_KEY
			+ "&";

	// API for initial call. Requires the lat/long from the MapQuest API
	public static final String NWS_API_URL = "https://api.weather.gov/points/";

	// NWS website for scraping purposes to easily get current information
	public static final String NWS_URL = "https://forecast.weather.gov/MapClick.php?";

	// Stores the JSON created WeatherPeriod class containing relevant information
	private ArrayList<WeatherPeriod> hourlyPeriods, detailedPeriods;

	// Map containing the objects created by the NWS json files
	HashMap<String, WeatherGridInformation> valuesMap;

	private String currentCondition, currentTemp;
	private String currentLocation, timeZone;

	/**
	 * Constructor for the WeatherInformation class. Takes in a String representing
	 * the location that the user wants to get the weather of
	 * 
	 * @param location - String representing the user's requested location
	 * @throws WeatherJsonError
	 */
	public WeatherInformation(String location) throws WeatherJsonError {
		this.valuesMap = new HashMap<>();
		this.hourlyPeriods = new ArrayList<>();
		this.detailedPeriods = new ArrayList<>();

		if (location != null && !location.equals("")) {
			// Access API for MapQuest and NWS and return the latlong location
			JSONReader.manipulateJSON(this, location);
		}
	}

	/**
	 * @return the currentLocation
	 */
	public String getCurrentLocation() {
		return this.currentLocation;
	}

	/**
	 * @param currentLocation the currentLocation to set
	 */
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	/**
	 * @return the currentCondition
	 */
	public String getCurrentCondition() {
		return this.currentCondition;
	}

	/**
	 * @return the currentTemp
	 */
	public String getCurrentTemp() {
		return this.currentTemp;
	}

	/**
	 * @return the hourlyPeriods
	 */
	public ArrayList<WeatherPeriod> getHourlyPeriods() {
		return this.hourlyPeriods;
	}

	/**
	 * @return the detailedPeriods
	 */
	public ArrayList<WeatherPeriod> getDetailedPeriods() {
		return this.detailedPeriods;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return this.timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the valuesMap
	 */
	public HashMap<String, WeatherGridInformation> getValuesMap() {
		return valuesMap;
	}

}

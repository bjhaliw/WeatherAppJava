package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

	// Holds the current weather conditions so that they can be easily displayed
	private HashMap<String, String> currentWeatherDetail;

	// Stores the JSON created WeatherPeriod class containing relevant information
	private ArrayList<WeatherPeriod> hourlyPeriods, detailedPeriods;

	private ArrayList<String> extendedForecastStrings;
	private String currentCondition, currentTemp;
	private String currentLocation, timeZone;
	private WeatherGridInformation weatherGridInformation;

	/**
	 * Constructor for the WeatherInformation class. Takes in a String representing
	 * the location that the user wants to get the weather of
	 * 
	 * @param location - String representing the user's requested location
	 * @throws WeatherJsonError 
	 */
	public WeatherInformation(String location) throws WeatherJsonError {
		this.currentWeatherDetail = new HashMap<>();
		this.hourlyPeriods = new ArrayList<>();
		this.detailedPeriods = new ArrayList<>();
		this.weatherGridInformation= new WeatherGridInformation();

		// Make sure the TextField from the GUI wasn't empty
		if (location != null && !location.equals("")) {
			updateWeather(location); // Starts the GET process
		} else {
			System.out.println("Please enter a valid location");
		}
	}

	/**
	 * Gets the current weather conditions for the current zipcode
	 * @throws WeatherJsonError 
	 */
	public void updateWeather(String location) throws WeatherJsonError {
		Document document;
		try {
			// Access API for MapQuest and NWS and return the latlong location
			String latLong = JSONReader.manipulateJSON(this, location);

			// Getting the Current Weather Information by scraping the NWS website //
			String[] latLongList = latLong.split(",");
			String append = "lat=" + latLongList[0] + "&lon=" + latLongList[1];

			// Connect to the NWS website and scrape the data
			document = Jsoup.connect(NWS_URL + append).get();
			System.out.println(document.toString());
			this.getCurrentWeatherSummary(document);
			this.getCurrentWeatherDetail(document);
			this.extendedForecastStrings = getExtendedForecastedWeather(document);
			this.currentLocation = document.select("h2[class=panel-title]").get(1).text();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets extended forecasted weather, which is normally about four days worth and
	 * is split up between day and night. Includes the weather condition for that
	 * time period and its respective high/low
	 * 
	 * @param document - Jsoup Document containing HTML code
	 * @return - ArrayList<String> containing the forecasted weather
	 */
	private static ArrayList<String> getExtendedForecastedWeather(Document document) {
		// Storing as a list to make it easier to use regular expressions to
		// get the information that we want afterwards
		ArrayList<String> list = new ArrayList<>();
		Element forecastID = document.getElementById("seven-day-forecast-body");
		Elements forecastParagraphs = forecastID.select("li"); // Information is in a list

		System.out.println("\nExtended Forecast");
		for (Element e : forecastParagraphs) {
			list.add(e.text());
		}

		for (String string : list) {
			System.out.println(string);
		}

		return list;
	}

	/**
	 * Gets the current temperature in fahrenheit and celsius, and the current
	 * weather condition (foggy, rainiy, etc.)
	 * 
	 * @param document - Jsoup Document containing the HTML code
	 */
	private void getCurrentWeatherSummary(Document document) {
		Element summaryID = document.getElementById("current_conditions-summary");
		Elements summaryParagraphs = summaryID.select("p"); // Information in paragraphs

		this.currentCondition = summaryParagraphs.get(0).text();
		this.currentTemp = summaryParagraphs.get(1).text() + " (" + summaryParagraphs.get(2).text() + ")";
	}

	/**
	 * Gets humidity, wind speed, visibility, barometric pressure, dewpoint, heat
	 * index/wind chill, and the date/time the information was updated
	 * 
	 * @param document - Jsoup Document containing HTML code
	 */
	private void getCurrentWeatherDetail(Document document) {
		Element detailID = document.getElementById("current_conditions_detail");
		Elements detailParagraphs = detailID.select("td"); // Information is in a table

		for (Element e : detailParagraphs) {
			System.out.println(e.text());
		}

		for (int i = 0; i < detailParagraphs.size(); i += 2) {

			this.currentWeatherDetail.put(detailParagraphs.get(i).text(), detailParagraphs.get(i + 1).text());
		}
	}

	/**
	 * @return the currentWeatherDetail
	 */
	public HashMap<String, String> getCurrentWeatherDetail() {
		return currentWeatherDetail;
	}

	/**
	 * @return the currentCondition
	 */
	public String getCurrentCondition() {
		return currentCondition;
	}

	/**
	 * @return the currentTemp
	 */
	public String getCurrentTemp() {
		return currentTemp;
	}

	/**
	 * @return the currentLocation
	 */
	public String getCurrentLocation() {
		return this.currentLocation;
	}

	/**
	 * @return the hourlyPeriods
	 */
	public ArrayList<WeatherPeriod> getHourlyPeriods() {
		return hourlyPeriods;
	}

	/**
	 * @return the detailedPeriods
	 */
	public ArrayList<WeatherPeriod> getDetailedPeriods() {
		return detailedPeriods;
	}

	/**
	 * @return the extendedForecastStrings
	 */
	public ArrayList<String> getExtendedForecastStrings() {
		return extendedForecastStrings;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the weatherGridInformation
	 */
	public WeatherGridInformation getWeatherGridInformation() {
		return weatherGridInformation;
	}	

}
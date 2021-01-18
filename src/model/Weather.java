package model;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Weather {

	// API for geocoding the locations passed in
	public static final String GEOCODE = "https://www.mapquestapi.com/geocoding/v1/address?key=" + Config.API_KEY + "&"; 
	
	public static final String URL = "https://www.weather.gov/";
	private HashMap<String, String> currentWeatherDetail;
	private ArrayList<String> simpleForecast, detailedForecast;
	private String currentCondition, currentTemp;
	private String locationQuery, currentLocation;

	public Weather(String location) {
		this.currentWeatherDetail = new HashMap<>();
		this.simpleForecast = new ArrayList<>();
		this.detailedForecast = new ArrayList<>();
		if (location != null && !location.equals("")) {
			this.locationQuery = location;
			updateWeather();
		} else {
			System.out.println("Please enter a zipcode");
		}
	}

	/**
	 * Gets the current weather conditions for the current zipcode
	 */
	public void updateWeather() {
		Document document;
		try {
			System.out.println(URL + this.locationQuery);
			
			String latLong = getLatLong(this.locationQuery);
			
			System.out.println(latLong);
			
			document = Jsoup.connect(URL + this.locationQuery).get();
			System.out.println(document.toString());
			this.getCurrentWeatherSummary(document);
			this.getCurrentWeatherDetail(document);
			this.getDetailedForecastedWeather(document);
			this.getExtendedForecastedWeather(document);
			this.currentLocation = document.select("h2[class=panel-title]").get(1).text();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private String getLatLong(String location) {
		String url = GEOCODE + "outFormat=xml&location=" + location;
		
		try {
			Document document = Jsoup.connect(url).get();
			
			String lat = document.getElementsByTag("lat").get(0).text();
			String lng = document.getElementsByTag("lng").get(0).text();
			
			double latNum = Double.parseDouble(lat);
			double lngNum = Double.parseDouble(lng);
			
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.CEILING);
			
			lat = df.format(latNum);
			lng = df.format(lngNum); 
			
			return lat + "," + lng;			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return null;
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

		// Change this to a switch statement to initialize the instance variables
		// instead.
		// Will be way easier
		for (int i = 0; i < detailParagraphs.size(); i += 2) {

			this.currentWeatherDetail.put(detailParagraphs.get(i).text(), detailParagraphs.get(i + 1).text());
		}
	}

	/**
	 * Gets extended forecasted weather, which is normally about four days worth and
	 * is split up between day and night. Includes the weather condition for that
	 * time period and its respective high/low
	 * 
	 * @param document - Jsoup Document containing HTML code
	 */
	private void getExtendedForecastedWeather(Document document) {
		Element forecastID = document.getElementById("seven-day-forecast-body");
		Elements forecastParagraphs = forecastID.select("li"); // Information is in a list

		System.out.println("\nExtended Forecast");
		for (Element e : forecastParagraphs) {
			this.simpleForecast.add(e.text());
		}
	}

	/**
	 * Gets the detailed forecast for the weather. This is the seven day format that
	 * is usually a bit more vague since the weather is further out at this point.
	 * 
	 * @param document - Jsoup Document containing HTML code
	 */
	private void getDetailedForecastedWeather(Document document) {
		Elements label = document.select("div[class=col-sm-2 forecast-label]");
		Elements text = document.select("div[class=col-sm-10 forecast-text]");

		for (int i = 0; i < text.size(); i++) {
			this.detailedForecast.add(label.get(i).text() + ": " + text.get(i).text());
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
	 * @return the simpleForecast
	 */
	public ArrayList<String> getSimpleForecast() {
		return simpleForecast;
	}

	/**
	 * @return the detailedForecast
	 */
	public ArrayList<String> getDetailedForecast() {
		return detailedForecast;
	}

	/**
	 * Sets the Zipcode
	 * 
	 * @param zipcode - String to represent user's zipcode
	 */
	public void setZipcode(String zipcode) {
		this.locationQuery = zipcode;
	}

	public String getCurrentLocation() {
		return this.currentLocation;
	}

}

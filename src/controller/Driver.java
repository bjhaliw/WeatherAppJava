package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Driver {

	public static void main(String[] args) {
		String url = "https://www.weather.gov/21122";

		try {
			Document document = Jsoup.connect(url).get();
			System.out.println(document.toString());

			System.out.println("\nWeather Information Location: " + document.select("h2[class=panel-title]").get(0).text());
			getCurrentWeatherSummary(document);
			getCurrentWeatherDetail(document);
			getExtendedForecastedWeather(document);
			getDetailedForecastedWeather(document);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the current temperature in fahrenheit and celsius, and the current
	 * weather condition (foggy, rainiy, etc.)
	 * 
	 * @param document - Jsoup Document containing the HTML code
	 * @return - HashMap<String, String> containing the required information. The
	 *         key is the type of weather detail and the value is the current
	 *         information.
	 */
	private static HashMap<String, String> getCurrentWeatherSummary(Document document) {
		HashMap<String, String> map = new HashMap<>();

		Element summaryID = document.getElementById("current_conditions-summary");
		Elements summaryParagraphs = summaryID.select("p"); // Information in paragraphs

		map.put("Current Condition", summaryParagraphs.get(0).text());
		map.put("Current Temperature", summaryParagraphs.get(1).text() + " (" + summaryParagraphs.get(2).text() + ")");
		map.put("Current Temp F", summaryParagraphs.get(1).text());
		map.put("Current Temp C", summaryParagraphs.get(2).text());

		System.out.println("\nCurrent Condition and Temperature");
		for (String string : map.keySet()) {
			System.out.println("Key: " + string + ", Value: " + map.get(string));
		}

		return map;
	}

	/**
	 * Gets humidity, wind speed, visibility, barometric pressure, dewpoint, heat
	 * index/wind chill, and the date/time the information was updated
	 * 
	 * @param document - Jsoup Document containing HTML code
	 * @return - HashMap<String, String> containing the required information. The
	 *         key is the type of weather detail and the value is the current
	 *         information.
	 */
	private static HashMap<String, String> getCurrentWeatherDetail(Document document) {
		HashMap<String, String> map = new HashMap<>();
		Element detailID = document.getElementById("current_conditions_detail");
		Elements detailParagraphs = detailID.select("td"); // Information is in a table

		for (int i = 0; i < detailParagraphs.size(); i += 2) {
			map.put(detailParagraphs.get(i).text(), detailParagraphs.get(i + 1).text());
		}

		System.out.println("\nDetails");

		for (String string : map.keySet()) {
			System.out.println("Key: " + string + ", Value: " + map.get(string));
		}

		return map;
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
	
	public static ArrayList<String> getDetailedForecastedWeather(Document document) {
		ArrayList<String> list = new ArrayList<>();
		Elements label = document.select("div[class=col-sm-2 forecast-label]");
		Elements text = document.select("div[class=col-sm-10 forecast-text]");		
		
		for (int i = 0; i < text.size(); i++) {
			list.add(label.get(i).text() + ": " + text.get(i).text());
		}
		
		System.out.println("\nDetailed Forecast");
		for (String string : list) {
			System.out.println(string);
		}
		
		return list;
	}

}

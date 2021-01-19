package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import errors.WeatherJsonError;
import javafx.concurrent.Task;
import javafx.scene.Parent;

public class JSONReader {

	/**
	 * Starts the process of reading the json files associated with the NWS API
	 * 
	 * @param weather  - WeatherInformation object
	 * @param location - Requested location
	 * @return - String representation of the Latitude,Longitude
	 * @throws WeatherJsonError
	 */

	public static String manipulateJSON(WeatherInformation weather, String location) throws WeatherJsonError {
		Gson gson = new Gson();

		// Gets the LatLong with the MapQuest API String latLong =
		String latLong = getLatLong(location);

		// Use NWS API to get initial JSON // String initialJson =
		String initialJson = readUrl(WeatherInformation.NWS_API_URL + latLong);
		JsonObject obj = JsonParser.parseString(initialJson).getAsJsonObject();
		JsonObject properties = obj.getAsJsonObject("properties");

		// Getting the Time Zone to change the GUI Clock // JsonPrimitive timeZone =
		JsonPrimitive timeZone = properties.getAsJsonPrimitive("timeZone");
		String timeZoneString = gson.fromJson(timeZone, String.class);
		weather.setTimeZone(timeZoneString);

		// Getting the Regular Forecast for the requested location // JsonPrimitive
		JsonPrimitive regForecast = properties.getAsJsonPrimitive("forecast");
		String regForecastURL = gson.fromJson(regForecast, String.class);
		jsonHourlyAndRegularForecast(weather.getDetailedPeriods(), regForecastURL);

		// Getting the Hourly Forecast for the requested location // JsonPrimitive
		JsonPrimitive hourlyForecast = properties.getAsJsonPrimitive("forecastHourly");
		String hourlyForecastURL = gson.fromJson(hourlyForecast, String.class);
		jsonHourlyAndRegularForecast(weather.getHourlyPeriods(), hourlyForecastURL);

		// Getting Forecast Grid Data for the requested location // JsonPrimitive
		JsonPrimitive gridForecast = properties.getAsJsonPrimitive("forecastGridData");
		String gridForecastURL = gson.fromJson(gridForecast, String.class);
		jsonGridForecast(weather, gridForecastURL);

		return latLong;
	}

	/**
	 * Responsible for reading the Grid Forecast json for the required location
	 * 
	 * @param list
	 * @param url  - url to the JSON file
	 */
	private static void jsonGridForecast(WeatherInformation weather, String url) {
		String json = readUrl(url);
		Gson gson = new Gson();
		// Create the overall object for the .json file
		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

		// Create an object based on the .json properties object
		JsonObject properties = obj.getAsJsonObject("properties");

		// Create object based on the required weather details
		JsonObject temperature = properties.getAsJsonObject("temperature");
		JsonObject dewpoint = properties.getAsJsonObject("dewpoint");
		JsonObject maxTemperature = properties.getAsJsonObject("maxTemperature");
		JsonObject minTemperature = properties.getAsJsonObject("minTemperature");
		JsonObject relativeHumidity = properties.getAsJsonObject("relativeHumidity");
		JsonObject apparentTemperature = properties.getAsJsonObject("apparentTemperature");
		JsonObject heatIndex = properties.getAsJsonObject("heatIndex");
		JsonObject windChill = properties.getAsJsonObject("windChill");
		JsonObject windDirection = properties.getAsJsonObject("windDirection");
		JsonObject windSpeed = properties.getAsJsonObject("windSpeed");
		JsonObject windGust = properties.getAsJsonObject("windGust");
		JsonObject probabilityOfPrecipitation = properties.getAsJsonObject("probabilityOfPrecipitation");

		// Load the WeatherInformation WeatherGridInformation instance variable with the
		// related values
		weather.getWeatherGridInformation().forecastedTemp = gson.fromJson(temperature,
				WeatherGridInformation.ForecastedTemp.class);
		weather.getWeatherGridInformation().forecastedDewpoint = gson.fromJson(dewpoint,
				WeatherGridInformation.ForecastedDewpoint.class);
		weather.getWeatherGridInformation().dailyTemperatureHigh = gson.fromJson(maxTemperature,
				WeatherGridInformation.DailyTemperatureHigh.class);
		weather.getWeatherGridInformation().dailyTemperatureLow = gson.fromJson(minTemperature,
				WeatherGridInformation.DailyTemperatureLow.class);
		weather.getWeatherGridInformation().forecastedHumidity = gson.fromJson(relativeHumidity,
				WeatherGridInformation.ForecastedHumidity.class);
		weather.getWeatherGridInformation().forecastedApparentTemp = gson.fromJson(apparentTemperature,
				WeatherGridInformation.ForecastedApparentTemp.class);
		weather.getWeatherGridInformation().forecastedHeatIndex = gson.fromJson(heatIndex,
				WeatherGridInformation.ForecastedHeatIndex.class);
		weather.getWeatherGridInformation().forecastedWindChill = gson.fromJson(windChill,
				WeatherGridInformation.ForecastedWindChill.class);
		weather.getWeatherGridInformation().forecastedWindDirection = gson.fromJson(windDirection,
				WeatherGridInformation.ForecastedWindDirection.class);
		weather.getWeatherGridInformation().forecastedWindSpeed = gson.fromJson(windSpeed,
				WeatherGridInformation.ForecastedWindSpeed.class);
		weather.getWeatherGridInformation().forecastedWindGust = gson.fromJson(windGust,
				WeatherGridInformation.ForecastedWindGust.class);
		weather.getWeatherGridInformation().forecastedPreciptiationChance = gson.fromJson(probabilityOfPrecipitation,
				WeatherGridInformation.ForecastedPreciptiationChance.class);
	}

	/**
	 * Responsible for reading the Hourly and Regular forecasts for the required
	 * location
	 * 
	 * @param list - ArrayList for the requested forecast
	 * @param url  - url to the JSON file
	 * @throws WeatherJsonError
	 */
	private static void jsonHourlyAndRegularForecast(ArrayList<WeatherPeriod> list, String url)
			throws WeatherJsonError {
		String json = readUrl(url);
		Gson gson = new Gson();
		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

		JsonObject properties = obj.getAsJsonObject("properties");

		if (properties != null) {
			JsonArray periods = properties.getAsJsonArray("periods");

			// System.out.println("Periods: " + periods);

			for (int i = 0; i < periods.size(); i++) {
				JsonObject curr = periods.get(i).getAsJsonObject();

				WeatherPeriod period = gson.fromJson(curr, WeatherPeriod.class);
				list.add(period);
			}

		} else { // An error occured trying to read the json file
			System.out.println("An Error has occured trying to access Hourly/Regular Forecast data");

			JsonPrimitive titleJson = obj.getAsJsonPrimitive("title");
			JsonPrimitive statusJson = obj.getAsJsonPrimitive("status");
			JsonPrimitive detailJson = obj.getAsJsonPrimitive("detail");

			String title = gson.fromJson(titleJson, String.class);
			String status = gson.fromJson(statusJson, String.class);
			String detail = gson.fromJson(detailJson, String.class);

			System.out.println("Title: " + title + ", Status: " + status + ", Detail: " + detail);

			throw new WeatherJsonError("Title: " + title + ", Status: " + status + ", Detail: " + detail);
		}

	}

	/**
	 * Opens the requested website and returns the String representation of it to be
	 * used for JSON manipulation
	 * 
	 * @param urlString - Website to be accessed
	 * @return - String representing the website's contents
	 */
	private static synchronized String readUrl(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

			BufferedReader br = null;
			if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			String output = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				output += line + "\n";
			}

			// System.out.println(output);

			br.close();
			conn.disconnect();

			return output;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Utilizes the MapQuest API to geocode the requested location
	 * 
	 * @param location - String of the requested location
	 * @return - String in form: lat,long
	 */
	private static String getLatLong(String location) {
		String url = WeatherInformation.GEOCODE_URL + "outFormat=xml&location=" + location;

		try {
			Document document = Jsoup.connect(url).get();

			// Getting the lat/long from the Jsoup Document
			String lat = document.getElementsByTag("lat").get(0).text();
			String lng = document.getElementsByTag("lng").get(0).text();

			// Converting the above strings to primitive doubles
			double latNum = Double.parseDouble(lat);
			double lngNum = Double.parseDouble(lng);

			// Creating a format to remove unneeded decimals
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.CEILING);

			// Saving the output lat and long
			lat = df.format(latNum);
			lng = df.format(lngNum);

			return lat + "," + lng;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

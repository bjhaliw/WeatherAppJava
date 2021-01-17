package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.WeatherPeriod;

public class TestDriver {

	public static void main(String[] args) throws Exception {
		String json = readUrl("https://api.weather.gov/gridpoints/LWX/112,82/forecast");

		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
		JsonObject properties = obj.getAsJsonObject("properties");
		JsonArray periods = properties.getAsJsonArray("periods");

		System.out.println("Periods: " + periods);
		ArrayList<WeatherPeriod> weatherPeriods = new ArrayList<>();
		
		Gson gson = new Gson();
		for (int i = 0; i < periods.size(); i++) {
			JsonObject curr = periods.get(i).getAsJsonObject();

			WeatherPeriod period = gson.fromJson(curr, WeatherPeriod.class);
			weatherPeriods.add(period);
		}
		
		for (WeatherPeriod currPer : weatherPeriods) {
			System.out.println("Name of Period: " + currPer.getName());
		}
		
		
	}

	private static String readUrl(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
			
			System.out.println(output);
			
			br.close();
			conn.disconnect();
			
			return output;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

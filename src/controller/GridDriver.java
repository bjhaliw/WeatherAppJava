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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import model.WeatherGridInformation;
import model.WeatherPeriod;
import model.WeatherValues;




public class GridDriver extends Application {

	public class WeatherTemp  {
		String validTime; // Time in some weird format
		double value; // Celsius. Need to convert to F after for Americans
		
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		String json = readUrl("https://api.weather.gov/gridpoints/LWX/111,82");
		
		// Create the overall object for the .json file
		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
		
		// Create an object based on the .json properties object
		JsonObject properties = obj.getAsJsonObject("properties");
		
		// Create object based on
		JsonObject temperature = properties.getAsJsonObject("temperature");
		
		// Create an ArrayList containing WeatherTemp objects
		ArrayList<WeatherTemp> weatherTemps = new ArrayList<>();
		
		Gson gson = new Gson();	
		
		WeatherGridInformation.ForecastedTemp temps = gson.fromJson(temperature, WeatherGridInformation.ForecastedTemp.class);		
		
		System.out.println("Temperature");
		System.out.println(temps.uom);		
		for (WeatherValues value : temps.values) {
			System.out.println("Valid Time: " + value.getValidTime() + ", Value: " + value.getValue());
		}
		
		System.out.println("\nDew Point");
		
		JsonObject dewpoint = properties.getAsJsonObject("dewpoint");
		WeatherGridInformation.ForecastedDewpoint dew = gson.fromJson(dewpoint, WeatherGridInformation.ForecastedDewpoint.class);
		for (WeatherValues value : dew.values) {
			System.out.println("Valid Time: " + value.getValidTime() + ", Value: " + value.getValue());
		}
				
		
		
        Scene scene  = new Scene(makeAreaChart(weatherTemps));
        stage.setScene(scene);
        stage.show();
	}
	
	public AreaChart<String, Number> makeAreaChart(ArrayList<WeatherTemp> list) {
		final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
		AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
		
		XYChart.Series seriesTemps= new XYChart.Series();
        seriesTemps.setName("Temperature");
        
        for (int i = 0; i < list.size(); i++) {
        	WeatherTemp temp = list.get(i);
        	seriesTemps.getData().add(new XYChart.Data(temp.validTime, temp.value));
        }
        
        chart.getData().addAll(seriesTemps);
        
        return chart;
	}
	
	public static void main(String[] args) {
		launch(args);
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
			
			//System.out.println(output);

			
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

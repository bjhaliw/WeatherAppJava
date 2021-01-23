package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.WeatherGridInformation;
import model.WeatherValues;

public class TestGraphs extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		String url = "C:\\Users\\Brenton\\Desktop\\forecastgrid.json";
		HashMap<String, WeatherGridInformation> map = new HashMap<>();
		jsonGridForecast(map, url);

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		int counter = 0;
		boolean breakOut = false;
		for (int i = 0; i < map.get("temperature").getValues().size(); i++) {
			if (breakOut) {
				break;
			}

			WeatherValues values = map.get("temperature").getValues().get(i);
			System.out.println(values.getIntValue());
			// Getting the duration that the forecast value is good for
			String durationString = values.getValidTime().substring(values.getValidTime().indexOf("/") + 1);
			Duration dur = Duration.parse(durationString);
			int hours = (int) dur.toHours(); // How many hours the forecast is good for

			// Getting the current value time
			String time = values.getValidTime().substring(0, values.getValidTime().indexOf("+"));
			time = time.substring(0, time.lastIndexOf(":"));
			time = time.replace("T", " ");
			LocalDateTime date = LocalDateTime.from(df.parse(time));
			System.out.println("Current Date is: " + date.toString());

			// While the forecasted values are good
			while (hours > 0) {
				series.getData().add(new XYChart.Data(date.toString(), values.getIntValue()));
				date = date.plusHours(1);
				System.out.println("Date loop: " + date.toString());
				hours--;
				counter++;
				if (counter > 24) {
					breakOut = true;
					break;
				}
			}

		}

		int maxValue = Integer.MIN_VALUE;
		int minValue = Integer.MAX_VALUE;
		for (Data<String, Number> data : series.getData()) {

			data.nodeProperty().addListener(e -> {
				displayLabelForData(data);
			});
			int f = (int) data.getYValue();

			if (f > maxValue) {
				maxValue = f;
			} else if (f < minValue) {
				minValue = f;
			}
		}

		if (maxValue != Double.MIN_VALUE && minValue != Double.MAX_VALUE) {
			yAxis.setAutoRanging(false);
			yAxis.setUpperBound(maxValue + 2);
			yAxis.setLowerBound(minValue - 2);
			yAxis.setTickUnit(1);
		}

		chart.getData().add(series);
		chart.setLegendVisible(false);

		VBox box = new VBox();
		VBox.setVgrow(chart, Priority.ALWAYS);
		box.setAlignment(Pos.CENTER);
		box.getChildren().add(chart);
		Scene scene = new Scene(box, 700, 700);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private static void convertCelsiusToF(ArrayList<WeatherValues> values) {

		for (WeatherValues wv : values) {
			double val = wv.getValue();
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(0);
			nf.setRoundingMode(RoundingMode.HALF_UP);

			val = (val * 9 / 5) + 32;
			String converted = nf.format(val);

			wv.setValue(Double.parseDouble(converted));
			wv.setIntValue(Integer.parseInt(converted));
		}
	}

	/**
	 * Responsible for reading the Grid Forecast json for the required location
	 * 
	 * @param list
	 * @param url  - url to the JSON file
	 */
	private static void jsonGridForecast(HashMap<String, WeatherGridInformation> weather, String url) {
		String json = readUrl(url);
		Gson gson = new Gson();
		// Create the overall object for the .json file
		JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

		// Create an object based on the .json properties object
		JsonObject properties = obj.getAsJsonObject("properties");

		String[] jsonObjs = { "temperature", "dewpoint", "maxTemperature", "minTemperature", "relativeHumidity",
				"apparentTemperature", "heatIndex", "windChill", "windDirection", "windSpeed", "windGust",
				"probabilityOfPrecipitation" };

		for (int i = 0; i < jsonObjs.length; i++) {
			JsonObject currObj = properties.getAsJsonObject(jsonObjs[i]);

			if (currObj != null) {

				WeatherGridInformation currWeather = gson.fromJson(currObj, WeatherGridInformation.class);

				if (currWeather.getUom().equals("wmoUnit:degC")) {
					convertCelsiusToF(currWeather.getValues());
				}

				weather.put(jsonObjs[i], currWeather);
			}
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

			File file = new File(urlString);

			BufferedReader br = new BufferedReader(new FileReader(file));

			String output = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				output += line + "\n";
			}

			//System.out.println(output);

			br.close();

			return output;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private void displayLabelForData(XYChart.Data<String, Number> data) {
		  final Node node = data.getNode();
		  final Text dataText = new Text(data.getYValue() + "");
		  node.parentProperty().addListener(new ChangeListener<Parent>() {
		    @Override public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
		      Group parentGroup = (Group) parent;
		      parentGroup.getChildren().add(dataText);
		    }
		  });

		  node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
		    @Override public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
		      dataText.setLayoutX(
		        Math.round(
		          bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
		        )
		      );
		      dataText.setLayoutY(
		        Math.round(
		          bounds.getMinY() - dataText.prefHeight(-1) * 0.5
		        )
		      );
		    }
		  });
		}

}
